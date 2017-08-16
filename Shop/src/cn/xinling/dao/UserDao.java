package cn.xinling.dao;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.xinling.domain.User;
import cn.xinling.utils.DataSourceUtils;

public class UserDao {

	public int register(User user) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "INSERT INTO user VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int row = qr.update(sql, user.getUid(), user.getUsername(), user.getPassword(), user.getName(), 
				user.getEmail(), user.getTelephone(), user.getBirthday(), user.getSex(), 
				user.getState(), user.getCode());
		return row;
	}

	public void active(String activeCode) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "UPDATE user SET state = ? WHERE code = ?";
		qr.update(sql, 1, activeCode);
	}

	public Long checkUsername(String username) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
		Long count = (Long) qr.query(sql, new ScalarHandler(), username);
		return count;
	}
	
	public User login(String username, String password) throws SQLException {
		QueryRunner qr = new QueryRunner(DataSourceUtils.getDataSource());
		String sql = "SELECT * FROM user WHERE username = ? AND password = ?";
		return qr.query(sql, new BeanHandler<User>(User.class), username, password);
	}
}
