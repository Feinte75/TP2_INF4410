package ca.polymtl.inf4402.tp2.repartiteur;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.bind.JAXBException;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class Client {
	
	private LinkedList<ServerInterface> serverStubs;
	private LinkedList<ServerInfo> serverInfos;
	LinkedList<Operation> operations;
	private InputParser inputParser;
	private HashMap<Request, Result> queries;
	
	public Client(String serversPath, String operationsPath) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		System.out.println("Server path : "+ serversPath + "   Operations path : "+ operationsPath);
		inputParser = new InputParser();
		serverInfos = inputParser.getServers(serversPath);
		operations = inputParser.getOperations(operationsPath);
		
		serverStubs = new LinkedList<ServerInterface>();
		
		// Load all server stubs from ip / port
		for(ServerInfo serverInfo : serverInfos) {
			serverStubs.add(loadServerStub(serverInfo.getIp(), serverInfo.getPort()));
		}
	}

	/**
	 * Utilisation en mode sécurisé
	 * Divise les operations en plusieurs listes répartis ensuite
	 * sur les serveurs. Chaque requete est executée par un thread
	 * 
	 * @throws IOException
	 */
	private void run() throws IOException {
		int totalResult = 0;
		int nbServers = serverStubs.size();
		boolean work = false;
		
		
		
		do {
			int i = 0;
			int operationStartingIndex = 0;
			int operationEndingIndex = 0;
			int opsPerServer = 10;
			
			work = false;
			queries = new HashMap<Request, Result>();
			
			/**
			 * Generate a request and spawn a thread to send it
			 */
			for(ServerInterface server : serverStubs) {
				
				LinkedList<Operation> splittedOperations = new LinkedList<Operation>();
				Result requestResult = new Result();
				
				if(operations.size() < operationStartingIndex)
					break;				
				else if (operations.size() > (operationStartingIndex  + opsPerServer))
					operationEndingIndex = operationStartingIndex + opsPerServer;
				else // Prevent overflow in operation range
					operationEndingIndex = operations.size();
				
				splittedOperations.addAll(operations.subList(operationStartingIndex, operationEndingIndex));
				
				Request request = new Request(serverInfos.get(i), 0, server, splittedOperations, requestResult);
				queries.put(request, requestResult);
				request.start();
				
				operationStartingIndex += opsPerServer;
				i++;
			}
			
			/**
			 * Wait for all threads to finish and get the result
			 */
			for(Request request : queries.keySet()) {
				try {
					request.join();
					int result = queries.get(request).getResult();
					System.out.println("Remaining ops : " + operations.size());
					
					if(result == -1) { // Server overloaded, resend request
						System.out.println("Server overloaded, resend needed");
						work = true;
					}
					else { // Request successful, remove operations from list
						operations.removeAll(request.getOperations());
						totalResult += result;
						
						if(operations.size() != 0) { // Still operations to solve
							work = true;
						}
					}
						
					System.out.println("Return value : " + queries.get(request).getResult());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
		} while(work); // Continue sending operations until there are no more
		
		System.out.println("Result is : " + totalResult);
	}

	private ServerInterface loadServerStub(String hostname, int port) {
		ServerInterface stub = null;
		System.out.println("Loading new stub : hostname -> "+ hostname + ":"+port);
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
		
		if (args.length > 1) {
			serversPath = args[0];
			operationsPath = args[1];
		}

		Client client = new Client(serversPath, operationsPath);
		
		client.run();
	}
}
