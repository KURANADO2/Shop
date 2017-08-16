package cn.xinling.web.filter;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.xinling.domain.User;
import cn.xinling.service.UserService;

public class AutoLoginFilter implements Filter {

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
//		System.out.println("auto login filter 正在过虑");
		// 因为ServletRequest接口没有getCookies方法，所以把request强转为HttpServletRequest
		HttpServletRequest req = (HttpServletRequest) request;
		// 因为ServletResponse接口没有sendRedirect方法，所以把response强转为HttpServletResponse
		HttpServletResponse res = (HttpServletResponse) response;

		HttpSession session = req.getSession();

		String cookie_username = null;
		String cookie_password = null;

		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("cookie_username".equals(cookie.getName())) {
					cookie_username = URLDecoder.decode(cookie.getValue(), "UTF-8");
				}
				if ("cookie_password".equals(cookie.getName())) {
					cookie_password = cookie.getValue();
				}
			}
		}

		if (cookie_username != null && cookie_password != null) {
//			System.out.println(cookie_username + "-------" + cookie_password);
			UserService userService = new UserService();
			User user = userService.login(cookie_username, cookie_password);
			// 自动登录成功,用户可以访问此web应用的任何资源
			session.setAttribute("user", user);
			// res.sendRedirect(req.getContextPath() + "/index.jsp");
		}
		chain.doFilter(req, res);
	}

	public void destroy() {

	}

}
