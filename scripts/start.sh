#!/bin/bash
java -Dspring.profiles.active=$DEPLOYMENT_ENV -jar /opt/gallery.jar