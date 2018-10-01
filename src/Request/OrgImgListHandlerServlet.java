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

import BLL.OrgImageMngr;
import Model.UserLoginBean;
import Util.Common;
@WebServlet("/OrgImgListHandler.do")
public class OrgImgListHandlerServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getOrgImageList(request);
		} else if ("del".equals(request.getParameter("do"))) {
			strResult = delOrgImage(request);
		}

		out.write(strResult);
		out.flush();
		out.close();
	}
	private String getOrgImageList(HttpServletRequest request) {
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}
		List<Map<String, Object>> lstOrgActiveInfo = OrgImageMngr.getOrgActiveList(appid);
		//id,appid,title,content,active,time
		if (lstOrgActiveInfo == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstOrgActiveInfo.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabsysinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"150\">APPID</th>");
		sbHtml.append("<th width=\"200\">标题</th>");
		sbHtml.append("<th >详情</th>");
		sbHtml.append("<th width=\"300\">图片</th>");
		sbHtml.append("<th width=\"200\">修改时间</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		while (iMap.hasNext()) {
			mapData = iMap.next();
			String strLogoID = String.valueOf(mapData.get("id"));
			sbHtml.append("<tr>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("appid"))).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("title")==null?"":String.valueOf(mapData.get("title"))).append("</td>");
			Integer type = (Integer) mapData.get("type");
			if(type==0){
				sbHtml.append("<td class=\"tabcent\"><a href='").append(mapData.get("content")==null?"#":String.valueOf(mapData.get("content"))).append("'>").append(mapData.get("content")).append("</a></td>");
			}else{
				sbHtml.append("<td class=\"tabcent\">").append(mapData.get("content")==null?"":String.valueOf(mapData.get("content"))).append("</td>");
			}
			byte[] logo = (byte[])mapData.get("active");
			String str="";
			if(logo!=null){
				str= "<img style='width:100px;height:50px;' src='OrgImgSrcHandler?id="+mapData.get("id")+"'></img>";
			}
			sbHtml.append("<td class=\"tabcent\">").append(str).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='OrgUploadImage.do?id=").append(mapData.get("id")).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delLogoInfo(").append(strLogoID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	public String delOrgImage(HttpServletRequest request){
		String id = (String)request.getParameter("id");
		boolean result = OrgImageMngr.delOrgLogoInfo(id);
		String str = "";
		if(result){
			str = "删除成功！";
		}else{
			str = "删除失败！";
		}
		return str;
	}
}
