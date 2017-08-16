package cn.xinling.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import cn.xinling.domain.User;
import cn.xinling.service.UserService;
import cn.xinling.utils.CommonUtils;
import cn.xinling.utils.MailUtils;

public class RegisterServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 解决post请求方式中文乱码
		request.setCharacterEncoding("UTF-8");
		
		//获得用户输入的验证码,如果验证码不正确则将页面转发到register.jsp页面
		String checkcode = request.getParameter("checkcode");
		//从session会话中获得CheckImgServlet产生的验证码
		String checkcode_session = (String) request.getSession().getAttribute("checkcode_session");
		System.out.println("RegisterServlet:  " + checkcode + "  " + checkcode_session);
		if(! checkcode_session.equals(checkcode)) {
			request.setAttribute("info", "您输入的验证码不正确!");
			request.getRequestDispatcher("/register.jsp").forward(request, response);
//					request.getSession().setAttribute("info", "您输入的验证码不正确!");
//					response.sendRedirect(request.getContextPath() + "/register.jsp");
			return ;
		}
		
		// 获得表单数据
		Map<String, String[]> properties = request.getParameterMap();
		User user = new User();
		user.setUid(CommonUtils.getUUID());
		user.setTelephone(null);
		user.setState(0);
		String activeCode = CommonUtils.getUUID();
		user.setCode(activeCode);
		try {
			ConvertUtils.register(new Converter() {

				public Object convert(Class clazz, Object value) {
					// 将String转成Date
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
					Date parse = null;
					try {
						parse = format.parse(value.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return parse;
				}
			}, Date.class);
			// 使用BeanUtils工具类将表单中的数据封装到User实体类中
			// 映射封装
			BeanUtils.populate(user, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		UserService userService = new UserService();
		boolean isRegisterSuccess = userService.register(user);
		if (isRegisterSuccess) {
			// 用户注册成功,向用户填写的邮箱发送激活邮件并将页面重定向到registerSuccess.jsp页面
			// 因为是在本地测试，所以写localhost:8080,如果是发布到公网上，应该写IP地址或域名地址
			String emailMsg = "恭喜您注册心灵商城成功，请点击链接激活账户:<a href='http://localhost:8080/Shop/activeServlet?activeCode="
					+ activeCode + "'>激活账户</a>";
			try {
				MailUtils.sendMail(user.getEmail(), emailMsg);
			} catch (AddressException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			}
			response.sendRedirect(request.getContextPath()
					+ "/registerSuccess.jsp");
		} else {
			// 用户注册失败,重定向到registerFail.jsp页面
			response.sendRedirect(request.getContextPath()
					+ "/registerFail.jsp");
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}