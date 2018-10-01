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

import BLL.SubjectRecommendMngr;
import Util.Common;

/**
 * Servlet implementation class SubjectListHandlerServlet
 */
@WebServlet("/SubjectListHandler.do")
public class SubjectListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       

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
			strResult = getSubjectCount(request);
		}else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSubjectList(request);
		}else if ("del".equals(request.getParameter("do"))) {
			strResult = delSubjectInfo(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}
	private static String delSubjectInfo(HttpServletRequest request){
		String id = request.getParameter("id");
		String simageid= request.getParameter("simageid");
		String bimageid = request.getParameter("bimageid");
		return String.valueOf(SubjectRecommendMngr.delSubjectInfo(id,simageid,bimageid));
	}
	private static String getSubjectCount(HttpServletRequest request){
		return String.valueOf(SubjectRecommendMngr.getSubjectCount());
	}
	private String getSubjectList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		int iStart=Integer.parseInt(request.getParameter("start"));
		int iLength=Integer.parseInt(request.getParameter("len"));
		List<Map<String, Object>> lstFiles = null;
		lstFiles = SubjectRecommendMngr.getSubjectList(iStart, iLength);
		if (lstFiles == null) {
			return "";
		}
		sbHtml.append("<table width=\"100%\" id=\"tabnotice\" name=\"tabnotice\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		//sbHtml.append("<th width=\"40\">&nbsp;</th>");
		sbHtml.append("<th width=\"5%\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th  width=\"15%\">主题名称</th>");
		sbHtml.append("<th width=\"15%\">主题词</th>");
		sbHtml.append("<th width=\"60\">主题类别</th>");
		sbHtml.append("<th width=\"40\">是否置顶</th>");
		sbHtml.append("<th width=\"15%\">主题摘要</th>");
		sbHtml.append("<th width=\"80px\">小图</th>");
		sbHtml.append("<th width=\"80px\">大图</th>");
		sbHtml.append("<th width=\"12%\">时间</th>");
		sbHtml.append("<th width=\"50px\">操作</th>");
		sbHtml.append("</tr>");
		Iterator<Map<String, Object>> iFile = lstFiles.iterator();
		Map<String, Object> iMap = null;
		int iNum = iStart;
		while (iFile.hasNext()) { 
			iMap = iFile.next();
			sbHtml.append("<tr>");
			//sbHtml.append("<td><input name=\"chknoteid\" value=\"").append(iMap.get("id")).append("\" type=\"checkbox\" /></td>");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td><a href=\"SubjectRecommend.do?id=").append(iMap.get("id")).append("\">").append(iMap.get("title")).append("</a></td>");
			sbHtml.append("<td>").append(iMap.get("keyword")==null?"":iMap.get("keyword")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("type")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(iMap.get("istop")).equals("1")?"是":"否").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(iMap.get("summary")==null?"":iMap.get("summary")).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append((int)iMap.get("simageid")==0?"<span>暂无图片 </span>":"<img style=\"height:50px;width:50px\" src=\"ImgSrcHandler?"+iMap.get("simageid")+"\">").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append((int)iMap.get("bimageid")==0?"<span>暂无图片 </span>":"<img style=\"height:50px;width:50px\" src=\"ImgSrcHandler?"+iMap.get("bimageid")+"\">").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(iMap.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"javascript:void(0);\" onclick=\"delSubjectInfo('").append(iMap.get("id")).append("','").append(iMap.get("simageid")).append("','").append(iMap.get("bimageid")).append("')\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr>");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
