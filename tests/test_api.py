from __future__ import annotations

import base64
from pathlib import Path

from tests.conftest import make_image


def test_health(client):
    assert client.get("/health").json() == {"status": "UP", "database": "UP"}
    assert client.get("/actuator/health").status_code == 200


def test_swagger_available(client):
    assert client.get("/docs").status_code == 200
    assert "/v1/current" in client.get("/openapi.json").json()["paths"]


def test_upload_list_fetch_delete(client, upload, image_dir):
    data = make_image()
    res = upload(data=data, title="Beach day")
    assert res.status_code == 201, res.text
    photo = res.json()
    assert photo["title"] == "Beach day"
    assert photo["managed"] is True and photo["fileExists"] is True
    assert (photo["width"], photo["height"]) == (40, 30)
    path = Path(photo["filePath"])
    assert path.parent == image_dir

    listed = client.get("/v1/photos").json()
    assert [p["photoId"] for p in listed] == [photo["photoId"]]

    file_res = client.get(photo["url"])
    assert file_res.status_code == 200 and file_res.content == data

    assert client.delete(f"/v1/photos/{photo['photoId']}").status_code == 204
    assert not path.exists()
    assert client.get(f"/v1/photos/{photo['photoId']}").status_code == 404


def test_same_name_uploads_dont_overwrite(upload):
    a = upload(name="cat.png").json()
    b = upload(name="cat.png").json()
    assert a["filePath"] != b["filePath"]
    assert b["title"] == "cat-2.png"


def test_rejects_non_images(upload):
    assert upload(name="notes.txt", data=b"hello").status_code == 400
    assert upload(name="fake.jpg", data=b"not really a jpeg").status_code == 400


def test_rejects_too_large(upload):
    assert upload(name="big.jpg", data=b"x" * (1024 * 1024 + 10)).status_code == 413


def test_windows_path_in_filename_is_sanitised(upload, image_dir):
    photo = upload(name=r"C:\Users\Will\Pictures\my photo!.jpg").json()
    assert Path(photo["filePath"]) == image_dir / "my_photo.jpg"


def test_register_existing_file_is_not_deleted(client, tmp_path):
    outside = tmp_path / "elsewhere.jpg"
    outside.write_bytes(make_image())
    res = client.post("/v1/photos", json={"filePath": str(outside), "title": "Old one"})
    assert res.status_code == 201
    photo = res.json()
    assert photo["managed"] is False and photo["fileExists"] is True

    client.delete(f"/v1/photos/{photo['photoId']}")
    assert outside.exists()


def test_register_accepts_old_kotlin_payload(client):
    payload = {"photoId": -100, "filePath": "/nowhere/x.jpg", "title": "x", "created": "2022-01-01T00:00:00"}
    photo = client.post("/v1/photos", json=payload).json()
    assert photo["fileExists"] is False
    assert client.get(photo["url"]).status_code == 404


def test_rename(client, upload):
    pid = upload().json()["photoId"]
    assert client.patch(f"/v1/photos/{pid}", json={"title": "New name"}).json()["title"] == "New name"


def test_current_photo_lifecycle(client, upload):
    assert client.get("/v1/current").json() == {"photo": None, "updated": None}

    pid = upload().json()["photoId"]
    res = client.put("/v1/current", json={"photoId": pid}).json()
    assert res["photo"]["photoId"] == pid and res["updated"]
    assert client.get("/v1/current").json()["photo"]["photoId"] == pid

    assert client.delete("/v1/current").json()["photo"] is None
    assert client.put("/v1/current", json={"photoId": 9999}).status_code == 404


def test_deleting_current_photo_clears_it(client, upload):
    pid = upload().json()["photoId"]
    client.put("/v1/current", json={"photoId": pid})
    client.delete(f"/v1/photos/{pid}")
    assert client.get("/v1/current").json()["photo"] is None


def test_legacy_images_endpoints(client, image_dir):
    data = make_image("PNG")
    res = client.post(
        "/v1/images",
        json={"filePath": "/some/client/path/poster.png", "contentsAsBase64": base64.b64encode(data).decode()},
    )
    assert res.status_code == 200, res.text
    body = res.json()
    assert body["id"] > 0 and Path(body["filePath"]) == image_dir / "poster.png"
    assert client.delete(f"/v1/images/{body['id']}").status_code == 204
    assert not (image_dir / "poster.png").exists()


def test_legacy_images_bad_base64(client):
    res = client.post("/v1/images", json={"filePath": "a.jpg", "contentsAsBase64": "%%%"})
    assert res.status_code == 400


def test_delete_missing_photo_404(client):
    assert client.delete("/v1/photos/123").status_code == 404
