package cn.xinling.web.servlet;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.xinling.domain.User;
import cn.xinling.service.UserService;

public class UserServlet extends BaseServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().write("hello haohao...");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	//注销登录
	public void loginOut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		//从session域中删除user对象
		session.removeAttribute("user");
		
		//如果用户组在登录时勾选了自动登录，则客户端cookie中保存用户名和密码10天，下次访问此站点的任何应用都会经过AutoLoginFilter的过滤，
		//AutoLoginFilter发现cookie中有正确的用户名和密码会自动执行登录功能，所以当我们点击注销时，让页面跳转到login.jsp，而AutoLoginFilter
		//又帮我们重新登录了，为此我们需要删除保存用户名和密码的cookie
		Cookie cookie_username = new Cookie("cookie_username", "");//cookie_username对应的值此时已无所谓
		Cookie cookie_password = new Cookie("cookie_password", "");
		//设置cookie的持久化时间为立即失效
		cookie_username.setMaxAge(0);
		cookie_password.setMaxAge(0);
		//设置cookie的携带路径必须和想要删除的同名cookie的携带路径形同，否则不能删除指定的cookie，因为相同键名的Cookie(值可以相同或不同)可以存在于不同的路径下。
		cookie_username.setPath(request.getContextPath());
		cookie_password.setPath(request.getContextPath());
//		System.out.println(cookie_username.getValue() + "+++" + cookie_password.getValue());
		//将cookie放到向响应中发给客户端
		response.addCookie(cookie_username);
		response.addCookie(cookie_password);
		
		response.sendRedirect(request.getContextPath() + "/login.jsp");
		
	}	
	
	//用户登录
	public void login(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		HttpSession session = request.getSession();
		
		
		//先验证用户输入的验证码是否在正确，在验证码正确的前提下再去从数据库中判断用户名和密码是否正确
		//先通过Session对象获得验证码图片中的文字
		String checkcode_session = (String) session.getAttribute("checkcode_session");
		//获得用户输入的验证码
		String checkcode = request.getParameter("checkcode");
		System.out.println("LoginServlet:  " + checkcode_session + "  " + checkcode);
		//判断用户输入的密码和CheckImgServlet动态生成的验证码是否一致
		if(! checkcode_session.equals(checkcode)) {
			//用户输入验证码不正确则进行请求转发到login.jsp
			request.setAttribute("info", "您输入的验证码不正确！");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
			return ;
		} 
		
		
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//如果用户名是admin密码也是admin则将页面跳转到/admin/home.jsp页面
		if("admin".equals(username) && "admin".equals(password)) {
			response.sendRedirect(request.getContextPath() + "/admin/home.jsp");
			return ;
		}
		
		String autoLogin = request.getParameter("autoLogin");
		//判断用户是否勾选自动登录
		//勾选自动登录
		if(autoLogin != null && "autoLogin".equals(autoLogin)) {
			//创建cookie用来保存用户输入的用户名和密码
			//因为cookie中不能直接保存中文，否则会报HTTP Status 500 - Control character in cookie value or attribute.错误。
			//所以使用URLEncoder类中的encode方法对用户名进行编码，从cookie中取出username时再用URLDecoder类中的decode方法进行解码成中文
			Cookie cookie_username = new Cookie("cookie_username", URLEncoder.encode(username, "UTF-8"));
			Cookie cookie_password = new Cookie("cookie_password", password);
			//设置cookie的持久化时间位10天
			cookie_username.setMaxAge(10 * 24 * 60 * 60);
			cookie_password.setMaxAge(10 * 24 * 60 * 60);
			//设置cookie的携带路径为访问此web应用下的任何资源时都携带这两个cookie
			cookie_username.setPath(request.getContextPath());
			cookie_password.setPath(request.getContextPath());
			//将cookie放到向响应中发给客户端
			response.addCookie(cookie_username);
			response.addCookie(cookie_password);
		}
		
		UserService userService = new UserService();
		User user = userService.login(username, password);
		if(user == null) {
			//登录失败，转发到登录页面login.jsp,并通过request域对象传递一个错误提示信息
			request.setAttribute("loginInfo", "用户名或密码不正确！");
			request.getRequestDispatcher("/login.jsp").forward(request, response);
		} else {
			//登录成功,重定向到首页index.jsp,并将User对象放到session域对象中，方便以后使用
			session.setAttribute("user", user);
			response.sendRedirect(request.getContextPath() + "/default.jsp");
		}
	}
}