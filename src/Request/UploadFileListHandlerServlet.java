package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.UploadMngr;
import Util.Common;

/**
 * Servlet implementation class UploadFileListHandlerServlet
 */
@WebServlet("/UploadFileListHandler.do")
public class UploadFileListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UploadFileListHandlerServlet() {
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
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getFileCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getFileList(request);
		} else if ("delfile".equals(request.getParameter("do"))) {
			strResult = delUploadFile(request);
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

	private String getFileCount(HttpServletRequest request) throws ServletException, IOException {
		String strFileName = request.getParameter("fn");
		String strUserName = request.getParameter("un");
		String strIsDelete = request.getParameter("isdel");
		String strFileType = request.getParameter("ft");
		return String.valueOf(UploadMngr.getFileCount(strFileName, strUserName, strFileType, strIsDelete));
	}

	private String getFileList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String strFileName = request.getParameter("fn");
		String strUserName = request.getParameter("un");
		String strIsDelete = request.getParameter("isdel");
		String strFileType = request.getParameter("ft");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstFiles = null;
		lstFiles = UploadMngr.getFileList(strFileName, strUserName, strFileType, strIsDelete, iStart, iLength);
		if (lstFiles == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" name=\"tabfiles\" id=\"tabfiles\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone tabpadno\">");
		sbHtml.append("<tr>");
		// sbHtml.append("<th width=\"40\">&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"9%\">文件名</th>");
		sbHtml.append("<th width=\"95\">文件类型</th>");
		sbHtml.append("<th width=\"75\">文件大小</th>");
		sbHtml.append("<th width=\"9%\">上传用户</th>");
		sbHtml.append("<th width=\"60\">是否完整</th>");
		sbHtml.append("<th>终端信息</th>");
		sbHtml.append("<th width=\"10%\">地址</th>");
		sbHtml.append("<th width=\"13%\">上传时间</th>");
		sbHtml.append("<th width=\"72\">epub下载</th>");
		sbHtml.append("<th width=\"40\">下载</th>");
		sbHtml.append("<th width=\"50\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iFile = lstFiles.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iFile.hasNext()) {
			iMap = iFile.next();
			sbHtml.append("<tr>");
			// sbHtml.append("<td><input name=\"chkfileid\" value=\"").append(iMap.get("fileid")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tdpadleft\">").append(iMap.get("filename")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("typename")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Integer.parseInt(String.valueOf(iMap.get("filesize"))) / 1024).append("KB</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(iMap.get("iscompleted"))) ? "是" : "否").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("client") == null ? "" : iMap.get("client")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address") == null ? "" : iMap.get("address")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			if ("1".equals(String.valueOf(iMap.get("ishasepub")))) {
				sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.open('download/getfile?filename='+encodeURI(\'").append(iMap.get("filename")).append("\')+'&fileid=").append(iMap.get("fileid")).append("&typeid=").append(iMap.get("typeid")).append("&username=").append(iMap.get("username")).append("&filetype=epub','','width=10,height=10,top='+(window.screen.availHeight-30-50)/2+',left='+(window.screen.availWidth-10-50)/2+',toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');\">epub下载</a></td>");
			} else {
				sbHtml.append("<td class=\"tabcent\">无</td>");
			}
			if ("1".equals(String.valueOf(iMap.get("iscompleted")))) {
				sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.open('download/getfile?filename='+encodeURI(\'").append(iMap.get("filename")).append("\')+'&fileid=").append(iMap.get("fileid")).append("&typeid=").append(iMap.get("typeid")).append("&username=").append(iMap.get("username")).append("&filetype=").append(iMap.get("typename")).append("','','width=10,height=10,top='+(window.screen.availHeight-30-50)/2+',left='+(window.screen.availWidth-10-50)/2+',toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no');\">下载</a></td>");
			}else{
				sbHtml.append("<td class=\"tabcent\"><span title=\"文件不全，咱不能下载\">无</span></td>");
			}
			if ("1".equals(String.valueOf(iMap.get("isdelete")))) {
				sbHtml.append("<td class=\"tabcent\"><em class=\"fine\">已删除<em></td>");
			} else {
				sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delUploadFile('").append(iMap.get("fileid")).append("','").append(iMap.get("username")).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			}
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delUploadFile(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String strFileID = request.getParameter("fid");
		String strUserName = request.getParameter("uname");
		if (Common.IsNullOrEmpty(strFileID) || Common.IsNullOrEmpty(strUserName)) {
			return "0";
		}
		// if (strFileID.endsWith(",")) {
		// strFileID = Common.Trim(strFileID, ",");
		// }
		if (UploadMngr.deleteFile(strFileID, strUserName)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}

}
