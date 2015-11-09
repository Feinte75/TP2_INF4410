package ca.polymtl.inf4402.tp1.client;

public class ServerInfo {
	
	private String name;
	private String ip;
	private int port;
	private boolean hs;
	private int timer;
	
	public ServerInfo(String name, String ip, int port, boolean hs, int timer) {
		super();
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.hs = hs;
		this.timer = timer;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

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
	
	
	
}
