package cn.xinling.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import cn.xinling.domain.Category;
import cn.xinling.domain.Product;
import cn.xinling.utils.DataSourceUtils;
import cn.xinling.vo.Condition;

public class AdminProductDao {

	//查找所有的商品
	public List<Product> findAllProduct() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product";
		List<Product> productList = qr.query(sql, new BeanListHandler<Product>(Product.class));
		return productList;
	}

	//查找所有的商品分类
	public List<Category> findAllCategory() throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM category";
		List<Category> categoryList = qr.query(sql, new BeanListHandler<Category>(Category.class));
		return categoryList;
	}

	//增加商品
	public void addProduct(Product product) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "INSERT INTO product VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		qr.update(sql, product.getPid(), product.getPname(), product.getMarket_price(), 
				product.getShop_price(), product.getPimage(), product.getPdate(), 
				product.getIs_hot(), product.getPdesc(), product.getPflag(), product.getCategory().getCid());
	}

	//通过pid删除商品
	public void deleteProductById(String pid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "DELETE FROM product WHERE pid = ?";
		qr.update(sql, pid);
	}

	//通过pid查找商品
	public Product findProductById(String pid) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM product WHERE pid = ?";
		Product product = qr.query(sql, new BeanHandler<Product>(Product.class), pid);
		return product;
	}

	//通过pid更新商品信息
	public void updateProductById(Product product) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "UPDATE product SET pname = ?, market_price = ?, shop_price = ?, pimage = ?, pdate = ?, is_hot = ?, pdesc = ?, pflag = ?, cid = ? WHERE pid = ?";
		qr.update(sql, product.getPname(), product.getMarket_price(), 
				product.getShop_price(), product.getPimage(), product.getPdate(), 
				product.getIs_hot(), product.getPdesc(), product.getPflag(), product.getCategory().getCid(), product.getPid());
	}

	//通过表单条件查找商品，难点在于表单的条件不一定用户都会填上，不确定用户会填上哪些筛选条件
	public List<Product> findProductListByCondition(Condition condition) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		//WHERE 1 = 1 总是成立，对后面拼接的AND条件（当然在用户没有在表单中输入任何数据的情况下会没有AND条件可以拼接，此时就查询所有的商品）不会有任何影响品。
		String sql = "SELECT * FROM product WHERE 1 = 1 ";
		List<String> paramsList = new ArrayList();
		//所有表单提交过来的数据如果没填则传过来的请求参数一定是空字符串
		if(condition.getPname() != null && !condition.getPname().trim().equals("")) {
			//模糊查询
			sql += " AND pname LIKE ? ";	//注意拼接的AND条件最好前后都留有一个空格
			paramsList.add("%" + condition.getPname().trim() + "%");
		}
		if(condition.getIs_hot() != null && !condition.getIs_hot().trim().equals("")) {
			//不能是模糊查询，因为值就两个，要么1（热门）要么0（不热门）
			sql += " AND is_hot = ? ";
			paramsList.add(condition.getIs_hot());
		}
		if(condition.getCid() != null && !condition.getCid().trim().equals("")) {
			sql += " AND cid = ? ";
			paramsList.add(condition.getCid());
		}
		//最后一个参数要求是可变参数（可变参数就是数组），所以把list转换为数组
		List<Product> productList = qr.query(sql, new BeanListHandler<Product>(Product.class), paramsList.toArray());
		return productList;
	}
}
