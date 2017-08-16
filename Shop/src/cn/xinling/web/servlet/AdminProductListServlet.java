package cn.xinling.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xinling.domain.Category;
import cn.xinling.domain.Product;
import cn.xinling.service.AdminProductService;

public class AdminProductListServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AdminProductService	adminProductService = new AdminProductService();
		List<Product> productList = adminProductService.findAllProduct();
		List<Category> categoryList = adminProductService.findAllCategory();
		request.setAttribute("productList", productList);
		request.setAttribute("categoryList", categoryList);
		request.getRequestDispatcher("/admin/product/list.jsp").forward(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}