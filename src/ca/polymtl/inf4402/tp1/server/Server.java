package ca.polymtl.inf4402.tp1.server;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp1.shared.Operation;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Server implements ServerInterface {
	
	public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}

	public Server() {
		super();
	}

	private void run() {
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		try {
			ServerInterface stub = (ServerInterface) UnicastRemoteObject
					.exportObject(this, 0);

			Registry registry = LocateRegistry.getRegistry();
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
		int somme = 0;
		
		for(Operation operation : operations ){
			if(operation.getNom().equals("fib")){
				somme = Operations.fib(operation.getOperande());
			}
			else {
				somme = Operations.prime(operation.getOperande());
			}
		}
		
		return somme;
	}
}
