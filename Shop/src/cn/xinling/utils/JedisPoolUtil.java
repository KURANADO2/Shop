package cn.xinling.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolUtil {
	private static JedisPool jedisPool = null;
	static {
		JedisPoolConfig poolConfig = new JedisPoolConfig();
//		poolConfig.setMaxIdle(30);
//		poolConfig.setMinIdle(10);
//		poolConfig.setMaxTotal(100);
//		jedisPool = new JedisPool(poolConfig, "192.168.234.131", 6379);
		//实际开发中都是从配置文件中加载配置参数而不使用硬编码的方式
		InputStream in = JedisPoolUtil.class.getClassLoader().getResourceAsStream("jedispool.properties");
		Properties properties = new Properties();
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		poolConfig.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle")));
		poolConfig.setMinIdle(Integer.parseInt(properties.getProperty("minIdle")));
		poolConfig.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal")));
		jedisPool = new JedisPool(poolConfig, properties.getProperty("url"), Integer.parseInt(properties.getProperty("port")));
	}
	
	public static Jedis getJedis() {
		return jedisPool.getResource();
	}
	
	public static void closeJedis(Jedis jedis) {
		jedis.close();
	}
	
	public static void closeJedisPool() {
		jedisPool.close();
	}
	
	//测试
//	public static void main(String[] args) {
//		Jedis jedis = getJedis();
//		System.out.println(jedis.get("xxx"));
//	}
}
