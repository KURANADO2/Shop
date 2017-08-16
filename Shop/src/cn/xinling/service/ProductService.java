package cn.xinling.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import cn.xinling.dao.ProductDao;
import cn.xinling.domain.Order;
import cn.xinling.domain.OrderItem;
import cn.xinling.domain.PageBean;
import cn.xinling.domain.Product;
import cn.xinling.utils.DataSourceUtils;

public class ProductService {

	public List<Object> findProductPnameByWord(String word) {
		ProductDao productDao = new ProductDao();
		List<Object> productPnameList = null;
		try {
			productPnameList = productDao.findProductPnameByWord(word);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productPnameList;
	}
	
	public List<Product> findHotProductList() {
		ProductDao productDao = new ProductDao();
		List<Product> hotProductList = null;
		try {
			hotProductList = productDao.findHotProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hotProductList;
	}

	public List<Product> findNewProductList() {
		ProductDao productDao = new ProductDao();
		List<Product> newProductList = null;
		try {
			newProductList = productDao.findNewProductList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return newProductList;
	}

	public PageBean<Product> findProductListByCid(
			String cid, int currentPage, int currentCount) {
		ProductDao productDao = new ProductDao();
		PageBean<Product> pageBean = new PageBean<Product>();
		int totalCount = 0;
		try {
			totalCount = productDao.getTotalCountByCid(cid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
		int index = (currentPage - 1) * currentCount; 
		List<Product> list = null;
		try {
			list = productDao.findProductListByCid(cid, index, currentCount);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		pageBean.setCurrentPage(currentPage);
		pageBean.setCurrentCount(currentCount);
		pageBean.setTotalPage(totalPage);
		pageBean.setTotalCount(totalCount);
		pageBean.setList(list);
		return pageBean;
	}

	public Product findProductByPid(String pid) {
		ProductDao productDao = new ProductDao();
		Product product = null;
		try {
			product = productDao.findProductByPid(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return product;
	}

	public void submitOrder(Order order) {
		//需要将Order对象中的数据和OrderItem中的数据都保存到数据库中，而且要都完成或都不完成，所以需要事务控制
		//事务控制放在service层处理
		try {
			//1.开启事务
			DataSourceUtils.startTransaction();
			ProductDao productDao = new ProductDao();
			//2.将Order对象中封装的数据保存到数据库中
			productDao.addOrder(order);
			//3.将OrderItem中封装的数据
			productDao.addOrderItem(order.getOrderItems());
		} catch (SQLException e) {
			try {
				//事务回滚
				DataSourceUtils.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			try {
				//4.提交事务
				DataSourceUtils.commitAndRelease();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//更新orders表中的收货人信息
	public void updateOrderAddr(Order order) {
		ProductDao productDao = new ProductDao();
		try {
			productDao.updateOrderAddr(order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateOrderState(String r6_Order) {
		ProductDao productDao = new ProductDao();
		try {
			productDao.updateOrderState(r6_Order);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public List<Order> findAllOrders(String uid) {
		ProductDao productDao = new ProductDao();
		List<Order> orderList = null;
		try {
			orderList = productDao.findAllOrders(uid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return orderList;
	}

	public List<Map<String, Object>> findAllOrderItemByOid(String oid) {
		ProductDao productDao = new ProductDao();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = productDao.findAllOrderItemByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}
}
