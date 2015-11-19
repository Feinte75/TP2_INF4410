import subprocess;
import timeit;
import os;
import time;
import sys

def run_repartiteur():
	subprocess.call("./client servers_distants.xml donnees-2317.txt secure", shell=True);

def run_server_setup(load = 1):
	return subprocess.Popen("./setup.sh "+str(load), shell=True, stdin=subprocess.PIPE, stdout=None);

setup = run_server_setup(load = 2);
time.sleep(5);
timer = timeit.Timer('run_repartiteur()', setup='from __main__ import run_repartiteur');
executionTimes = timer.repeat(repeat=10, number=1);
setup.stdin.write(bytes('\n', 'UTF-8'));
print(executionTimes);

