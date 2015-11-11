package ca.polymtl.inf4402.tp1.client;

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
import java.util.LinkedList;

import ca.polymtl.inf4402.tp1.shared.Operation;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {
	
	private ServerInterface serverStub = null;
	private LinkedList<ServerInfo> secureServerInfos;
	private LinkedList<ServerInfo> insecureServerInfos;
	private InputParser inputParser;
	
	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		serverStub = loadServerStub("127.0.0.1");

		if (distantServerHostname != null) {
			serverStub = loadServerStub(distantServerHostname);
		}
		
		secureServerInfos = new LinkedList<ServerInfo>();
		insecureServerInfos = new LinkedList<ServerInfo>();
		inputParser = new InputParser();
	}

	private void run() throws IOException {
		
		Operation op = new Operation("fib", 4);
		LinkedList<Operation> listOp = new LinkedList<Operation>();
		listOp.add(op);
		
		

		
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
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

	// int result = distantServerStub.execute(4, 7);
	/*
	 * fonction pour lire les op du fichier
	 */

	private String readOp(String fileName) throws FileNotFoundException  {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String everything = null;
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();

			// } finally {
			br.close();

		} catch (IOException e) {

		}
		return everything;
	}

	public static void main(String[] args) throws IOException {
		String distantHostname = null;

		if (args.length > 0) {
			distantHostname = args[0];
		}

		Client client = new Client(distantHostname);
		
			client.run();
		
	}
}
