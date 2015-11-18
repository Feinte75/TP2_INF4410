#!/bin/sh

q1=$1;

echo "Starting servers and registries"

cd bin
rmiregistry 50001 &
rmiregistry 50002 &
rmiregistry 50003 &
cd ..
./server 50001 $q1 0 &
./server 50002 "$((2 * $q1))" 0 &
./server 50003 "$((4 * $q1))" 0 &

echo "3 Servers started on ports 50001 to 50003 with loads [$q1, 2 * $q1, 4 * $q1], press enter to exit and kill the servers"

read dummy

echo "Stoping servers and registries"
pkill -f "(rmiregistry)*(5000)"
pkill -f "(INF4410)*(server)"

