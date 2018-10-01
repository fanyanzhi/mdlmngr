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

import BLL.DownloadMngr;
import BLL.EpubTransMngr;
import BLL.SourceMngr;
import Util.Common;

/**
 * Servlet implementation class RecommendSearchListHandlerServlet
 */
@WebServlet("/EpubTransServerListHandler.do")
public class EpubTransServerListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public EpubTransServerListHandlerServlet() {
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
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getEpubTransCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		} else if("delrec".equals(request.getParameter("do"))){
			strResult = delODataRecord(request);
		}
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

	protected String getEpubTransCount(HttpServletRequest request) {
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
		String name_en = "";
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Integer size = null;
		if (!Common.IsNullOrEmpty(typeid)) {
			list = SourceMngr.getSourceType(typeid);
		}
		if (list != null && list.size() > 0) {
			name_en = (String) list.get(0).get("name_en");
			size = EpubTransMngr.getEpubTransInfoCount(name_en, filter, keyword);
		}
		return String.valueOf(size);
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String typeid = request.getParameter("typeid");
		String filter = request.getParameter("filter");
		String keyword = request.getParameter("keyword");
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
			epubTransInfolist = EpubTransMngr.getEpubTransInfoList(name_en, filter, keyword, start, len);
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
		//sbHtml.append("<th>是否有epub文件</th>");
		sbHtml.append("<th width=\"15%\">时间</th>");
		sbHtml.append("<th width=\"72\">epub下载</th>");
		/*sbHtml.append("<th width=\"40\">下载</th>");*/
		sbHtml.append("<th width=\"40\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		boolean isValid = false;
		String fileidstr = null;
		String filenamestr = null;
		String filesizestr = null;
		String typenamestr = null;

		StringBuilder sbHostID = new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			strServerID = String.valueOf(mapData.get("id"));
			isValid = "1".equals(String.valueOf(mapData.get("ishasepub")));
			if (isValid) {
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
			//sbHtml.append("<td class=\"tabcent\">").append(isValid ? "有" : "无").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			if (isValid) {
				sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.open('download/getfile?filename='+encodeURI(\'").append(filenamestr).append("\')+'&fileid=").append(fileidstr).append("&typeid=").append(name_en).append("&filetype=epub','','width=10,height=10,top='+(window.screen.availHeight-30-50)/2+',left='+(window.screen.availWidth-10-50)/2+',toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');\">epub下载</a></td>");
			} else {
				sbHtml.append("<td class=\"tabcent\">无</td>");
			}
//			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.open('download/getfile?filename='+encodeURI(\'").append(filenamestr).append("\')+'&fileid=").append(fileidstr).append("&typeid=").append(name_en).append("&filetype=caj','','width=10,height=10,top='+(window.screen.availHeight-30-50)/2+',left='+(window.screen.availWidth-10-50)/2+',toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');\">下载</a></td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"delODataFile('").append(name_en).append("','").append(fileidstr).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			// sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='EpubServerInfo.do?sid=").append(strServerID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delEpubServer(").append(strServerID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		if (!Common.IsNullOrEmpty(sbHostID.toString())) {
			sbHostID.delete(sbHostID.length() - 1, sbHostID.length());
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidtrs\" id=\"hidtrs\" value=\"").append(sbHostID.toString()).append("\" />");
		return sbHtml.toString();
	}
	private String delODataRecord(HttpServletRequest request){
		String strResult = "1";
		String odataType = request.getParameter("otype");
		String fileid = request.getParameter("fileid");
		if (Common.IsNullOrEmpty(odataType)||Common.IsNullOrEmpty(fileid)) {
			return "0";
		}
		if (EpubTransMngr.delODataRecord(odataType,fileid)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
		
	}
}
