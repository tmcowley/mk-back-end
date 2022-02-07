#!/bin/bash

# unmake
./unmake

# launch back-end
cd app-server
# nohup mvn package exec:exec &
nohup mvn exec:exec &

sleep 2

cd ../

# launch the front-end 
# note localhost is opened automatically by npm start
cd front-end/
npm start

