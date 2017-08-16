package cn.xinling.service;

import java.sql.SQLException;
import java.util.List;

import cn.xinling.dao.AdminProductDao;
import cn.xinling.domain.Category;
import cn.xinling.domain.Product;
import cn.xinling.vo.Condition;

public class AdminProductService {

	public List<Product> findAllProduct() {
		List<Product> productList = null;
		try {
			productList = new AdminProductDao().findAllProduct();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}

	public List<Category> findAllCategory() {
		List<Category> categoryList = null;
		try {
			categoryList = new AdminProductDao().findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public void addProduct(Product product) {
		//传递数据给Dao层
		try {
			new AdminProductDao().addProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteProductById(String pid) {
		try {
			new AdminProductDao().deleteProductById(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Product findProductById(String pid) {
		Product product = null;
		try {
			product = new AdminProductDao().findProductById(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}

	public void updateProductById(Product product) {
		try {
			new AdminProductDao().updateProductById(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Product> findProductListByCondition(Condition condition) {
		List<Product> productList = null;
		try {
			productList = new AdminProductDao().findProductListByCondition(condition);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productList;
	}
}
