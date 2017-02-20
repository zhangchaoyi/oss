package common.pojo;
/**
 * 用于OperationCurrencyServiceImpl 数据处理
 * @author chris
 */
public class CurrencyRmb {
	private String account;
	private String timestamp;
	private String reason;
	private int count;
	private String getOrConsume;
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getGetOrConsume() {
		return getOrConsume;
	}
	public void setGetOrConsume(String getOrConsume) {
		this.getOrConsume = getOrConsume;
	}
	@Override
	public String toString() {
		return "CurrencyRmb [account=" + account + ", timestamp=" + timestamp + ", reason=" + reason + ", count="
				+ count + ", getOrConsume=" + getOrConsume + "]";
	}
}
