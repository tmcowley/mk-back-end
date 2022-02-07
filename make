#!/bin/bash

# launch back-end
cd app-server
nohup mvn package exec:exec &

cd ../

cd front-end/
npm start

