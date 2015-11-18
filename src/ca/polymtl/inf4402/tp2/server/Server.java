package ca.polymtl.inf4402.tp2.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class Server implements ServerInterface {
	
	private int serverPort;
	private float serverLoad;
	private int serverMaliciousness;

	public Server(int port, int load, int maliciousness) {
		super();
		serverPort = port;
		serverLoad = load;
		serverMaliciousness = maliciousness;
	}

	private void run() {
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry(null, serverPort);
			registry.rebind("server", stub);
			System.out.println("Server ready.");
			
		} catch (ConnectException e) {
			System.err
					.println("Impossible de se connecter au registre RMI. Est-ce que rmiregistry est lancé ?");
			System.err.println();
			System.err.println("Erreur: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Erreur: " + e.getMessage());
		}
	}
	
	/**
	 * Probability of rejection if number of operations received is over server load
	 */
	private boolean rejectRequest(int nbOperations) {
		
		float rejectionRate = (((float)nbOperations - serverLoad) / (9 * serverLoad)) * 100;
		float rand = (float) (Math.random() * 100);
		System.out.println("Rejection rate : " + rejectionRate + "   t : " + rand);
		
		return rand < rejectionRate;
	}

	private boolean fakeResult() {
		
		return (Math.random() * 100) < serverMaliciousness;
	}
	
	@Override
	public int doOperation(LinkedList<Operation> operations)
			throws RemoteException {
		
		int sum = 0;
		
		if(rejectRequest(operations.size()))
			return -1;
			  
		for(Operation operation : operations ){ 
			
			System.out.println("Operation n°" + operations.indexOf(operation) + " : " + operation.getNom() + "  with : " + operation.getOperande());
			
			if(operation.getNom().equals("fib")){
				sum += Operations.fib(operation.getOperande());
				sum %= 5000;
			}
			else {
				sum += Operations.prime(operation.getOperande());
				sum %= 5000;
			}
		}
		
		if(fakeResult()) {
			sum += (int)(Math.random() * 100 );
			sum %= 5000;
		}
			
		System.out.println("Finished treatment with : " + sum);
		return sum;
	}
	
	
	public static void main(String[] args) {
		int port = 0;
		int load = 0;
		int maliciousness = 0;
		
		if (args.length > 2) {
			port = Integer.parseInt(args[0]);
			load = Integer.parseInt(args[1]);
			maliciousness = Integer.parseInt(args[2]);
		}
		else {
			System.err.println("Not enough parameters. Please specify \n"
					+ "./server <Server Port [50000 - 50050]> <Server Load [0 - max Integer]> <Maliciousness percentage [0 - 100]>");
			System.exit(-1);
		}
		
		System.out.println(port);
		Server server = new Server(port, load, maliciousness);
		server.run();
	}
}
