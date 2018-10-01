package SysMngr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

import BLL.OrgNoticeMngr;
import Util.Common;
import Util.ImportExcel;

/**
 * Servlet implementation class OrgNoticeServlet
 */
@WebServlet("/OrgNotice.do")
public class OrgNoticeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrgNoticeServlet() {
		super();
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
		String noticeId = request.getParameter("id");
		String noticeTitle = request.getParameter("sn");
		request.setAttribute("PageSize", 20);
		request.setAttribute("noticeId", noticeId);
		request.setAttribute("noticeTitle", noticeTitle);

		request.setAttribute("HandlerURL", "OrgNoticeHandler.do");
		request.getRequestDispatcher("/SysMngr/orgNotice.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter pw = response.getWriter();
		try {
			@SuppressWarnings("deprecation")
			DefaultFileItemFactory diskFactory = new DefaultFileItemFactory();
			diskFactory.setSizeThreshold(4 * 1024);
			ServletFileUpload upload = new ServletFileUpload(diskFactory);
			upload.setSizeMax(4 * 1024 * 1024);
			List fileItems = upload.parseRequest(request);
			Iterator iter = fileItems.iterator();
			String subjectid = "";
			boolean result = false;
			Map<String, String> map = new LinkedHashMap<String, String>();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				if (item.isFormField()) {
					if ("subjectid".equals(item.getFieldName())) {
						subjectid = item.getString();
					}
				} else {
					ImportExcel importExcel = new ImportExcel();
					String name = item.getName();
					InputStream inputStream = item.getInputStream();
					List<List<String>> list = importExcel.read(name, inputStream);
					if (null != list) {
						for (int i = 0, listSize = list.size(); i < listSize; i++) {
							List<String> rows = list.get(i);
							String key = rows.get(0) == null ? "" : rows.get(0);
							String value = rows.get(1) == null ? "" : rows.get(1);
							map.put(key, value);
						}

					}

				}

			}
			if (!Common.IsNullOrEmpty(subjectid) && map.size() > 0) {
				result = OrgNoticeMngr.importOrgNotice(subjectid, map);
			}
			if (result) {
				pw.write("{\"msg\":\"导入成功\"}");
			} else {
				pw.write("{\"msg\":\"导入失败\"}");
			}
			pw.flush();
			pw.close();
		} catch (Exception e) {
			System.out.println("使用 fileupload 包时发生异常 ...");
			e.printStackTrace();
		} //
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
