package cn.xinling.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xinling.domain.Category;
import cn.xinling.domain.Order;
import cn.xinling.service.AdminService;

import com.google.gson.Gson;

public class AdminServlet extends BaseServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
	
	//弹出层订单详情
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String oid = request.getParameter("oid");
		AdminService adminService = new AdminService();
		List<Map<String, Object>> mapList = adminService.findOrderInfoByOid(oid);
		
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
//		System.out.println(json);	//打印json字符串如下：
		/*
		 * [
		 * 		{"shop_price":4087.0,"count":13,"pname":"华为 HUAWEI Mate S 臻享版","pimage":"products/1/c_0016.jpg","subtotal":53131.0},
		 * 		{"shop_price":5499.0,"count":2,"pname":"神舟（HASEE） 战神K660D-i7D2","pimage":"products/1/c_0041.jpg","subtotal":10998.0}
		 * ]
		 * */
		response.setContentType("text/html;charset=UTF-8");
		try {
			Thread.sleep(1000);	//为了演示加载的延迟效果，此处让线程休息3s，然后再向服务器发送响应数据
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		response.getWriter().write(json);
	}	
	
	//查找所有的订单
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AdminService adminService = new AdminService();
		List<Order> orderList = adminService.findAllOrders(); 
		request.setAttribute("orderList", orderList);
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
	}
	
	//查找所有的商品分类
	public void findAllCategory(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AdminService adminService = new AdminService();
		List<Category> categoryList = adminService.findAllCategory();
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		//解决中文乱码
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
}