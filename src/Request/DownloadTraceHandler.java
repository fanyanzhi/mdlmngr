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

import BLL.Logger;
import BLL.Logger.DownloadTraceStatus;
import Util.Common;

@WebServlet("/DownloadTraceHandler.do")
public class DownloadTraceHandler extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getDownloadTraceCount(request);
		} 
		else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getDownloadTraceList(request);
		} 
		else if ("delfile".equals(request.getParameter("do"))) {
			strResult = delDownloadTraceInfo(request);
		}else if ("delfileAll".equals(request.getParameter("do"))) {
			strResult = delDownloadTraceAllInfo(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}
	
	private String getDownloadTraceCount(HttpServletRequest request) throws ServletException, IOException {
		String userName = request.getParameter("username");
		String fileId = request.getParameter("fileId");
		String operation = request.getParameter("operation");
		String opstatus = request.getParameter("opstatus");
		
		return String.valueOf(Logger.getDownloadTraceCount(userName,fileId,operation,opstatus));
	}
	
	private String getDownloadTraceList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String userName = request.getParameter("username");
		String fileId = request.getParameter("fileId");
		String operation = request.getParameter("operation");
		String opstatus = request.getParameter("opstatus");
		int iStart = Integer.parseInt(request.getParameter("start"));
		int iLength = Integer.parseInt(request.getParameter("len"));

		List<Map<String, Object>> lstFiles = null;
		lstFiles = Logger.getDownloadTraceList(userName, fileId,operation,opstatus, iStart, iLength);
		if (lstFiles == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" name=\"tabfiles\" id=\"tabfiles\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20px\">&nbsp;</th>");
		sbHtml.append("<th width=\"40px\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"120px\">用户名</th>");
		sbHtml.append("<th width=\"80px\">文件类型</th>");
		sbHtml.append("<th width=\"150px\">文件id</th>");
		sbHtml.append("<th width=\"100px\">操作</th>");
		sbHtml.append("<th>数据</th>");
		sbHtml.append("<th width=\"10%\">地址</th>");
		sbHtml.append("<th width=\"10%\">客户端</th>");
		sbHtml.append("<th width=\"10%\">时间</th>");
		sbHtml.append("<th width=\"50px\">执行结果</th>");
		sbHtml.append("<th width=\"50px\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iFile = lstFiles.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		String statusStr = "";
		while (iFile.hasNext()) {
			iMap = iFile.next();
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chktraceid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td>").append(iMap.get("username")==null?"":iMap.get("username")).append("</td>");
			sbHtml.append("<td>").append(iMap.get("typeid")==null?"":iMap.get("typeid")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("fileid")==null?"":iMap.get("fileid")).append("</td>");
			
			DownloadTraceStatus[] status = DownloadTraceStatus.values ();
		       for (DownloadTraceStatus s : status) {
		    	   if(s.value==(int)iMap.get("operation")){
		    		   statusStr = s.name(); 
		    	   }
		       }
			sbHtml.append("<td class=\"tabcent\">").append(statusStr).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("tracedata")==null?"":iMap.get("tracedata")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("address")==null?"":iMap.get("address")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("client")==null?"":iMap.get("client")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMap.get("time"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMap.get("opstatus")).equals("1")?"成功":"失败").append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"DisDownlodaTrace('").append(iMap.get("id")).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	
	private String delDownloadTraceInfo(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		String id = request.getParameter("id");
		if (Common.IsNullOrEmpty(id)) {
			return "0";
		}
		id=Common.Trim(id, ",");
		if (Logger.delDownloadTraceInfo(id)) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}
	private String delDownloadTraceAllInfo(HttpServletRequest request) throws ServletException, IOException {
		String strResult = "";
		if (Logger.delDownloadTraceAllInfo()) {
			strResult = "1";
		} else {
			strResult = "0";
		}
		return strResult;
	}
	
}
