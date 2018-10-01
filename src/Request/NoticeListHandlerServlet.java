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

import Util.Common;

import BLL.NoticeMngr;

/**
 * Servlet implementation class NoticeListHandlerServlet
 */
@WebServlet("/NoticeListHandler.do")
public class NoticeListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public NoticeListHandlerServlet() {
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
		if("getcount".equals(request.getParameter("do"))){
			strResult = getNoticeCount(request);
		}if ("getlist".equals(request.getParameter("do"))) {
			strResult = getNoticeList(request);
		}else if("delnotice".equals(request.getParameter("do"))){
			strResult = delNotice(request);
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
	
	private String getNoticeCount(HttpServletRequest request) throws ServletException, IOException {
		return String.valueOf(NoticeMngr.getNoticeCount());
	}
	
	private String getNoticeList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		int iStart=Integer.parseInt(request.getParameter("start"));
		int iLength=Integer.parseInt(request.getParameter("len"));
		List<Map<String,Object>> lstNotice=NoticeMngr.getNoticeList(iStart, iLength);
		if(lstNotice==null){
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tabnotice\" name=\"tabnotice\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"20\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"20%\">通知标题</th>");
		sbHtml.append("<th>通知内容</th>");
		sbHtml.append("<th width=\"15%\">发布日期</th>");
		sbHtml.append("<th width=\"10%\">公共通知</th>");
		sbHtml.append("<th width=\"10%\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String,Object>> iNotice=lstNotice.iterator();
		Map<String,Object> iMap=null;
		int iNum = 1;
		while(iNotice.hasNext()){
			iMap=iNotice.next();
			String strContent=String.valueOf(iMap.get("content"));
			sbHtml.append("<tr>");
			sbHtml.append("<td><input name=\"chknoteid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td><a href=\"PublishNotice.do?nid=").append(iMap.get("id")).append("\" title=\"点击查看详情\">").append(iMap.get("title")).append("</a></td>");
			sbHtml.append("<td><span title=\"").append(strContent).append("\">").append(strContent.length()>45?strContent.substring(0, 45).concat("..."):strContent).append("</span></td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append("1".equals(String.valueOf(iMap.get("ispublic")))?"是":"<span title=\"在详情查看用户\">否</span>").append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delNotice(").append(iMap.get("id")).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString(); 		
	}

	public String delNotice(HttpServletRequest request) throws ServletException, IOException {
		String strID=request.getParameter("nid");
		if(strID==null){
			return "0";
		}
		strID=Common.Trim(strID, ",");
		if(NoticeMngr.delNotice(strID)){
			NoticeMngr.delNoticeRelationship(strID);
			return "1";	
		}else{
			return "0";
		}
		
	}
}
