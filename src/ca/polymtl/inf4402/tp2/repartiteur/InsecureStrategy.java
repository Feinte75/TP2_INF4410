package ca.polymtl.inf4402.tp2.repartiteur;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class InsecureStrategy implements RepartitionStrategy {

	static final int requestTimeout = 60;
	
	@Override
	public int computeResult(LinkedList<Operation> operations, HashMap<ServerInterface, ServerInfo> servers) {
		
		int totalResult = 0;
		int[] intermediateResults;
		int[] estimatedLoad;
		boolean error; 
		HashMap<Request, Result> queries = new HashMap<Request, Result>();
		
		LinkedList<ServerInterface> availableServers = new LinkedList<ServerInterface>();
		availableServers.addAll(servers.keySet());
		
		intermediateResults = new int[availableServers.size()];
		
		LinkedList<Operation> splittedOperations = null;
		
		do {
			int operationEndingIndex = 0;
			int nbOpsToSend = 1;

			// Send nbOperations so that the less powerful server can handle it
			int minOps = servers.get(availableServers.get(0)).getLoadEstimate();
			for(int i = 1; i < availableServers.size(); i++) {
				int l = servers.get(availableServers.get(i)).getLoadEstimate();
				if( l < minOps)
					minOps = l;
			}
			
			nbOpsToSend = minOps;
			
			error = false;
			
			if(availableServers.size() != 0 && operations.size() > 0)
				System.out.println("Remaining ops : " + operations.size());
			
			/**
			 * For each available server (Not already working on a request)
			 * Spawn a request thread to send operations and wait for the result
			 */
			Iterator<ServerInterface> availableServerIterator = availableServers.iterator();
			while (availableServerIterator.hasNext() && operations.size() > 0) {

				ServerInterface server = availableServerIterator.next();

				splittedOperations = new LinkedList<Operation>();
				Result requestResult = new Result();
				
				// Determine the number of operations we can send to the server
				//nbOpsToSend = servers.get(server).getLoadEstimate();
				
				if (operations.size() > nbOpsToSend)
					// Enough operations to send
					operationEndingIndex = nbOpsToSend;
				else 
					// Not enough operations to max nbOps, take all remaining ones
					operationEndingIndex = operations.size();
				
				// Extract operations from the list
				splittedOperations.addAll(operations.subList(0, operationEndingIndex));
				
				// Remove operations from the list
				//operations.removeAll(splittedOperations);

				Request request = new Request(servers.get(server), server, splittedOperations, requestResult);
				
				// Keep a reference to the request linked to the result
				queries.put(request, requestResult);
				request.start();

				System.out.println("Sending " + splittedOperations.size() + " operations to "
						+ servers.get(server).getServerIpPort());
				
				// Remove now active server from available server list
				availableServerIterator.remove(); 
			}
			operations.removeAll(splittedOperations);
			/**
			 * For each active request check if they have ended. If so manage the result
			 */
			Iterator<Request> currentRequestsIterator = queries.keySet().iterator();
			int i = 0;
			// Wait for all requests to end
			while (currentRequestsIterator.hasNext()) {
				Request request = currentRequestsIterator.next();
				
				// Wait for the request to end
				try {					
					request.join();
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
						System.out.println("Failure : " + servers.get(request.getServer()).getServerIpPort() + " -> overloaded | Decreasing operations load");
						servers.get(request.getServer()).decreaseLoadEstimate();
						error = true;
						currentRequestsIterator.remove();
						availableServers.add(request.getServer());
						break;
					default: // Request successful, sum total and double server estimated load
						System.out.println("Success : " + servers.get(request.getServer()).getServerIpPort() + " -> returned value : " + result + "  | Increase operations load");
						servers.get(request.getServer()).increaseLoadEstimate();
						intermediateResults[i] = result;
						availableServers.add(request.getServer());
						currentRequestsIterator.remove();
						break;
					}
				}
				i++;
			}
			
			if(error) {
				operations.addAll(splittedOperations);
				continue;
			}
			
			HashMap<Integer, Integer> frequency = new HashMap<Integer, Integer>();
			boolean consensus = false;
			System.out.println("Server majority is : " + Math.round(intermediateResults.length / 2f));
			
			for (int j = 0; j < intermediateResults.length; j++) {	
				
				if(frequency.containsKey(intermediateResults[j]))
					frequency.put(intermediateResults[j], frequency.get(intermediateResults[j]) + 1 );
				else
					frequency.put(intermediateResults[j], 1);
				
				if(frequency.get(intermediateResults[j]) >= Math.round(intermediateResults.length /2f)) {
					totalResult += intermediateResults[j];
					System.out.println("Consensus on " + intermediateResults[j] + " as a good result");
					operations.removeAll(splittedOperations);
					consensus = true;
					break;
				}
			}
			
			if (!consensus) {
				System.out.println("Error in insecure consensus, a server is misconfigured or down");
				System.exit(-1);
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
