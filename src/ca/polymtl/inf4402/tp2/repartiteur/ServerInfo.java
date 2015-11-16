package ca.polymtl.inf4402.tp2.repartiteur;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ca.polymtl.inf4402.tp2.shared.ServerInterface;

@XmlRootElement(name = "ServerInfo")
public class ServerInfo {
	
	private String name;
	private String ip;
	private int port;
	private boolean hs;
	private int timer;
	private int loadEstimate;
	
	private ServerInterface serverStub;
	
//	public ServerInfo(String name, String ip, int port, boolean hs, int timer) {
//		super();
//		this.name = name;
//		this.ip = ip;
//		this.port = port;
//		this.hs = hs;
//		this.timer = timer;
//	}

	public int getLoad() {
		return loadEstimate;
	}

	public void setLoad(int load) {
		this.loadEstimate = load;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@XmlElement(name="ip")  
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@XmlElement(name="port")
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isHs() {
		return hs;
	}

	public void setHs(boolean hs) {
		this.hs = hs;
	}

	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	@XmlTransient
	public ServerInterface getServerStub() {
		return serverStub;
	}

	public void setServerStub(ServerInterface serverStub) {
		this.serverStub = serverStub;
	}
	
	
	
}
