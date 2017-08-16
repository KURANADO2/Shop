package cn.xinling.web.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import cn.xinling.domain.Category;
import cn.xinling.domain.Product;
import cn.xinling.service.AdminProductService;
import cn.xinling.service.ProductService;

public class AdminUpdateProductServlet extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		
		Product product = new Product();
		Map<String, Object> properties = new HashMap<String, Object>(); 
		
		//因为form标签中使用了enctype="multipart/form-data"，所以不能通过request.getParameter获得参数
		try {
			String tmp = this.getServletContext().getRealPath("tmp");
			DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory(1024 * 1024, new File(tmp));
			ServletFileUpload servletFileUpload = new ServletFileUpload(diskFileItemFactory);
			List<FileItem> fileItemList = servletFileUpload.parseRequest(request);
			for(FileItem fileItem : fileItemList) {
				boolean isFormField = fileItem.isFormField();
				if(isFormField) {
					String fieldName = fileItem.getFieldName();
					String fieldValue = fileItem.getString("UTF-8");
					properties.put(fieldName, fieldValue); 
				} else {
					String fileName = fileItem.getName();
					System.out.println(fileName);
					if(fileName != null && !"".equals(fileName)) {
						InputStream in = fileItem.getInputStream();
						String upload = this.getServletContext().getRealPath("upload");
						OutputStream out = new FileOutputStream(upload + "/" + fileName);
						IOUtils.copy(in, out);
						in.close();
						out.close();
						//删除临时文件
						fileItem.delete();
						properties.put("pimage", "upload/" + fileName);
					} 
				}
			}
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
		if(properties.get("pimage") == null) {
			properties.put("pimage", new ProductService().findProductByPid(properties.get("pid").toString()).getPimage());
		}
		
		//封装Product对象
		try {
			BeanUtils.populate(product, properties);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		
		//private Date pdate;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String format2 = format.format(new Date());
		Date parse = null;
		try {
			parse = format.parse(format2);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		product.setPdate(parse);	
		//private int pflag;//是否下架，1下架，0未下架
		product.setPflag(0);
		//private Category category;
		Category category = new Category();
		category.setCid(properties.get("cid").toString());
		product.setCategory(category);
		
		// 传递数据给service层
		new AdminProductService().updateProductById(product);
		// 跳转到AdminProductListServlt，因为不需要传递数据，所以习惯使用重定向，如果需要传递数据到AdminProductListServlet在使用request转发
		response.sendRedirect(request.getContextPath()
				+ "/adminProductListServlet");
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}