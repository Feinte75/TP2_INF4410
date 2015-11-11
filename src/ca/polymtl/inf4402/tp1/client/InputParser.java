package ca.polymtl.inf4402.tp1.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


import ca.polymtl.inf4402.tp1.shared.Operation;

public class InputParser {
		
	public LinkedList<ServerInfo> getServers(String serversPath)throws JAXBException {
		
		// création d'un contexte JAXB sur la classe ServerInfos
	    JAXBContext context = JAXBContext.newInstance(ServerInfos.class) ;
	    
	     // création d'un unmarshaller
	    Unmarshaller unmarshaller = context.createUnmarshaller() ;
	    ServerInfos serverinfos = (ServerInfos)unmarshaller.unmarshal(new File(serversPath)) ;
	    	    
		return serverinfos.getServerInfos();
	}
	
	public LinkedList<Operation> getOperations(String operationPath) throws IOException {
		
		LinkedList<Operation> operations = new LinkedList<Operation>();
		BufferedReader br = new BufferedReader(new FileReader(operationPath));
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
		return operations;
	}
	
}
