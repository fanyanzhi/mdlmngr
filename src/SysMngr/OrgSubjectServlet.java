package SysMngr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.DefaultFileItemFactory;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import BLL.SubjectRecommendMngr;
import Model.SubjectRecommendInfoBean;
import Util.Common;

/**
 * Servlet implementation class OrgSubjectServlet
 */
@WebServlet("/OrgSubject.do")
public class OrgSubjectServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgSubjectServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String subjectid = request.getParameter("id");
		String subjectName = request.getParameter("sn");
		request.setAttribute("PageSize", 20);
		request.setAttribute("subjectid", subjectid);
		request.setAttribute("subjectname", subjectName);
		
		request.setAttribute("HandlerURL", "OrgSubjectHandler.do");
		request.getRequestDispatcher("/SysMngr/orgsubject.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		response.setContentType("text/plain;charset=utf-8");
		response.setContentType("text/html;charset=UTF-8");  
		PrintWriter pw = response.getWriter();
		try {
			@SuppressWarnings("deprecation")
			DefaultFileItemFactory diskFactory = new DefaultFileItemFactory();
			diskFactory.setSizeThreshold(4 * 1024);
			//diskFactory.setRepository(new File("d:\\"));
			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			upload.setSizeMax(4 * 1024 * 1024);
			List fileItems = upload.parseRequest(request);
			Iterator iter = fileItems.iterator();
			String subjectid= "";
			boolean result = false;
			Map<String, String> map = new LinkedHashMap<String, String>();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					if("subjectid".equals(item.getFieldName())){
					subjectid = item.getString();
					}
				} else {
					BufferedReader br = new BufferedReader(new InputStreamReader(item.getInputStream(),"UTF-8"));
					String line = "";
					String[] arrs = null;
					while ((line = br.readLine()) != null) {
						arrs = line.split(",");
						if (arrs.length > 0) {
							map.put(arrs[0], arrs[1]);
						} else {
							map.put(arrs[0], "");
						}
					}
				}
			}
			if(!Common.IsNullOrEmpty(subjectid)&& map.size()>0){
				result = SubjectRecommendMngr.exportSubjectOrg(subjectid,map);
			}
			if(result){
				pw.write("{\"msg\":\"导入成功\"}");
			}else{
				pw.write("{\"msg\":\"导入失败\"}");
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			System.out.println("使用 fileupload 包时发生异常 ...");
			e.printStackTrace();
		} //
	}

//	private void processFormField(FileItem item, PrintWriter pw) throws Exception {
//		String name = item.getFieldName();
//		String value = item.getString();
//		pw.println(name + " : " + value + "\r\n");
//	}

	private void processUploadFile(FileItem item, PrintWriter pw) throws Exception {
		String filename = item.getName();
		System.out.println("完整的文件名：" + filename);
		int index = filename.lastIndexOf("\\");
		filename = filename.substring(index + 1, filename.length());
		long fileSize = item.getSize();
		if ("".equals(filename) && fileSize == 0) {
			System.out.println("文件名为空 ...");
			return;
		}
		File uploadFile = new File("d:\\" + "/" + filename);

		item.write(uploadFile);
		pw.println(filename + " 文件保存完毕 ...");
		pw.println("文件大小为 ：" + fileSize + "\r\n");
	}
}
