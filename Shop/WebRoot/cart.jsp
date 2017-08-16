<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>黑马商城购物车</title>
<link rel="stylesheet" href="css/bootstrap.min.css" type="text/css" />
<script src="js/jquery-1.11.3.min.js" type="text/javascript"></script>
<script src="js/bootstrap.min.js" type="text/javascript"></script>
<!-- 引入自定义css文件 style.css -->
<link rel="stylesheet" href="css/style.css" type="text/css" />
<style>
body {
	margin-top: 20px;
	margin: 0 auto;
}

.carousel-inner .item img {
	width: 100%;
	height: 300px;
}

font {
	color: #3164af;
	font-size: 18px;
	font-weight: normal;
	padding: 0 10px;
}
.cart-empty {
    height: 98px;
    padding: 80px 0 120px;
    color: #333;
}
.cart-empty .message {
    height: 98px;
    padding-left: 341px;
    background: url(//misc.360buyimg.com/user/cart/css/i/no-login-icon.png) 250px 22px no-repeat;
}
.cart-empty .message ul {
    padding-top: 23px;
}
.cart-empty .message .txt {
    font-size: 14px;
}
.cart-empty .message li {
    line-height: 26px;
}
ul {
    list-style: none;
}
li {
    line-height: 26px;
}
.btn-1 {
    font-family: arial,"Microsoft YaHei";
    display: inline-block;
    *display: inline;
    *zoom: 1;
    height: 25px;
    line-height: 25px;
    background-color: #e74649;
    background-image: -moz-linear-gradient(top,#e74649,#df3134);
    background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0,#e74649),color-stop(1,#df3134));
    filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#e74649', endColorstr='#df3134', GradientType='0');
    -ms-filter: progid:DXImageTransform.Microsoft.gradient(startColorstr='#e74649', endColorstr='#df3134');
    background-image: linear-gradient(to top,#e74649 0,#df3134 100%);
    border-radius: 3px;
    color: #fff;
    font-size: 12px;
    font-weight: 400;
    padding: 0 10px;
    vertical-align: middle;
    cursor: pointer;
    border: 0;
    float: none;
    transition: all .2s ease-out;
}
.mr10 {
    margin-right: 10px;
}
.ftx-05, .ftx05 {
    color: #005ea7;
}
</style>
<script type="text/javascript">
	function delProductFromCart(pid) {
		if(confirm("确认要删除该商品吗？")) {
			location.href="${pageContext.request.contextPath}/productServlet?method=delProductFromCart&pid="+pid;
		}
	}
	function clearCart() {
		if(confirm("确认清空购物车吗？")) {
			location.href="${pageContext.request.contextPath}/productServlet?method=clearCart";
		}
	}
</script>
</head>

<body>
	<!-- 引入header.jsp -->
	<jsp:include page="/header.jsp"></jsp:include>
	<c:if test="${!empty cart.cartItem }">
		<div class="container">
			<div class="row">

				<div style="margin:0 auto; margin-top:10px;width:950px;">
					<strong style="font-size:16px;margin:5px 0;">订单详情</strong>
					<table class="table table-bordered">
						<tbody>
							<tr class="warning">
								<th>图片</th>
								<th>商品</th>
								<th>价格</th>
								<th>数量</th>
								<th>小计</th>
								<th>操作</th>
							</tr>
							<c:forEach items="${cart.cartItem }" var="entry">
								<tr class="active">
									<td width="60" width="40%"><input type="hidden" name="id"
										value="22"> <img
										src="${pageContext.request.contextPath }/${entry.value.product.pimage }"
										width="70" height="60">
									</td>
									<td width="30%"><a target="_blank">${entry.value.product.pname
											}</a>
									</td>
									<td width="20%">${entry.value.product.shop_price }</td>
									<td width="10%">${entry.value.buyNum }</td>
									<td width="15%"><span class="subtotal">￥${entry.value.subtotal
											}</span>
									</td>
									<!-- 需要将pid作为参数传给delProductFromCart函数，使用EL表达式完成，但注意必须需要为其加上单引号 -->
									<td><a href="javascript:;" class="delete"
										onclick="delProductFromCart('${entry.value.product.pid}')">删除</a>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</div>
			</div>

			<div style="margin-right:130px;">
				<div style="text-align:right;">
					<em style="color:#ff6600;"> 登录后确认是否享有优惠&nbsp;&nbsp; </em> 赠送积分: <em
						style="color:#ff6600;">${cart.total }</em>&nbsp; 商品金额: <strong
						style="color:#ff6600;">￥${cart.total }元</strong>
				</div>
				<div style="text-align:right;margin-top:10px;margin-bottom:10px;">
					<a href="javascript:void(0);" onclick="clearCart()" id="clear" class="clear">清空购物车</a>
					<a href="${pageContext.request.contextPath }/productServlet?method=submitOrder"> <input type="button" width="100" value="提交订单" name="submit" border="0" style="background: url('./images/register.gif') no-repeat scroll 0 0 rgba(0, 0, 0, 0); height:35px;width:100px;color:white;">
					</a>
				</div>
			</div>

		</div>
	</c:if>
	<c:if test="${empty cart.cartItem }">
		<div class="cart-empty">
			<div class="message">
				<ul>
					<li class="txt">购物车内暂时没有商品</li>
					<li><a href="#none" class="btn-1 login-btn mr10">登录</a> <a
						href="//www.jd.com/" class="ftx-05"> 去购物&gt; </a></li>
				</ul>
			</div>
		</div>
	</c:if>

	<!-- 引入footer.jsp -->
	<jsp:include page="/footer.jsp"></jsp:include>

</body>

</html>