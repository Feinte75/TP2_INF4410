#!/bin/sh

cd bin
screen -d ./rmiregistry 50001 &
screen -d ./rmiregistry 50002 &


