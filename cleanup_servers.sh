#!/bin/sh

echo "Stoping servers and registries"
pkill -f "(rmiregistry)*(5000)"
pkill -f "(INF4410)*(server)"
