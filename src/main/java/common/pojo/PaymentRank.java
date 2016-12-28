package common.pojo;

public class PaymentRank {
	private String createTime;
	private String firstPaidTime;
	private double revenue;
	private long paidTimes;
	private long onlineDays;
	private String onlineTime;
	private long gameTimes;
	private int level;
	private String roleName;
	
	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getFirstPaidTime() {
		return firstPaidTime;
	}

	public void setFirstPaidTime(String firstPaidTime) {
		this.firstPaidTime = firstPaidTime;
	}

	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public long getPaidTimes() {
		return paidTimes;
	}

	public void setPaidTimes(long paidTimes) {
		this.paidTimes = paidTimes;
	}

	public long getOnlineDays() {
		return onlineDays;
	}

	public void setOnlineDays(long onlineDays) {
		this.onlineDays = onlineDays;
	}

	public String getOnlineTime() {
		return onlineTime;
	}

	public void setOnlineTime(String onlineTime) {
		this.onlineTime = onlineTime;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public long getGameTimes() {
		return gameTimes;
	}

	public void setGameTimes(long gameTimes) {
		this.gameTimes = gameTimes;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
}
