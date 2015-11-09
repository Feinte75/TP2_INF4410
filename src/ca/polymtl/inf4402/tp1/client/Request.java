package ca.polymtl.inf4402.tp1.client;

import java.util.LinkedList;

import ca.polymtl.inf4402.tp1.shared.Operation;

public class Request {
	
	private ServerInfo serverInfo;
	private int timer;
	private LinkedList<Operation> operations;
	private int retour;
	
	public Request(ServerInfo serverInfo, int timer) {
		super();
		this.serverInfo = serverInfo;
		this.timer = timer;
		this.operations = new LinkedList<Operation>();
		this.retour = -1;
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

	public int getRetour() {
		return retour;
	}

	public void setRetour(int retour) {
		this.retour = retour;
	}
}
