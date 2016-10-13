package common.pojo;

public class AddPaymentAnalyze {
	int addPlayers;
	int fdPaidPeople;
	int fwPaidPeople;
	int fmPaidPeople;
	
	public AddPaymentAnalyze(){
		addPlayers=0;
		fdPaidPeople=0;
		fwPaidPeople=0;
		fmPaidPeople=0;
	}
	
	public int getAddPlayers() {
		return addPlayers;
	}
	public void setAddPlayers(int addPlayers) {
		this.addPlayers = addPlayers;
	}
	public int getFdPaidPeople() {
		return fdPaidPeople;
	}
	public void setFdPaidPeople(int fdPaidPeople) {
		this.fdPaidPeople = fdPaidPeople;
	}
	public int getFwPaidPeople() {
		return fwPaidPeople;
	}
	public void setFwPaidPeople(int fwPaidPeople) {
		this.fwPaidPeople = fwPaidPeople;
	}
	public int getFmPaidPeople() {
		return fmPaidPeople;
	}
	public void setFmPaidPeople(int fmPaidPeople) {
		this.fmPaidPeople = fmPaidPeople;
	}
	
}
