package cn.xinling.service;

import java.sql.SQLException;
import java.util.List;

import cn.xinling.dao.CategoryDao;
import cn.xinling.domain.Category;

public class CategoryService {

	public List<Category> findCategoryList() {
		CategoryDao categoryDao = new CategoryDao();
		List<Category> categoryList = null;
		try {
			categoryList = categoryDao.findCategoryList();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

}
