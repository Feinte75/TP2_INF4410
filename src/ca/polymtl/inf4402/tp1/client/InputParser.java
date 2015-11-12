package ca.polymtl.inf4402.tp1.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import ca.polymtl.inf4402.tp1.shared.Operation;

public class InputParser {
		
	public LinkedList<ServerInfo> getServers(String serversPath) {
		
	    JAXBContext context = null;
	    ServerInfos serverinfos = null;
	    
		try {
			// création d'un contexte JAXB sur la classe ServerInfos
			context = JAXBContext.newInstance(ServerInfos.class);
			
		     // création d'un unmarshaller
		    Unmarshaller unmarshaller = context.createUnmarshaller() ;
		    serverinfos = (ServerInfos)unmarshaller.unmarshal(new File(serversPath)) ;
		} catch (JAXBException e) {
			System.err.println("Get servers from xml error");
			e.printStackTrace();
		}
   
		return serverinfos.getServerInfos();
	}
	
	public LinkedList<Operation> getOperations(String operationPath) {
		
		LinkedList<Operation> operations = new LinkedList<Operation>();
		BufferedReader br;
		
		try {
			br = new BufferedReader(new FileReader(operationPath));
			String line = br.readLine();

		    while (line != null) {
		    	int indexOfSpace = line.indexOf(" ");
		    	String number = line.substring(indexOfSpace).trim();
		    	String nom = line.substring(0,indexOfSpace).trim();
		        int operande = Integer.parseInt(number);
		      
		        Operation operation = new Operation(nom, operande);		        
		        operations.add(operation);
		    	
		    	line = br.readLine();
		    }
		 br.close();
		 
		} catch (Exception e) {
			System.err.println("Get operations from file error");
			e.printStackTrace();
		}
		 
		return operations;
	}
	
}
