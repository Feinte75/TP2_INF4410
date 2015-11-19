Test simple (Secure ou insecure):

- Lancer setup.sh avec le parametre q1. Cela lancera en local 3 serveurs (registre rmi et serveurs de calculs) sur les ports 50007 à 50009

$ ./setup.sh 5

q1 = 5 ; q2 = 10 ; q3 = 20

(Pour fermer les serveurs, appuyer sur entrée ce qui tuera les processus et terminera le script)

- Lancer ensuite client

$ ./client <path_to_server_description.xml> <path_to_operations_file.txt> <secure | insecure>

Il est possible d'ajouter la commande time devant afin d'avoir le temps d'execution de la commande
$ time ./client ...

Deux fichiers xml sont donnés : serveurs_locaux.xml et serveurs_distants.xml, ils sont préremplis avec des informations suffisantes pour faire rouler le programme et peuvent être simplement modifiés.


Tests avancées :

- Le script test.py nous a permis d'executer de multiples fois des commandes similaires afin d'obtenir des moyennes. Il peut mettre en place les serveurs locaux (en appellant setup.sh) et appeller ensuite de multiple fois le client en mesurant le temps d'execution. 
Pour les tests distants les serveurs sont lancés en se connectant en ssh et en lancant le script ./single_server_setup <port> 
On lance ensuite le client avec ./client ... et le fichier serveurs_distants.xml




