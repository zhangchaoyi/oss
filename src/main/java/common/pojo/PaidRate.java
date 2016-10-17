package common.pojo;

public class PaidRate {
	private long paidPlayers;
	private long activePlayers;

	public PaidRate(){
		paidPlayers = 0L;
		activePlayers = 0L;
	}
	
	public long getPaidPlayers() {
		return paidPlayers;
	}

	public void setPaidPlayers(long paidPlayers) {
		this.paidPlayers = paidPlayers;
	}

	public long getActivePlayers() {
		return activePlayers;
	}

	public void setActivePlayers(long activePlayers) {
		this.activePlayers = activePlayers;
	}

}
