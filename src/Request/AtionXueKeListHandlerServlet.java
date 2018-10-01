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

import BLL.ZjclsMngr;
import BLL.KuaiBaoMngr;
import BLL.MeetingMngr;
import Util.Common;

/**
 * Servlet implementation class DownloadFileListHandlerServlet
 */
@WebServlet("/AtionXueKeListHandler.do")
public class AtionXueKeListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AtionXueKeListHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getAtionCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getAtionList(request);
		} else if ("delfile".equals(request.getParameter("do"))) {
			strResult = delAtionFile(request);
		} else if("usercount".equals(request.getParameter("do"))){
			strResult = getUserCount(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	private String getAtionCount(HttpServletRequest request) throws ServletException, IOException {
		//String strXueKeName = request.getParameter("xn");
		String strUserName = request.getParameter("un");
		String strXueKeCode = request.getParameter("xi");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(ZjclsMngr.getZjclsCount(strXueKeCode, strUserName, strStartDate, strEndDate));
	}

	private String getAtionList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
//		String strXueKeName = request.getParameter("xn");
		String strUserName = request.getParameter("un");
		String strXueKeCode = request.getParameter("xi");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstFiles = null;
		lstFiles = ZjclsMngr.getZjclsList(strXueKeCode, strUserName, strStartDate, strEndDate, iStart, iLength);
		if (lstFiles == null) {
			return "";
		}
		for(Map<String, Object> map :lstFiles){
			String code=(String)map.get("sortcode");
			map.put("name", ZjclsMngr.getNameByCode(code));
		}
		sbHtml.append("<table width=\"100%\" name=\"tabfiles\" id=\"tabfiles\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		//sbHtml.append("<th width=\"40\">&nbsp;</th>");
		sbHtml.append("<th width=\"5%\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"15%\">用户名</th>");
		sbHtml.append("<th>学科</th>");
		sbHtml.append("<th width=\"15%\">学科code</th>");
		sbHtml.append("<th width=\"20%\">时间</th>");
		sbHtml.append("<th width=\"50px\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iFile = lstFiles.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iFile.hasNext()) { //id,username,filename,fileid,client,address,time
			iMap = iFile.next();
			sbHtml.append("<tr>");
			//sbHtml.append("<td><input name=\"chkfileid\" value=\"").append(iMap.get("fileid")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("name")==null?"":iMap.get("name")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("sortcode")==null?"":iMap.get("sortcode")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delDownloadFile('").append(iMap.get("id")).append("','").append(iMap.get("username")).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private String delAtionFile(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String strFileID = request.getParameter("fid");
		String strUserName=request.getParameter("uname");
		if (Common.IsNullOrEmpty(strFileID)||Common.IsNullOrEmpty(strUserName)) {
			return "0";
		}
		if (ZjclsMngr.delAttention(strUserName, strFileID)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}
	
	private String getUserCount(HttpServletRequest request) throws ServletException, IOException {
		String strUserName = request.getParameter("un");
		String strXueKeCode = request.getParameter("xi");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		return String.valueOf(ZjclsMngr.getXueKeUserCount(strXueKeCode, strUserName, strStartDate, strEndDate));
	}

}
