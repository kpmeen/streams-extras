#!/bin/bash

mkdir /root/.bintray
echo "realm = Bintray API Realm" >> /root/.bintray/.credentials
echo "host = api.bintray.com" >> /root/.bintray/.credentials
echo "user = $BINTRAY_USER" >> /root/.bintray/.credentials
echo "password = $BINTRAY_TOKEN" >> /root/.bintray/.credentials