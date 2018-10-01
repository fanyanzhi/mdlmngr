package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.CnkiMngr;
import BLL.Pdf2Epub;
import BLL.SourceMngr;
import Util.Common;

/**
 * Servlet implementation class CnkiFileListHandlerServlet
 */
@WebServlet("/CnkiFileListHandler.do")
public class CnkiFileListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CnkiFileListHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";
		if ("getcount".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getCnkiFileCount(request);
		} else if ("getlist".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = getCnkiFileList(request);
		} else if ("tranepub".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = transEpub(request);
		} else if ("redown".equalsIgnoreCase(request.getParameter("do"))) {
			strResult = reDownCnkiFile(request);
		} 
//		else if ("vdetail".equalsIgnoreCase(request.getParameter("do"))) {
//			strResult = getCnkiFileDetail(request);
//		}
		out.write(strResult);
		out.flush();
		out.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	protected String getCnkiFileCount(HttpServletRequest request) {
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
		String epubstatus = request.getParameter("es");
		String name_en = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Integer size = null;
		if (!Common.IsNullOrEmpty(typeid)) {
			list = SourceMngr.getSourceType(typeid);
		}
		if (list != null && list.size() > 0) {
			name_en = (String) list.get(0).get("name_en");
			size = CnkiMngr.getEpubTransInfoCount(name_en, filter, keyword, epubstatus);
		}
		return String.valueOf(size);
	}

	protected String getCnkiFileList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
		String epubstatus = request.getParameter("es");
		String start = request.getParameter("start");
		String len = request.getParameter("len");

		String name_en = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> epubTransInfolist = new ArrayList<Map<String, Object>>();
		if (!Common.IsNullOrEmpty(typeid)) {
			list = SourceMngr.getSourceType(typeid);
		}
		if (list != null && list.size() > 0) {
			name_en = (String) list.get(0).get("name_en");
			epubTransInfolist = CnkiMngr.getEpubTransInfoList(name_en, filter, keyword, epubstatus, start, len);
		}
		Iterator<Map<String, Object>> iMap = epubTransInfolist.iterator();
		Map<String, Object> mapData = null;

		sbHtml.append("<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"15%\">文件ID</th>");
		sbHtml.append("<th width=\"30%\">文件名</th>");
		sbHtml.append("<th>文件大小</th>");
		sbHtml.append("<th>文件类型</th>");
		sbHtml.append("<th>是否有epub文件</th>");
		sbHtml.append("<th width=\"15%\">时间</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		String strValid = "";
		String fileidstr = null;
		String filenamestr = null;
		String filesizestr = null;
		String typenamestr = null;

		StringBuilder sbHostID = new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			strServerID = String.valueOf(mapData.get("id"));
			strValid = "1".equals(String.valueOf(mapData.get("ishasepub"))) ? "有" : "0".equals(String.valueOf(mapData.get("ishasepub"))) ? "无" : "null";
			if ("有".equals(strValid)) {
				sbHostID.append("tr").append(strServerID).append(";");
			}
			fileidstr = String.valueOf(mapData.get("fileid"));
			filenamestr = String.valueOf(mapData.get("filename"));
			filesizestr = String.valueOf(mapData.get("filesize"));
			typenamestr = String.valueOf(mapData.get("typename"));

			typenamestr = String.valueOf(mapData.get("typename"));
			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(fileidstr).append("</a></td>");

			sbHtml.append("<td class=\"tabcent\">").append(filenamestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(filesizestr).append("</td>");

			sbHtml.append("<td class=\"tabcent\">").append(typenamestr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(strValid).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			// sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='EpubServerInfo.do?sid=").append(strServerID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delEpubServer(").append(strServerID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		if (!Common.IsNullOrEmpty(sbHostID.toString())) {
			sbHostID.delete(sbHostID.length() - 1, sbHostID.length());
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidtrs\" id=\"hidtrs\" value=\"").append(sbHostID.toString()).append("\" />");
		return sbHtml.toString();
	}

	private String transEpub(HttpServletRequest request) {
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
		String epubstatus = request.getParameter("es");
		Integer size = null;
		String name_en = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> epubTransInfolist = new ArrayList<Map<String, Object>>();
		if (!Common.IsNullOrEmpty(typeid)) {
			list = SourceMngr.getSourceType(typeid);
		}
		if (list != null && list.size() > 0) {
			name_en = (String) list.get(0).get("name_en");
			size = CnkiMngr.getEpubTransInfoCount(name_en, filter, keyword, epubstatus);
			if (size > 0) {
				epubTransInfolist = CnkiMngr.getEpubTransInfoList(name_en, filter, keyword, epubstatus, String.valueOf(size));
			} else {
				return "0";
			}
			if (epubTransInfolist == null || epubTransInfolist.size() == 0) {
				return "0";
			}
		}
		return Pdf2Epub.pdfTransEpub(name_en, epubTransInfolist);
	}

	private String reDownCnkiFile(HttpServletRequest request) {
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
		String epubstatus = request.getParameter("es");
		Integer size = null;
		String name_en = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> epubTransInfolist = new ArrayList<Map<String, Object>>();
		if (!Common.IsNullOrEmpty(typeid)) {
			list = SourceMngr.getSourceType(typeid);
		}
		if (list != null && list.size() > 0) {
			name_en = (String) list.get(0).get("name_en");
			size = CnkiMngr.getEpubTransInfoCount(name_en, filter, keyword, epubstatus);
			if (size > 0) {
				epubTransInfolist = CnkiMngr.getEpubTransInfoList(name_en, filter, keyword, epubstatus, String.valueOf(size));
			} else {
				return "0";
			}
			if (epubTransInfolist == null || epubTransInfolist.size() == 0) {
				return "0";
			}
		}
		return CnkiMngr.reDownFile(name_en, epubTransInfolist);
	}

//	/**
//	 * 获取文件详细信息
//	 * @param request
//	 * @return
//	 */
//	private String getCnkiFileDetail(HttpServletRequest request) {
//		String typeid = request.getParameter("typeid");
//		String filter = request.getParameter("filter");
//		String keyword = request.getParameter("keyword");
//		String epubstatus = request.getParameter("es");
//		Integer size = null;
//		String name_en = "";
//		
//		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
//		List<Map<String, Object>> epubTransInfolist = new ArrayList<Map<String, Object>>();
//		
//		if (!Common.IsNullOrEmpty(typeid)) {
//			list = SourceMngr.getSourceType(typeid);
//		}
//		
//		if (list != null && list.size() > 0) {
//			name_en = (String) list.get(0).get("name_en");
//			size = CnkiMngr.getEpubTransInfoCount(name_en, filter, keyword, epubstatus);
//			if (size > 0) {
//				epubTransInfolist = CnkiMngr.getEpubTransInfoList(name_en, filter, keyword, epubstatus, String.valueOf(size));
//			} else {
//				return "0";
//			}
//			if (epubTransInfolist == null || epubTransInfolist.size() == 0) {
//				return "0";
//			}
//		}
//		return "";//CnkiMngr.getCnkiFileDetail(name_en, epubTransInfolist);
//	}

}
