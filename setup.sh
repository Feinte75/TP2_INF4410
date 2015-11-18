#!/bin/sh

echo "starting"
cd bin
rmiregistry 50001 &
rmiregistry 50002 &
rmiregistry 50003 &
cd ..
./server 50001 2 0 &
./server 50002 4 0 &
./server 50003 8 0 &

echo "3 Servers started on ports 50001 to 50003, press enter to exit and kill the servers"

read dummy

echo "stoping"
pkill -f "(rmiregistry)*(5000)"
pkill -f "(INF4410)*(server)"

