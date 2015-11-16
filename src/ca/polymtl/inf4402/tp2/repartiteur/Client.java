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
	
	private LinkedList<ServerInterface> availableServers;
	private LinkedList<ServerInfo> serverInfos;
	private HashMap<ServerInterface, ServerInfo> servers;
	
	LinkedList<Operation> operations;
	private InputParser inputParser;
	private HashMap<Request, Result> queries;
	
	private static float requestTimeOut = 60f;
	
	public Client(String serversPath, String operationsPath) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		System.out.println("Server path : "+ serversPath + "   Operations path : "+ operationsPath);
		inputParser = new InputParser();
		serverInfos = inputParser.getServers(serversPath);
		operations = inputParser.getOperations(operationsPath);
		
		availableServers = new LinkedList<ServerInterface>();
		
		servers = new HashMap<ServerInterface, ServerInfo>();
		// Load all server stubs from ip / port
		for(ServerInfo serverInfo : serverInfos) {
			serverInfo.setServerStub(loadServerStub(serverInfo.getIp(), serverInfo.getPort()));
			availableServers.add(serverInfo.getServerStub());
			servers.put(serverInfo.getServerStub(), serverInfo);
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
		int nbServers = availableServers.size();
		boolean work = false;
		queries = new HashMap<Request, Result>();
		
		do {
			int i = 0;
			int operationStartingIndex = 0;
			int operationEndingIndex = 0;
			int opsPerServer = 10;
			
			work = false;
			
			Iterator<ServerInterface> availableServerIterator = availableServers.iterator();
			/**
			 * Generate a request and spawn a thread to send it
			 */
			while(availableServerIterator.hasNext()) {
				
				ServerInterface server = availableServerIterator.next();
				
				LinkedList<Operation> splittedOperations = new LinkedList<Operation>();
				Result requestResult = new Result();
				
				if(operations.size() < operationStartingIndex)
					break;				
				else if (operations.size() > (operationStartingIndex  + opsPerServer))
					operationEndingIndex = operationStartingIndex + opsPerServer;
				else // Prevent overflow in operation range
					operationEndingIndex = operations.size();
				
				splittedOperations.addAll(operations.subList(operationStartingIndex, operationEndingIndex));
				operations.removeAll(splittedOperations);
				
				Request request = new Request(serverInfos.get(i), server, splittedOperations, requestResult);
				queries.put(request, requestResult);
				request.start();
				
				System.out.println("Sending " + splittedOperations.size() + " operations to " + servers.get(server).getIp() + ":" + servers.get(server).getPort());
				availableServerIterator.remove();
				operationStartingIndex += opsPerServer;
				i++;
			}
			
			/**
			 * Wait for all threads to finish and get the result
			 */
			Iterator<Request> currentRequestsIterator = queries.keySet().iterator();
			
			while(currentRequestsIterator.hasNext()) {
				Request request = currentRequestsIterator.next();
				try {
					// Wait a bit for request to die
					request.join(100);
					
					// If not dead increment timer 
					if(request.isAlive()) {
						//System.out.println("Request alive ! with : " + request.getTimer());
						if(request.getTimer() > requestTimeOut) { // Check if server is locked
							operations.addAll(request.getOperations());
							currentRequestsIterator.remove();
							availableServers.remove(request.getServer());
						}
						else {
							request.updateTimer();
						}
							
					} else { // Take the result and put the server back to the pool
						
						int result = queries.get(request).getResult();
						System.out.println("Remaining ops : " + operations.size());
						
						switch (result) {
						case -2 : // Server crashed
							availableServers.remove(request.getServer());
							operations.addAll(request.getOperations());
							currentRequestsIterator.remove();
							break;
						case -1 : // Or Server overloaded, resend operations
							operations.addAll(request.getOperations());
							currentRequestsIterator.remove();
							availableServers.add(request.getServer());
							System.out.println("Server overloaded, resend needed");
							
							break;
						default : // Request successful, remove operations from list
							totalResult += result;
							System.out.println("Server " + servers.get(request.getServer()).getIp()+":"+ servers.get(request.getServer()).getPort() + "  returned value : " + result);
							availableServers.add(request.getServer());
							currentRequestsIterator.remove();
							break;
						}
					}
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(operations.size() != 0 || queries.size() != 0) { // Still operations to solve
				work = true;
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
