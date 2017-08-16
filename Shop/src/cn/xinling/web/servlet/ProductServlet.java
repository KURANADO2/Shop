package cn.xinling.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import redis.clients.jedis.Jedis;
import cn.xinling.domain.Cart;
import cn.xinling.domain.CartItem;
import cn.xinling.domain.Category;
import cn.xinling.domain.Order;
import cn.xinling.domain.OrderItem;
import cn.xinling.domain.PageBean;
import cn.xinling.domain.Product;
import cn.xinling.domain.User;
import cn.xinling.service.CategoryService;
import cn.xinling.service.ProductService;
import cn.xinling.utils.CommonUtils;
import cn.xinling.utils.JedisPoolUtil;
import cn.xinling.utils.PaymentUtil;

import com.google.gson.Gson;

public class ProductServlet extends BaseServlet {

	// 下面的代码写的过于烦琐，所以改用反射技术来实现调用对应的方法
	/*
	 * public void doGet(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { String method =
	 * request.getParameter("method"); if("categoryList".equals(method)) {
	 * categoryList(request, response); } else if ("index".equals(method)) {
	 * index(request, response); } else if ("productInfo".equals(method)) {
	 * productInfo(request, response); } else if
	 * ("productListByCid".equals(method)) { productListByCid(request,
	 * response); } }
	 * 
	 * public void doPost(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException { doGet(request,
	 * response); }
	 */
	
	//封装数据到Order对象中
	public void myOrders(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//先判断用户是否登录，若未登录则直接将页面重定向到login.jsp并结束此方法（项目中有多处需要用到判断用户是否
		//登录，所以可以抽取一个工具类或者在filter中进行处理）
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			// 重定向后后面的代码都没必要再执行了，所以直接return
			return;
		}
		
		ProductService productService = new ProductService();
		//获得当前登录用户的所有订单
		List<Order> orderList = productService.findAllOrders(user.getUid());
		if(orderList != null) {
			//遍历该用户的所有订单
			for(Order order : orderList) {
				//根据该订单的oid查找该订单中的所有订单项
				String oid = order.getOid();
				List<Map<String, Object>> mapList = productService.findAllOrderItemByOid(oid);
				for(Map<String, Object> map : mapList) {
					try {
						OrderItem orderItem = new OrderItem();
						Product product  = new Product();
						
						//1.从map中取出count，subtotal封装到OrderItem中
						//下面这两条语句是从map中取出对应的值然后代用OrderItem对象的set方法封装值，这样比较麻烦，可以使用BeanUtils工具类直接进行封装
//						orderItem.setCount(Integer.parseInt(map.get("count").toString()));
//						orderItem.setSubtotal(Double.parseDouble(map.get("subtotal").toString()));
						BeanUtils.populate(orderItem, map);
						//2.从map中取出pimage，pname，shop_price封装到Product中，同理也直接使用BeanUtils进行封装
						BeanUtils.populate(product, map);
						
						//3.将product封装到orderItem中
						orderItem.setProduct(product);
						
						//4.将orderItem封装到Order对象中
						order.getOrderItems().add(orderItem);
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		//到此orderList封装完毕
		
		request.setAttribute("orderList", orderList);
		//转发到order_list.jsp
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
	}	
	
	// 更新数据库orders表中的收货人信息+在线支付
	public void confirmOrder(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 1.更新数据库orders表中的收货人信息
		Map<String, String[]> properties = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		ProductService productService = new ProductService();
		// 更新orders表中的收货人信息
		productService.updateOrderAddr(order);
		System.out.println(order.getName() + "--" + order.getAddress() + "--" + order.getTelephone());
		
		// 2.在线支付
		// 接入易宝支付
		// 获得易宝支付必须基本数据
		String orderid = request.getParameter("oid");	//订单编号oid
		System.out.println(order.getTotal() + "----------------");
//		String money = order.getTotal() + "";	//获取订单金额
		String money = "1.0";
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据,通过"易宝支付产品通用接口帮助文档"->""
		String p0_Cmd = "Buy";	//业务类型，固定值"Buy"
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString(
				"p1_MerId");	//获取配置文件的配置信息--商户编号--商户在易宝支付系统的唯一身份标识
		String p2_Order = orderid;	//商户订单号
		String p3_Amt = money;	//支付金额
		String p4_Cur = "CNY";	//交易币种，固定值"CNY",即人民币
		String p5_Pid = "";	//商品名称，用于支付时显示在易宝支付网关左侧的订单产品信息，如商品名称涉及到中文需要自己解决中文乱码问题
		String p6_Pcat = "";	//商品种类，如果此参数用到中文请注意转码
		String p7_Pdesc = "";	//商品描述，如果此参数用到中文请注意转码
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString(
				"callback");	//商户接收支付成功数据的地址，支付成功后易宝支付会向该地址发送两次成功通知，
								//如果不写此参数支付成功后将得不到支付成功的通知
		String p9_SAF = "";	//送货地址
		String pa_MP = "";	//商户扩展信息，如果此参数用到中文请注意转码
		String pr_NeedResponse = "1";	//应答机制，固定值为"1"
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);

		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="
				+ pd_FrpId + "&p0_Cmd=" + p0_Cmd + "&p1_MerId=" + p1_MerId
				+ "&p2_Order=" + p2_Order + "&p3_Amt=" + p3_Amt + "&p4_Cur="
				+ p4_Cur + "&p5_Pid=" + p5_Pid + "&p6_Pcat=" + p6_Pcat
				+ "&p7_Pdesc=" + p7_Pdesc + "&p8_Url=" + p8_Url + "&p9_SAF="
				+ p9_SAF + "&pa_MP=" + pa_MP + "&pr_NeedResponse="
				+ pr_NeedResponse + "&hmac=" + hmac;

		// 重定向到第三方支付平台
		response.sendRedirect(url);
	}

	// 提交订单
	public void submitOrder(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 先判断用户是否登录，如未登录则将页面重定向到login.jsp页面，判断用户是否登录的标准是看session对象中是否存有User对象
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");
		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login.jsp");
			// 重定向后后面的代码都没必要再执行了，所以直接return
			return;
		}
		// 封装一个Order对象传递个service层
		Order order = new Order();
		// 1.private String oid; //该订单的订单编号
		order.setOid(CommonUtils.getUUID());
		// 2.private Date ordertime; //订单的生成时间(这里需要注意，
		// 直接写order.setOrdertime(new Date())程序会出现异常，
		// 因为mysql中的datetime类型和java.util.Date数据类型不匹配，
		// mysql中要求的datatime格式为yyyy-MM-dd hh:mm:ss,而直
		// 接new Date()产生的日期格式不是这样，所以需要转换为yyyy-MM-dd hh:mm:ss
		// 格式的字符串后再将此字符串解析为Date类型)
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String format2 = format.format(date);
		try {
			order.setOrdertime(format.parse(format2));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 3.private double total; //该订单的总金额,即购物车的总金额
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart != null) {
			order.setTotal(cart.getTotal());
		} else {
			order.setTotal(0);
		}
		// 4.private int state; //订单的支付状态，1已付款，0未付款,因为此时还没有进行付款，所以state一定为0
		order.setState(0);
		// 因为此时还有填写收货人相关信息，所以收货人地址、收货人姓名、收货人联系方式均为null
		// 5.private String address; //收货人地址，注意收货人和当前登录的用户并不一定是同一个人
		order.setAddress(null);
		// 6.private String name; //收货人姓名
		order.setName(null);
		// 7.private String telephone; //收货人联系方式
		order.setTelephone(null);
		// 8.private User user; //该订单属于哪个用户，即当前登录的用户
		order.setUser(user);
		// 9.List<OrderItem> orderItems = new ArrayList<OrderItem>();
		// //该订单中包含的订单项
		// 该订单中包含的订单项OrderItem即购物车中的购物项CartItem
		Map<String, CartItem> cartItems = cart.getCartItem();
		for (Map.Entry<String, CartItem> item : cartItems.entrySet()) {
			CartItem cartItem = item.getValue();
			OrderItem orderItem = new OrderItem();
			// 1.private String itemid; //该订单项的编号
			orderItem.setItemid(CommonUtils.getUUID());
			// 2.private int count; //该订单项商品的购买数量
			orderItem.setCount(cartItem.getBuyNum());
			// 3.private double subtotal; //该订单项金额小计
			orderItem.setSubtotal(cartItem.getSubtotal());
			// 4.private Product product; //该订单项的商品
			orderItem.setProduct(cartItem.getProduct());
			// 5.private Order order; //该订单项属于哪个订单（一个订单项只能属于一个订单，一个订单下可以包含多个订单项）
			orderItem.setOrder(order);
			// 将该订单项添加到订单中
			order.getOrderItems().add(orderItem);
		}

		// 到此Order对象封装完毕
		// 将Order对象传递给service层
		ProductService productService = new ProductService();
		productService.submitOrder(order);

		session.setAttribute("order", order);
		// 页面重定向到order_info.jsp页面
		response.sendRedirect(request.getContextPath() + "/order_info.jsp");
	}

	// 清空购物车-----删除购物车中的所有商品
	public void clearCart(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("cart");
		// 将页面重定向到cart.jsp页面
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}

	// 从购物车中删除指定的商品
	public void delProductFromCart(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart != null) {
			Map<String, CartItem> cartItems = cart.getCartItem();
			CartItem cartItem = cartItems.get(pid);
			cart.setTotal(cart.getTotal() - cartItem.getSubtotal());
			cartItems.remove(pid);
			cart.setCartItem(cartItems);
		}
		session.setAttribute("cart", cart);
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}

	// 向购物车中添加商品
	public void addProductToCart(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();

		ProductService service = new ProductService();

		// 获得要放到购物车的商品的pid
		String pid = request.getParameter("pid");
		// 获得该商品的购买数量
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));

		// 获得product对象
		Product product = service.findProductByPid(pid);
		// 计算小计
		double subtotal = product.getShop_price() * buyNum;
		// 封装CartItem
		CartItem item = new CartItem();
		item.setProduct(product);
		item.setBuyNum(buyNum);
		item.setSubtotal(subtotal);

		// 获得购物车---判断是否在session中已经存在购物车
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
		}

		// 将购物项放到车中---key是pid
		// 先判断购物车中是否已将包含此购物项了 ----- 判断key是否已经存在
		// 如果购物车中已经存在该商品----将现在买的数量与原有的数量进行相加操作
		Map<String, CartItem> cartItems = cart.getCartItem();

		double newsubtotal = 0.0;

		if (cartItems.containsKey(pid)) {
			// 取出原有商品的数量
			CartItem cartItem = cartItems.get(pid);
			int oldBuyNum = cartItem.getBuyNum();
			oldBuyNum += buyNum;
			cartItem.setBuyNum(oldBuyNum);
			cart.setCartItem(cartItems);
			// 修改小计
			// 原来该商品的小计
			double oldsubtotal = cartItem.getSubtotal();
			// 新买的商品的小计
			newsubtotal = buyNum * product.getShop_price();
			cartItem.setSubtotal(oldsubtotal + newsubtotal);

		} else {
			// 如果车中没有该商品
			cart.getCartItem().put(product.getPid(), item);
			newsubtotal = buyNum * product.getShop_price();
		}

		// 计算总计
		double total = cart.getTotal() + newsubtotal;
		cart.setTotal(total);

		// 将车再次访问session
		session.setAttribute("cart", cart);

		// 直接跳转到购物车页面
		response.sendRedirect(request.getContextPath() + "/cart.jsp");
	}

	public void categoryList(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 从连接池中取出一个Jedis连接
		Jedis jedis = JedisPoolUtil.getJedis();
		// 从jedis缓存中取出商品分类的json字符串
		String categoryListJson = jedis.get("categoryListJson");
		// 如果是第一次访问，说明缓存中没有数据
		if (categoryListJson == null) {
			System.out.println("缓存中没有数据");
			CategoryService categoryService = new CategoryService();
			List<Category> categoryList = categoryService.findCategoryList();
			Gson gson = new Gson();
			categoryListJson = gson.toJson(categoryList);
			// 从数据库中查找商品分类转换成json字符串存储到jedis缓存中
			jedis.set("categoryListJson", categoryListJson);
		}
		// 解决响应中文乱码问题
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(categoryListJson);
	}

	public void index(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ProductService productService = new ProductService();
		// 准备热门商品数据---List<Product> hotProductList
		List<Product> hotProductList = productService.findHotProductList();
		// 准备最新商品数据---List<Product> newProductList
		List<Product> newProductList = productService.findNewProductList();
		// 将商品数据放到request域对象中转发给index.jsp页面进行显示
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);
		request.getRequestDispatcher("/index.jsp").forward(request, response);
	}

	public void productInfo(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pid = request.getParameter("pid");
		String cid = request.getParameter("cid");
		String currentPage = request.getParameter("currentPage");
		ProductService productService = new ProductService();
		Product product = productService.findProductByPid(pid);

		// 获得客户端携带的名字为pids的cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					pids = cookie.getValue();
					String[] split = pids.split("-");
					// 将数组转换成List集合
					List<String> asList = Arrays.asList(split);
					// 使用LinkedList存储pid集合，因为LinkedList添加删除效率的高于ArrayList
					// 构造方法要求传入一个集合，所以将数组转换为集合后再传入到LinkedList的构造方法中
					LinkedList<String> list = new LinkedList<String>(asList);
					// 判断list中是否已包含本次传过来的pid，如果包含，则将这个pid先从集合中删除，然后再添加到集合的最前面。
					if (list.contains(pid)) {
						list.remove(pid);
					}
					list.addFirst(pid);
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < list.size() & i < 7; i++) {
						sb.append(list.get(i));
						sb.append("-");
					}
					pids = sb.substring(0, sb.length() - 1);
				}
			}
		}
		Cookie cookie_pids = new Cookie("pids", pids);
		response.addCookie(cookie_pids);
		request.setAttribute("product", product);
		request.setAttribute("cid", cid);
		request.setAttribute("currentPage", currentPage);
		request.getRequestDispatcher("/product_info.jsp").forward(request,
				response);
	}

	public void productListByCid(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String currentPageStr = request.getParameter("currentPage");
		// System.out.println(currentPageStr);
		String cid = request.getParameter("cid");

		int currentPage = 1;
		if (currentPageStr == null || "".equals(currentPageStr))
			currentPage = 1;
		else
			currentPage = Integer.parseInt(currentPageStr);

		int currentCount = 12;
		ProductService productService = new ProductService();
		PageBean<Product> pageBean = productService.findProductListByCid(cid,
				currentPage, currentCount);
		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);

		List<Product> historyProductList = new ArrayList<Product>();
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("pids".equals(cookie.getName())) {
					String pids = cookie.getValue();
					String[] split = pids.split("-");
					for (String pid : split) {
						Product product = productService.findProductByPid(pid);
						historyProductList.add(product);
					}
				}
			}
		}

		// 将历史记录放到request域中传给porduct_list.jsp页面
		request.setAttribute("historyProductList", historyProductList);

		request.getRequestDispatcher("/product_list.jsp").forward(request,
				response);
	}

}