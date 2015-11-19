import subprocess;
import timeit;
import os;
import time;

def run_repartiteur():
    subprocess.call("./client servers_distants.xml donnees-2317.txt secure", shell=True);

def run_server_setup(load = 1):
    return subprocess.Popen("./setup.sh "+str(load), shell=True, stdin=subprocess.PIPE);


'''setup = run_server_setup();'''
'''time.sleep(30)'''
timer = timeit.Timer('run_repartiteur()', setup='from __main__ import run_repartiteur');
executionTimes = timer.repeat(repeat=10, number=1);

print(executionTimes);

setup.stdin.write(bytes(os.linesep, 'UTF-8'));

