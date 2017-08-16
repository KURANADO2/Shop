package cn.xinling.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ColumnListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.xinling.domain.Order;
import cn.xinling.domain.OrderItem;
import cn.xinling.domain.Product;
import cn.xinling.utils.DataSourceUtils;

public class ProductDao {

	// 根据商品名称模糊查询商品
	public List<Object> findProductPnameByWord(String word) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		// 只取出模糊查询结果的前八条记录
		String sql = "SELECT * FROM product WHERE pname LIKE ? LIMIT 0, 8";
		List<Object> productPnameList = qr.query(sql, new ColumnListHandler(
				"pname"), "%" + word + "%");
		return productPnameList;
	}

	public List<Product> findHotProductList() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product WHERE is_hot = ? LIMIT ?, ?";
		List<Product> hotProductList = qr.query(sql,
				new BeanListHandler<Product>(Product.class), 1, 0, 9);
		return hotProductList;
	}

	public List<Product> findNewProductList() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product ORDER BY pdate DESC LIMIT ?, ?";
		List<Product> newProductList = qr.query(sql,
				new BeanListHandler<Product>(Product.class), 0, 9);
		return newProductList;
	}

	public int getTotalCountByCid(String cid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT COUNT(*) FROM product WHERE cid = ?";
		Long totalCountByCid = (Long) qr.query(sql, new ScalarHandler(), cid);
		return totalCountByCid.intValue();
	}

	public List<Product> findProductListByCid(String cid, int index,
			int currentCount) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product WHERE cid = ? LIMIT ?, ?";
		List<Product> list = qr.query(sql, new BeanListHandler<Product>(
				Product.class), cid, index, currentCount);
		return list;
	}

	public Product findProductByPid(String pid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product WHERE pid = ?";
		return qr.query(sql, new BeanHandler<Product>(Product.class), pid);
	}

	// 将OrderItem中封装的数据插入到orderitem表中
	public void addOrderItem(List<OrderItem> orderItems) throws SQLException {
		// 使用无参的构造方法
		QueryRunner qr = new QueryRunner();
		// 因为要使用事务，通过ThreadLocal保证获得的是同一个连接
		Connection connection = DataSourceUtils.getConnection();
		String sql = "INSERT INTO orderitem VALUES(?, ?, ?, ?, ?)";
		for (OrderItem orderItem : orderItems) {
			qr.update(connection, sql, orderItem.getItemid(), orderItem
					.getCount(), orderItem.getSubtotal(), orderItem
					.getProduct().getPid(), orderItem.getOrder().getOid());
		}
	}

	// 将Order中封装的数据插入到orders表中
	public void addOrder(Order order) throws SQLException {
		// 使用无参的构造方法
		QueryRunner qr = new QueryRunner();
		// 因为要使用事务，通过ThreadLocal保证获得的是同一个连接
		Connection connection = DataSourceUtils.getConnection();
		String sql = "INSERT INTO orders VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		qr.update(connection, sql, order.getOid(), order.getOrdertime(),
				order.getTotal(), order.getState(), order.getAddress(),
				order.getName(), order.getTelephone(), order.getUser().getUid());
	}

	// 更新orders表中的收货人信息
	public void updateOrderAddr(Order order) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "UPDATE orders SET address = ?, name = ?, telephone = ? WHERE oid = ?";
		qr.update(sql, order.getAddress(), order.getName(),
				order.getTelephone(), order.getOid());
	}

	public void updateOrderState(String r6_Order) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "UPDATE orders SET state = ? WHERE oid = ?";
		qr.update(sql, 1, r6_Order);
	}

	public List<Order> findAllOrders(String uid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM orders WHERE uid = ?";
		List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(
				Order.class), uid);
		return orderList;
	}

	public List<Map<String, Object>> findAllOrderItemByOid(String oid)
			throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		// 只查出order_list.jsp页面中需要用到的数据
		String sql = "SELECT p.pimage, p.pname, p.shop_price, i.count, i.subtotal FROM  orderitem i, product p WHERE i.pid = p.pid AND i.oid = ?";
		// mapList中包含p.pimage, p.pname, p.shop_price, i.count,
		// i.subtotal，既不能完全封装到Product对象，也无法全部封装到OrderItem对象,所以不是用BeanListHandler<>()而使用MapListHandler()
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),
				oid);
		return mapList;
	}
}
