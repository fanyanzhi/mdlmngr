package SysMngr;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Iterator;
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

/**
 * Servlet implementation class SubjectRecommendServlet
 */
@WebServlet("/SubjectRecommend.do")
public class SubjectRecommendServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubjectRecommendServlet() {
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
		String id = request.getParameter("id");
		List<Map<String, Object>> list = SubjectRecommendMngr.getSubjectInfo(id);
		if (list != null && list.size() > 0) {
			Map<String, Object> map = list.get(0);
			SubjectRecommendInfoBean subject = new SubjectRecommendInfoBean();
			subject.setSubjectid((String) map.get("subjectid"));
			subject.setTitle((String) map.get("title"));
			subject.setKeyword((String) map.get("keyword"));
			subject.setType((String) map.get("type"));
			subject.setLinktype((int) map.get("linktype"));
			subject.setIstop((int) map.get("istop"));
			subject.setIsadv((int) map.get("isadv"));
			subject.setOpenclass((int) map.get("openclass"));
			subject.setIsrecomd((int) map.get("isrecomd"));
			if (map.get("summary") != null) {
				subject.setSummary((String) map.get("summary"));
			}
			if (map.get("simageid") != null) {
				subject.setSimageid((int) map.get("simageid"));
			}
			if (map.get("bimageid") != null) {
				subject.setBimageid((int) map.get("bimageid"));
			}
			request.setAttribute("subject", subject);
			request.setAttribute("sign", 1);
			request.setAttribute("id", id);
		}
		request.getRequestDispatcher("/SysMngr/subjectrecommend.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/plain;charset=gbk");
		PrintWriter pw = response.getWriter();
		try {
			@SuppressWarnings("deprecation")
			DefaultFileItemFactory diskFactory = new DefaultFileItemFactory();
			diskFactory.setSizeThreshold(4 * 1024);
			diskFactory.setRepository(new File("d:\\"));
			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			upload.setSizeMax(4 * 1024 * 1024);
			List fileItems = upload.parseRequest(request);
			Iterator iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					System.out.println("处理表单内容 ...");
					processFormField(item, pw);
				} else {
					System.out.println("处理上传的文件 ...");
					//processUploadFile(item, pw);
					BufferedReader br=new BufferedReader(new InputStreamReader(item.getInputStream()));
					String line="";
			        String[] arrs=null;
			        while ((line=br.readLine())!=null) {
			            arrs=line.split(",");
			            System.out.println(arrs[0] + " : " + arrs[1] + " : " + arrs[2]);
			        }
				}
			}
			pw.close();
		} catch (Exception e) {
			System.out.println("使用 fileupload 包时发生异常 ...");
			e.printStackTrace();
		} //
	}

	private void processFormField(FileItem item, PrintWriter pw) throws Exception {
		String name = item.getFieldName();
		String value = item.getString();
		pw.println(name + " : " + value + "\r\n");
	}

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
