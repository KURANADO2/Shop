package cn.xinling.domain;

public class OrderItem {
	private String itemid;	//该订单项的编号
	private int count;	//该订单项商品的购买数量
	private double subtotal;	//该订单项金额小计
	private Product product;	//该订单项的商品
	private Order order;	//该订单项属于哪个订单（一个订单项只能属于一个订单，一个订单下可以包含多个订单项）
	public String getItemid() {
		return itemid;
	}
	public void setItemid(String itemid) {
		this.itemid = itemid;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public Order getOrder() {
		return order;
	}
	public void setOrder(Order order) {
		this.order = order;
	}
}
