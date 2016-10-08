package common.pojo;

public class AreaARPU {
	private double revenue;
	private long count;

	public AreaARPU(){
		
	}
	
	public AreaARPU(double revenue, long count){
		this.revenue = revenue;
		this.count = count;
	}
	
	public AreaARPU(double revenue){
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
