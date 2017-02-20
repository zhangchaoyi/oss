package common.pojo;
/**
 * 用于PaymentDataServiceImpl 数据处理
 * @author chris
 */
public class AreaARU {
	private double revenue;
	private long count;

	public AreaARU(){
		
	}
	
	public AreaARU(double revenue, long count){
		this.revenue = revenue;
		this.count = count;
	}
	
	public AreaARU(double revenue){
		this.revenue = revenue;
		this.count = 0;
	}
	
	public double getRevenue() {
		return revenue;
	}

	public void setRevenue(double revenue) {
		this.revenue = revenue;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
}
