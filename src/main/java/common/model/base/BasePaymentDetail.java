package common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePaymentDetail<M extends BasePaymentDetail<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}

	public java.lang.Integer getId() {
		return get("id");
	}

	public void setDate(java.util.Date date) {
		set("date", date);
	}

	public java.util.Date getDate() {
		return get("date");
	}

	public void setOs(java.lang.String os) {
		set("os", os);
	}

	public java.lang.String getOs() {
		return get("os");
	}

	public void setPaidMoney(java.lang.Double paidMoney) {
		set("paid_money", paidMoney);
	}

	public java.lang.Double getPaidMoney() {
		return get("paid_money");
	}

	public void setPaidPeople(java.lang.Integer paidPeople) {
		set("paid_people", paidPeople);
	}

	public java.lang.Integer getPaidPeople() {
		return get("paid_people");
	}

	public void setPaidNum(java.lang.Integer paidNum) {
		set("paid_num", paidNum);
	}

	public java.lang.Integer getPaidNum() {
		return get("paid_num");
	}

	public void setFtPaidMoney(java.lang.Double ftPaidMoney) {
		set("ft_paid_money", ftPaidMoney);
	}

	public java.lang.Double getFtPaidMoney() {
		return get("ft_paid_money");
	}

	public void setFtPaidPeople(java.lang.Integer ftPaidPeople) {
		set("ft_paid_people", ftPaidPeople);
	}

	public java.lang.Integer getFtPaidPeople() {
		return get("ft_paid_people");
	}

	public void setFdPaidMoney(java.lang.Double fdPaidMoney) {
		set("fd_paid_money", fdPaidMoney);
	}

	public java.lang.Double getFdPaidMoney() {
		return get("fd_paid_money");
	}

	public void setFdPaidPeople(java.lang.Integer fdPaidPeople) {
		set("fd_paid_people", fdPaidPeople);
	}

	public java.lang.Integer getFdPaidPeople() {
		return get("fd_paid_people");
	}

	public void setFdPaidNum(java.lang.Integer fdPaidNum) {
		set("fd_paid_num", fdPaidNum);
	}

	public java.lang.Integer getFdPaidNum() {
		return get("fd_paid_num");
	}

}