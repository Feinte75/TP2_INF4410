#/bin/sh

echo "Starting server"

cd bin
rmiregistry $1 &
cd ..

./server $* &

read dummy

pkill -f "(rmiregistry)*($1)"



