#!/bin/sh

q1=$1;

echo "Starting servers and registries"

cd bin
rmiregistry 50007 &
rmiregistry 50008 &
rmiregistry 50009 &
cd ..
./server 50007 $q1 0 &
./server 50008 "$((2 * $q1))" 0 &
./server 50009 "$((4 * $q1))" 0 &

echo "3 Servers started on ports 50007 to 50009 with loads [$q1, 2 * $q1, 4 * $q1], press enter to exit and kill the servers"

read dummy

./cleanup_servers.sh
