package ca.polymtl.inf4402.tp2.repartiteur;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import ca.polymtl.inf4402.tp2.shared.ServerInterface;

@XmlRootElement(name = "ServerInfo")
public class ServerInfo {
	
	private String ip;
	private int port;
	private int loadEstimate = 1;
	private Boolean mode = false;
	
	private ServerInterface serverStub;

	public String getServerIpPort() {
		
		return ip + ":" + port;
	}
	
	public int getLoadEstimate() {
		return loadEstimate;
	}

	public void increaseLoadEstimate() {
		
		if (!mode){
			this.loadEstimate *= 2;
		} else{
			this.loadEstimate += 1;
		}
	}
	
	public void decreaseLoadEstimate() {
		
		if (!mode){
			this.loadEstimate /= 2;
		} else{
			this.loadEstimate -= 1;
		}
		this.mode = true; 
		
		if(this.loadEstimate < 1)
			this.loadEstimate = 1;
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
	
	@XmlTransient
	public ServerInterface getServerStub() {
		return serverStub;
	}

	public void setServerStub(ServerInterface serverStub) {
		this.serverStub = serverStub;
	}
}
