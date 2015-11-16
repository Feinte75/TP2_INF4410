#!/bin/sh

echo "startin"
cd bin
rmiregistry 50001 &
rmiregistry 50002 &
cd ..
./server 50001 2 &
./server 50002 5 &

echo "Servers started, press enter to exit and kill the servers"

read dummy

echo "stoping"
pkill -f "(rmiregistry)*(5000)"
pkill -f "(INF4410)*(server)"

