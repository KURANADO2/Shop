package cn.xinling.service;

import java.sql.SQLException;

import cn.xinling.dao.UserDao;
import cn.xinling.domain.User;

public class UserService {

	public boolean register(User user) {
		UserDao userDao = new UserDao();
		int row = 0;
		try {
			row = userDao.register(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return row > 0 ? true : false;
	}

	public void active(String activeCode) {
		UserDao userDao = new UserDao();
		try {
			userDao.active(activeCode);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean checkUsername(String username) {
		UserDao userDao = new UserDao();
		Long count = 0L;
		try {
			count = userDao.checkUsername(username);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count > 0 ? true : false;
	}

	public User login(String username, String password) {
		UserDao userDao = new UserDao();
		User user = null;
		try {
			user = userDao.login(username, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return user;
	}
	
}
