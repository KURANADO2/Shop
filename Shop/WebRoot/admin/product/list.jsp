<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<HTML>
<HEAD>
<meta http-equiv="Content-Language" content="zh-cn">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link href="${pageContext.request.contextPath}/css/Style1.css"
	rel="stylesheet" type="text/css" />
<script language="javascript"
	src="${pageContext.request.contextPath}/js/public.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath }/js/jquery-1.11.3.min.js"></script>
<script type="text/javascript">
	function addProduct() {
		window.location.href = "${pageContext.request.contextPath}/adminAddProductUIServlet";
	}
	function delProduct(pid) {
		var isDel = confirm("确认删除?");
		if(isDel)
			location.href = "${pageContext.request.contextPath}/adminDeleteProductServlet?pid=" + pid;
	}
	$(function() {
		$("#is_hot option[value='${requestScope.condition.is_hot}']").prop("selected", true);
		$("#cid option[value='${requestScope.condition.cid}']").prop("selected", true);
	});
</script>
</HEAD>
<body>
	<br>
	<form id="Form1" name="Form1"
		action="${pageContext.request.contextPath}/adminSearchProductListServlet"
		method="post">
		商品名称:<input tyep="text" name="pname" value="${requestScope.condition.pname }">
		是否热门:<select name="is_hot" id="is_hot">
				<option value="">不限</option>	<!-- 这个value必须指定为空字符串，而不能省略掉value属性不写，如果不写，则servlet接收到的请求参数将是字符串"不限"而不是空字符串"",导致dao层查不到相应数据 -->
				<option value="0">否</option>
				<option value="1">是</option>
			  </select>
		商品分类:<select name="cid" id="cid">
				<option value="">不限</option>	<!-- 同理这一项必须为其设置value属性，并且值为空字符串""，而不能省略掉value属性不写 -->
				<c:forEach items="${requestScope.categoryList }" var="category">
					<option value="${category.cid }">${category.cname }</option>
				</c:forEach>
			  </select>
		<input type="submit" value="搜索">
		<table cellSpacing="1" cellPadding="0" width="100%" align="center"
			bgColor="#f5fafe" border="0">
			<TBODY>
				<tr>
					<td class="ta_01" align="center" bgColor="#afd1f3"><strong>商品列表</strong>
					</TD>
				</tr>
				<tr>
					<td class="ta_01" align="right">
						<button type="button" id="add" name="add" value="添加"
							class="button_add" onclick="addProduct()">添加</button></td>
				</tr>
				<tr>
					<td class="ta_01" align="center" bgColor="#f5fafe">
						<table cellspacing="0" cellpadding="1" rules="all"
							bordercolor="gray" border="1" id="DataGrid1"
							style="BORDER-RIGHT: gray 1px solid; BORDER-TOP: gray 1px solid; BORDER-LEFT: gray 1px solid; WIDTH: 100%; WORD-BREAK: break-all; BORDER-BOTTOM: gray 1px solid; BORDER-COLLAPSE: collapse; BACKGROUND-COLOR: #f5fafe; WORD-WRAP: break-word">
							<tr
								style="FONT-WEIGHT: bold; FONT-SIZE: 12pt; HEIGHT: 25px; BACKGROUND-COLOR: #afd1f3">

								<td align="center" width="18%">序号</td>
								<td align="center" width="17%">商品图片</td>
								<td align="center" width="17%">商品名称</td>
								<td align="center" width="17%">商品价格</td>
								<td align="center" width="17%">是否热门</td>
								<td width="7%" align="center">编辑</td>
								<td width="7%" align="center">删除</td>
							</tr>
							<c:forEach items="${requestScope.productList }"
								var="product" varStatus="vs">
								<tr onmouseover="this.style. ackgroundColor = 'white'"
									onmouseout="this.style.backgroundColor = '#F5FAFE';">
									<td style="CURSOR: hand; HEIGHT: 22px" align="center"
										width="18%">${vs.count }</td>
									<td style="CURSOR: hand; HEIGHT: 22px" align="center"
										width="17%"><img width="40" height="45"
										src="${pageContext.request.contextPath }/${product.pimage}">
									</td>
									<td style="CURSOR: hand; HEIGHT: 22px" align="center"
										width="17%">${product.pname }</td>
									<td style="CURSOR: hand; HEIGHT: 22px" align="center"
										width="17%">${product.shop_price }</td>
									<td style="CURSOR: hand; HEIGHT: 22px" align="center"
										width="17%">${product.is_hot == 1 ? '是' : '否' }</td>
									<td align="center" style="HEIGHT: 22px"><a
										href="${pageContext.request.contextPath }/adminUpdateProductUIServlet?pid=${product.pid }">
											<img
											src="${pageContext.request.contextPath}/images/i_edit.gif"
											border="0" style="CURSOR: hand"> </a>
									</td>

									<td align="center" style="HEIGHT: 22px"><a
										href="javascript:void(0);"
										onclick="delProduct('${product.pid}')"> <img
											src="${pageContext.request.contextPath}/images/i_del.gif"
											width="16" height="16" border="0" style="CURSOR: hand">
									</a></td>
								</tr>
							</c:forEach>
						</table></td>
				</tr>

			</TBODY>
		</table>
	</form>
</body>
</HTML>

