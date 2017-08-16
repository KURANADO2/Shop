package cn.xinling.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import cn.xinling.dao.AdminDao;
import cn.xinling.domain.Category;
import cn.xinling.domain.Order;
import cn.xinling.domain.Product;

public class AdminService {

	public List<Category> findAllCategory() {
		AdminDao adminDao = new AdminDao();
		List<Category> categoryList = null;
		try {
			categoryList = adminDao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public void saveProduct(Product product) {
		AdminDao adminDao = new AdminDao();
		try {
			adminDao.saveProduct(product);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Order> findAllOrders() {
		AdminDao adminDao = new AdminDao();
		List<Order> orderList = null;
		try {
			orderList = adminDao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		AdminDao adminDao = new AdminDao();
		List<Map<String, Object>> mapList = null;
		try {
			mapList =  adminDao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}

}
