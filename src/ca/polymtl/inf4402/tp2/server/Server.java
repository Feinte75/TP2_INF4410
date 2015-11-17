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
	
	public static void main(String[] args) {
		int port = 0;
		int load = 0;
		
		if (args.length > 1) {
			port = Integer.parseInt(args[0]);
			load = Integer.parseInt(args[1]);
		}
		else {
			System.err.println("Not enough parameters. Please specify \n"
					+ "<Server Port> and <Max Load>");
		}
		
		System.out.println(port);
		Server server = new Server(port, load);
		server.run();
	}

	public Server(int port, int load) {
		super();
		serverPort = port;
		serverLoad = load;
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

	@Override
	public int doOperation(LinkedList<Operation> operations)
			throws RemoteException {
		int sum = 0;
		
		/**
		 * Probability of rejection if number of operations received is over server load
		 */
		float rejectionRate = (((float)operations.size() - serverLoad) / (9 * serverLoad)) * 100;
		float rand = (float) (Math.random() * 100);
		System.out.println("Rejection rate : " + rejectionRate + "   t : " + rand);
		
		if(rand < rejectionRate)
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
		
		System.out.println("Finished treatment with : " + sum);
		return sum;
	}
}
