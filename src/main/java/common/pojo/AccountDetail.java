package common.pojo;

/**
 * 用于PaymentRankServiceImpl 数据处理
 * @author chris
 */
public class AccountDetail {
	private long onlineTime;
	private long loginTimes;
	private double revenue;

	public AccountDetail(){
		onlineTime = 0L;
		loginTimes = 0L;
		revenue = 0.0;
	}
	
	public long getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(long onlineTime) {
		this.onlineTime = onlineTime;
	}

	public long getLoginTimes() {
		return loginTimes;
	}

	public void setLoginTimes(long loginTimes) {
		this.loginTimes = loginTimes;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

}
