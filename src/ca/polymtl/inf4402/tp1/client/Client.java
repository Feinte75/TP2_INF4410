package ca.polymtl.inf4402.tp1.client;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
	public static void main(String[] args) {
		String distantHostname = null;

		if (args.length > 0) {
			distantHostname = args[0];
		}

		Client client = new Client(distantHostname);
		client.run();
	}

									

	private ServerInterface serverStub = null;

	public Client(String distantServerHostname) {
		super();

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new SecurityManager());
		}
		serverStub = loadServerStub("127.0.0.1");
		

		if (distantServerHostname != null) {
			serverStub = loadServerStub(distantServerHostname);
		}
	}

	private void run(){
		//try{
			//readOp("donnees-2317.txt");
		Operation op = new Operation("fib", 4);
		LinkedList<Operation> listOp = new LinkedList<Operation>();
		listOp.add(op);
		
		
		try {
			System.out.println(serverStub.doOperation(listOp));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	

			
			
		//}catch(IOException e) {
			
		//}
		
	}

	private ServerInterface loadServerStub(String hostname) {
		ServerInterface stub = null;

		try {
			Registry registry = LocateRegistry.getRegistry(hostname);
			stub = (ServerInterface) registry.lookup("server");
		} catch (NotBoundException e) {
			System.out.println("Erreur: Le nom " + e.getMessage()
					+ " n'est pas défini dans le registre.");
		} catch (AccessException e) {
			System.out.println("Erreur: " + e.getMessage());
		} catch (RemoteException e) {
			System.out.println("Erreur: " + e.getMessage());
		}

		return stub;
	}

	
	

	
	//int result = distantServerStub.execute(4, 7);
	/*
	 * fonction pour lire les op du fichier 
	 */
	
	private String readOp(String fileName) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String everything =null ;
		try {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		     everything = sb.toString();
		    
		//} finally {
		    br.close();
		    
		}catch(IOException e) {
			
		}
		return everything;
		
	}
}
