package ca.polymtl.inf4402.tp2.repartiteur;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class SecureStrategy implements RepartitionStrategy {

	// 
	static final int requestTimeout = 60;
	
	@Override
	public int computeResult(LinkedList<Operation> operations, HashMap<ServerInterface, ServerInfo> servers) {
		
		int totalResult = 0;
		HashMap<Request, Result> queries = new HashMap<Request, Result>();
		
		LinkedList<ServerInterface> availableServers = new LinkedList<ServerInterface>();
		availableServers.addAll(servers.keySet());

		do {
			int operationEndingIndex = 0;
			int nbOpsToSend = 0;

			if(availableServers.size() != 0 && operations.size() > 0)
				System.out.println("Remaining ops : " + operations.size());
			
			/**
			 * For each available server (Not already working on a request)
			 * Spawn a request thread to send operations and wait for the result
			 */
			Iterator<ServerInterface> availableServerIterator = availableServers.iterator();
			while (availableServerIterator.hasNext() && operations.size() > 0) {

				ServerInterface server = availableServerIterator.next();

				LinkedList<Operation> splittedOperations = new LinkedList<Operation>();
				Result requestResult = new Result();
				
				// Determine the number of operations we can send to the server
				nbOpsToSend = servers.get(server).getLoadEstimate();
				
				if (operations.size() > nbOpsToSend)
					// Enough operations to send
					operationEndingIndex = nbOpsToSend;
				else 
					// Not enough operations to max nbOps, take all remaining ones
					operationEndingIndex = operations.size();
				
				// Extract operations from the list
				splittedOperations.addAll(operations.subList(0, operationEndingIndex));
				
				// Remove operations from the list
				operations.removeAll(splittedOperations);

				Request request = new Request(servers.get(server), server, splittedOperations, requestResult);
				
				// Keep a reference to the request linked to the result
				queries.put(request, requestResult);
				request.start();

				System.out.println("Sending " + splittedOperations.size() + " operations to "
						+ servers.get(server).getServerIpPort());
				
				// Remove now active server from available server list
				availableServerIterator.remove(); 
			}

			/**
			 * For each active request check if they have ended. If so manage the result
			 */
			Iterator<Request> currentRequestsIterator = queries.keySet().iterator();

			while (currentRequestsIterator.hasNext()) {
				Request request = currentRequestsIterator.next();
				
				try {
					// Check if request is dead for few ms
					request.join(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				// If not dead increment request timer
				if (request.isAlive()) {

					request.updateTimer();
					
					if (request.getTimer() > requestTimeout) { 
						System.out.println(servers.get(request.getServer()).getServerIpPort() + " timed out. Replacing operations in the list.");
						operations.addAll(request.getOperations());
						currentRequestsIterator.remove();
						availableServers.remove(request.getServer());
					} 
					
				} else { // Request returned

					int result = queries.get(request).getResult();

					switch (result) {
					case -2: // Result not modified, server crashed
						System.out.println(servers.get(request.getServer()).getServerIpPort() + " -> crashed | Removing it from available servers");
						availableServers.remove(request.getServer());
						operations.addAll(request.getOperations());
						currentRequestsIterator.remove();
						break;
					case -1: // Server overloaded, put operations back to the list and reduce server estimated load
						System.out.println("Failure : " + servers.get(request.getServer()).getServerIpPort() + " -> overloaded | Reducing estimated operations load");
						servers.get(request.getServer()).decreaseLoadEstimate();
						operations.addAll(request.getOperations());
						currentRequestsIterator.remove();
						availableServers.add(request.getServer());
						break;
					default: // Request successful, sum total and double server estimated load
						System.out.println("Success : " + servers.get(request.getServer()).getServerIpPort() + " -> returned value : " + result + "  | Increase estimated operations load");
						servers.get(request.getServer()).increaseLoadEstimate();
						totalResult += result;
						availableServers.add(request.getServer());
						currentRequestsIterator.remove();
						break;
					}
				}
			}
			
			// Still operations to solve
			if (operations.size() == 0 && queries.size() == 0) { 
				break;
			}

		} while (true); // Continue sending operations until there are no more
		
		totalResult %= 5000;
		return totalResult;
	}

}
