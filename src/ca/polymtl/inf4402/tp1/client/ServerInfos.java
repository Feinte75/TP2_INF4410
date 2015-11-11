package ca.polymtl.inf4402.tp1.client;

import java.util.LinkedList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="ServerInfos")
public class ServerInfos {
	private LinkedList<ServerInfo> serverInfos;
	
	@XmlElement(name = "ServerInfo")
	public LinkedList<ServerInfo> getServerInfos() {
		return serverInfos;
	}

	public void setServerInfos(LinkedList<ServerInfo> serverInfos) {
		this.serverInfos = serverInfos;
	}
	

}
