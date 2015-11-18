package ca.polymtl.inf4402.tp2.repartiteur;

import java.io.IOException;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class Client {

	private RepartitionStrategy repartitionStrategy;
	
	private HashMap<ServerInterface, ServerInfo> servers;
	LinkedList<Operation> operations;
	
	private InputParser inputParser;

	public Client(String serversPath, String operationsPath, boolean secureRepartitionMode) {
		
		LinkedList<ServerInfo> serverInfos;
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		System.out.println("Server path : " + serversPath + "   Operations path : " + operationsPath);
		inputParser = new InputParser();
		serverInfos = inputParser.getServers(serversPath);
		operations = inputParser.getOperations(operationsPath);


		servers = new HashMap<ServerInterface, ServerInfo>();
		// Load all server stubs from ip / port
		for (ServerInfo serverInfo : serverInfos) {
			serverInfo.setServerStub(loadServerStub(serverInfo.getIp(), serverInfo.getPort()));
			servers.put(serverInfo.getServerStub(), serverInfo);
		}
		
		if(secureRepartitionMode) {
			repartitionStrategy = new SecureStrategy();	
		}
		else {
			repartitionStrategy = new InsecureStrategy();
		}
	}

	/**
	 * Utilisation en mode sécurisé Divise les operations en plusieurs listes
	 * répartis ensuite sur les serveurs. Chaque requete est executée par un
	 * thread
	 * 
	 * @throws IOException
	 */
	private void run() throws IOException {
		int totalResult = 0;
		
		totalResult = repartitionStrategy.computeResult(operations, servers);
		
		System.out.println("Final result is : " + totalResult);
		System.out.println("Ending client ...");
	}

	
	
	private ServerInterface loadServerStub(String hostname, int port) {
		ServerInterface stub = null;
		System.out.println("Loading new stub : hostname -> " + hostname + ":" + port);
		try {
			Registry registry = LocateRegistry.getRegistry(hostname, port);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom " + e.getMessage() + " n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	public static void main(String[] args) throws IOException {
		String serversPath = null;
		String operationsPath = null;
		String mode = null;
		
		if (args.length > 2) {
			serversPath = args[0];
			operationsPath = args[1];
			mode = args[2];
		}
		else {
			System.err.println("Not enough parameters. Please specify \n"
					+ "./client <Servers XML path> <Operations file path> <Repartition mode [secure - insecure]>");
			System.exit(-1);
		}

		Client client = new Client(serversPath, operationsPath, mode.equals("secure"));

		client.run();
	}
}
