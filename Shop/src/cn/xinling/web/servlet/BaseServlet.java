package cn.xinling.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("all")
public class BaseServlet extends HttpServlet {

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		//1.获得请求参数
		String method = request.getParameter("method");
		//2.获得当前被访问对象的字节码对象(如当前被访问的是ProductServlet则获得ProductServlet.class,如果当前被访问的是UserServlet则获得UserServlet.class)
		Class clazz = this.getClass();
		//3.通过字节码对象获得当前字节码对象中的指定方法,param1：方法名称，pram2:是一个可变参数，为参数的字节码
		Method method2 = null;
		try {
			method2 = clazz.getMethod(method, HttpServletRequest.class, HttpServletResponse.class);
			//4.执行响应方法,param1:当前对象，param2：可变参数，传入方法的实参
			method2.invoke(this, request, response);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
}