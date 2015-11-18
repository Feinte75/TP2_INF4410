package ca.polymtl.inf4402.tp2.repartiteur;

import java.util.HashMap;
import java.util.LinkedList;

import ca.polymtl.inf4402.tp2.shared.Operation;
import ca.polymtl.inf4402.tp2.shared.ServerInterface;

public interface RepartitionStrategy {
	int computeResult(LinkedList<Operation> operations, HashMap<ServerInterface, ServerInfo> servers);
}

