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

import javax.xml.bind.JAXBException;

import ca.polymtl.inf4402.tp1.shared.Operation;
import ca.polymtl.inf4402.tp1.shared.ServerInterface;

public class Client {
	
	private LinkedList<ServerInterface> serverStubs;
	private LinkedList<ServerInfo> serverInfos;
	LinkedList<Operation> operations;
	private InputParser inputParser;
	
	public Client(String distantServerHostname, String serversPath, String operationsPath) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}

		inputParser = new InputParser();
		serverInfos = inputParser.getServers(serversPath);
		operations = inputParser.getOperations(operationsPath);
		
		serverStubs = new LinkedList<ServerInterface>();
		
		// Load all server stubs from ip / port
		for(ServerInfo serverInfo : serverInfos) {
			serverStubs.add(loadServerStub(serverInfo.getName(), serverInfo.getPort()));
		}
	}

	private void run() throws IOException {
		
	}

	private ServerInterface loadServerStub(String hostname, int port) {
		ServerInterface stub = null;

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
		String distantHostname = null;
		String serversPath = null;
		String operationsPath = null;
		
		if (args.length > 2) {
			distantHostname = args[0];
			serversPath = args[1];
			operationsPath = args[2];
		}

		Client client = new Client(distantHostname, serversPath, operationsPath);
		
		client.run();
	}
}
