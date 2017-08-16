package cn.xinling.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xinling.service.ProductService;

import com.google.gson.Gson;

public class SearchWordServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String word = request.getParameter("word");
		ProductService productService = new ProductService();
		List<Object> productPnameList = productService.findProductPnameByWord(word);
		//调用Gson（google公司开发的工具类，需要导入gson-2.2.4.jar包）
		Gson gson = new Gson();
		//使用Gson工具的toJson方法可以直接将java类转换为json字符串
		String json = gson.toJson(productPnameList);
		System.out.println(json);
		//解决服务器响应数据中文乱码
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}