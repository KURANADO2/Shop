package cn.xinling.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.xinling.domain.Category;
import cn.xinling.domain.Order;
import cn.xinling.domain.Product;
import cn.xinling.utils.DataSourceUtils;

public class AdminDao {

	public List<Category> findAllCategory() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM category";
		List<Category> categoryList = qr.query(sql, new BeanListHandler<Category>(Category.class));
		return categoryList;
	}

	public void saveProduct(Product product) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "INSERT INTO product VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		qr.update(sql, product.getPid(), product.getPname(), product.getMarket_price(),
				product.getShop_price(), product.getPimage(), product.getPdate(),
				product.getIs_hot(), product.getPdesc(), product.getPflag(), product.getCategory().getCid());
	}

	public List<Order> findAllOrders() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM orders";
		List<Order> orderList = qr.query(sql, new BeanListHandler<Order>(Order.class));
		return orderList;
	}

	public List<Map<String, Object>> findOrderInfoByOid(String oid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT p.pimage, p.pname, p.shop_price, i.count, i.subtotal FROM product AS p, orderitem AS i WHERE p.pid = i.pid AND i.oid = ?";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), oid);
		return mapList;
	}

}
