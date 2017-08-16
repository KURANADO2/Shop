<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!-- 登录 注册 购物车... -->
<div class="container-fluid">
	<div class="col-md-4">
		<img src="img/logo2.png" />
	</div>
	<div class="col-md-5">
		<img src="img/header.png" />
	</div>
	<div class="col-md-3" style="padding-top:20px">
		<ol class="list-inline">
			<c:if test="${empty sessionScope.user }">
				<li><a href="login.jsp">登录</a></li>
				<li><a href="register.jsp">注册</a></li>
			</c:if>
			<c:if test="${!empty sessionScope.user }">
				<li><a href="#">欢迎你，${sessionScope.user.username }</a></li>
				<li><a href="${pageContext.request.contextPath }/userServlet?method=loginOut">注销</a></li>
			</c:if>
			<li><a href="cart.jsp">购物车</a></li>
			<li><a href="${pageContext.request.contextPath }/productServlet?method=myOrders">我的订单</a></li>
		</ol>
	</div>
</div>

<!-- 导航条 -->
<div class="container-fluid">
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">首页</a>
			</div>

			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav" id="categoryListUI">
					
				</ul>
				
				<form class="navbar-form navbar-right" role="search" action="productServlet?method=productInfo&pid=5&cid=1&currentPage=1" method="post">
					<div class="form-group" style="position: relative;">
						<input id="search" type="text" class="form-control" style="width: 300px;" placeholder="Search">
						<div id="showDiv" style="border-radius: 5px; box-shadow: 10px 10px 5px #888888; display: none; position:absolute;z-index:1000;background:#fff; width:300px; border:1px solid #ccc;"></div>
					</div>
					<button type="submit" class="btn btn-default">搜索</button>
				</form>
				<!-- 完成异步搜索提示 -->
				<!-- 因为此页面会被包含到index.jsp页面中，而index.jsp页面中已经引入和jQuery类库,所以此处不需要再重复引入jQuery类库 -->
				<script type="text/javascript">
					//点击搜索按钮时跳转到productServetl
					//鼠标移动到某一商品名称上时改变该商品名称所在div的背景颜色
					function mouseover(obj) {
						$(obj).css("background", "#3388FF");
					}
					function mouseout(obj) {
						$(obj).css("background", "#fff");
					}
					//当用户点击搜索提示列表中的某一个商品名称时触发事件
					function selectWord(obj) {
						//$(obj).html()能够取到obj中的内容，但$(obj).val()却取不到
						//alert($(obj).html());
						//让搜索框显示用户点击的商品名称
						$("#search").val($(obj).html());
						//隐藏showDiv(搜索提示的列表)
						$("#showDiv").css("display", "none");
					}
					$(function() {
						//alert();
						$.ajax({
							"async": true,
							"url": "${pageContext.request.contextPath}/productServlet?method=categoryList",
							"success": function(data) {
								var content = "";
								for(var i = 0; i < data.length; i ++) {
									//alert(data[i].cname);
									content += "<li><a href='${pageContext.request.contextPath}/productServlet?method=productListByCid&cid=" + data[i].cid + "'>" + data[i].cname + "</a></li>";
								}
								$("#categoryListUI").html(content);
							},
							"type": "POST",
							"dataType": "json"
						});
						$("#search").bind('keyup', function() {
							//alert($(this).val());
							$.ajax({
								//采用异步请求方式
								async : true,
								//请求的url地址
								url : "${pageContext.request.contextPath}/searchWordServlet",
								//请求参数
								data : {"word" : $(this).val()},
								//成功时的回调函数，data为服务器返回的响应数据，格式为json
								success : function(data) {
									//当返回的数据（返回的是一个数组）长度>0时
									if(data.length > 0) {
										$("#showDiv").css("display", "block");
										var content = "";
										for(var i = 0; i < data.length; i ++) {
											content += "<div style='padding: 5px 20px; cursor: pointer;' onmouseover='mouseover(this);' onmouseout='mouseout(this);' onclick='selectWord(this);'>" + data[i] + "</div>";
											$("#showDiv").html(content);
										}
									} 
								},
								//POST方式提交请求
								type : "POST",
								//以json格式解析服务器返回的响应数据
								dataType : "json"
							});
						});
					});
				</script>
			</div>
		</div>
	</nav>
</div>