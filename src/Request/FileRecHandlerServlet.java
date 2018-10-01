package Request;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import BLL.Logger;

/**
 * Servlet implementation class ImgRecHandlerServlet
 */
@WebServlet("/FileRecHandler.do")
public class FileRecHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public FileRecHandlerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		request.setCharacterEncoding("utf-8");
		String sign = request.getParameter("sign");
		if("1".equals(sign)){
			System.out.println("delete");
			deleteFile(request,response);
		}
		else{
			uploadFile(request,response);
		}
	}
	@SuppressWarnings("unchecked")
	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String strTempPath = request.getParameter("fileurl");
		DiskFileItemFactory factory = new DiskFileItemFactory();
		ServletFileUpload upload = new ServletFileUpload(factory);
		PrintWriter out = response.getWriter();
		try {
			List<FileItem> items = upload.parseRequest(request);
		    Iterator<FileItem> itr = items.iterator();
		    while (itr.hasNext()) {
		    	FileItem item = (FileItem) itr.next();
		    	if (item.isFormField()) {
		    		System.out.println("表单参数名:" + item.getFieldName() + "，表单参数值:" + item.getString("UTF-8"));
		    	} else{
		    		if (item.getName() != null && !item.getName().equals("")) {
		    			File f = new File(strTempPath);
		    			if(!f.exists()){
		    				f.mkdirs();
		    			}
		    			File tempFile = new File(item.getName());
		    			File file = new File(strTempPath,tempFile.getName());
		    			item.write(file);
		    			out.println("{message:\"上传文件成功！\" }");
				     }else{
				    	 out.println("{message:\"没有选择上传文件！\" }");
				     }
		    	}
		   }
		  }catch(FileUploadException e){
			  Logger.WriteException(e);
		  } catch (Exception e) {
			  Logger.WriteException(e);
			  request.setAttribute("upload.message", "上传文件失败！");
			  out.println("{message:\"上传文件失败！\" }");
		}
	}
	private void deleteFile(HttpServletRequest request, HttpServletResponse response) throws IOException{
		String strTempPath = request.getParameter("fileurl");
		File file = new File(strTempPath);
		boolean result = false;
		result = delete(file);
		JSONObject jsonObject = new JSONObject();
		if(result){
		    jsonObject.accumulate("message", "文件删除成功！");
		}else{
			jsonObject.accumulate("message", "文件删除失败！");
		}
		response.setContentType("application/json");
	    response.getWriter().write(jsonObject.toString());
	}
	public static boolean delete(File file){
		 boolean result = false;
			if(file.exists()){
				if (file.isFile()) {
					result= file.delete();
	            } else if (file.isDirectory()) {
	                File files[] = file.listFiles();
	                for (int i = 0; i < files.length; i++) {
	                    delete(files[i]);
	                }
	                result = file.delete();
	            }
			}
			return result;
	 }
}
