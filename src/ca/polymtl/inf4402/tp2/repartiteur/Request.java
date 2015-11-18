package ca.polymtl.inf4402.tp2.repartiteur;

import java.rmi.RemoteException;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class Request extends Thread {
	

	private ServerInfo serverInfo;
	private long timer;
	private LinkedList<Operation> operations;
	private Result result;
	private ServerInterface server;
	private long launchTime;
	
	public Request(ServerInfo serverInfo, ServerInterface server, LinkedList<Operation> operations, Result result) {
		super();
		this.serverInfo = serverInfo;
		this.timer = 0;
		this.launchTime = System.currentTimeMillis();
		this.server = server;
		this.operations = operations;
		this.result = result;
	}

	@Override
	public void run() {
		try {
			result.setResult(server.doOperation(operations));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public long getTimer() {
		return timer;
	}

	public void updateTimer() {
		timer = (System.currentTimeMillis() - launchTime) / 1000;
	}

	public LinkedList<Operation> getOperations() {
		return operations;
	}

	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}
	
	public ServerInterface getServer() {
		return server;
	}

	public void setServer(ServerInterface server) {
		this.server = server;
	}
}
