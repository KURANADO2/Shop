package cn.xinling.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	private String oid;	//该订单的订单编号
	private Date ordertime;	//订单的生成时间
	private double total;	//该订单的总金额
	private int state;	//订单的支付状态，1已付款，0未付款
	private String address;	//收货人地址，注意收货人和当前登录的用户并不一定是同一个人
	private String name;	//收货人姓名
	private String telephone;	//收货人联系方式
	private User user;	//该订单属于哪个用户，即当前登录的用户
	List<OrderItem> orderItems = new ArrayList<OrderItem>();	//该订单中包含的订单项
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public Date getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<OrderItem> getOrderItems() {
		return orderItems;
	}
	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}
}
