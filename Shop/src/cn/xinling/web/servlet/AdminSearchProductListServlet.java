package cn.xinling.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;

import cn.xinling.domain.Category;
import cn.xinling.domain.Product;
import cn.xinling.service.AdminProductService;
import cn.xinling.vo.Condition;

public class AdminSearchProductListServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		Map<String, String[]> properties = request.getParameterMap();
		Condition condition = new Condition();
		try {
			BeanUtils.populate(condition, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		AdminProductService adminProductService = new AdminProductService();
		List<Product> productList = adminProductService.findProductListByCondition(condition);
		List<Category> categoryList = adminProductService.findAllCategory();
		request.setAttribute("productList", productList);
		request.setAttribute("categoryList", categoryList);
		request.setAttribute("condition", condition);	//用于回显用户选择的表单数据
		request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}