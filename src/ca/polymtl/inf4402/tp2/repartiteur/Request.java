package ca.polymtl.inf4402.tp2.repartiteur;

import java.rmi.RemoteException;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public class Request extends Thread {
	

	private ServerInfo serverInfo;
	private int timer;
	private LinkedList<Operation> operations;
	private Result result;
	private ServerInterface server;
	
	public Request(ServerInfo serverInfo, int timer, ServerInterface server, LinkedList<Operation> operations, Result result) {
		super();
		this.serverInfo = serverInfo;
		this.timer = timer;
		this.server = server;
		this.operations = operations;
		this.result = result;
	}

	@Override
	public void run() {
		try {
			result.setResult(server.doOperation(operations));
		} catch (RemoteException e) {
			result.setResult(-1);
			e.printStackTrace();
		}
	}
	
	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	public LinkedList<Operation> getOperations() {
		return operations;
	}

	public void addOperation(Operation operation) {
		this.operations.add(operation);
	}
}
