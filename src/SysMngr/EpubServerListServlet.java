package SysMngr;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.Pdf2Epub;
import Util.Common;

/**
 * Servlet implementation class EpubServerListServlet
 */
@WebServlet("/EpubServerList.do")
public class EpubServerListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EpubServerListServlet() {
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

		request.setAttribute("HandlerURL", "EpubServerListHandler.do");
		String strServerData = getEpubServerList();
		if(strServerData.length()==0){
			strServerData="<div class=\"nodata\">还没有数据。</div>";
		}
		request.setAttribute("ServerHtml", strServerData);
		request.getRequestDispatcher("/SysMngr/epubserverlist.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	
	private String getEpubServerList() {

		List<Map<String, Object>> lstEpubServer = Pdf2Epub.getServerList();
		if (lstEpubServer == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstEpubServer.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"200\">服务器地址</th>");
		sbHtml.append("<th>状态</th>");
		sbHtml.append("<th>命令端口</th>");
		sbHtml.append("<th>状态端口</th>");
		sbHtml.append("<th>线程池大小</th>");
		sbHtml.append("<th>活跃数</th>");
		sbHtml.append("<th>排队数</th>");
		sbHtml.append("<th>时间</th>");
		sbHtml.append("<th width=\"100\">操作</th>");
		sbHtml.append("</tr>");
		int iNum = 1;

		String strServerID = "";
		boolean isValid = false;
		String strHost = null;
		String strCmdPort = null;
		String strStatusPort = null;
		StringBuilder sbHostID=new StringBuilder();
		while (iMap.hasNext()) {
			mapData = iMap.next();
			strServerID = String.valueOf(mapData.get("id"));
			isValid = "1".equals(String.valueOf(mapData.get("status")));
			if(isValid){
				sbHostID.append("tr").append(strServerID).append(";");
			}
			strHost = String.valueOf(mapData.get("host"));
			strCmdPort = String.valueOf(mapData.get("cmdport"));
			strStatusPort = String.valueOf(mapData.get("statusport"));
			sbHtml.append("<tr id=\"tr").append(strServerID).append("\">");
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0);\" onclick=\"window.location.href='EpubServerInfo.do?sid=").append(strServerID).append("'\" title=\"编辑\">").append(strHost).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(isValid ? "启用" : "关闭").append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(strCmdPort).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(strStatusPort).append("</td>");
			sbHtml.append("<td class=\"tabcent\"></td>");
			sbHtml.append("<td class=\"tabcent\"></td>");
			sbHtml.append("<td class=\"tabcent\"></td>");
			sbHtml.append("<td class=\"tabcent\">").append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0);\" class=\"edit\" onclick=\"window.location.href='EpubServerInfo.do?sid=").append(strServerID).append("'\" title=\"编辑\"> </a><a href=\"javascript:void(0);\" onclick=\"delEpubServer(").append(strServerID).append(")\" class=\"del\" title=\"删除\"> </a></td>");
			sbHtml.append("</tr> ");
		}
		if(!Common.IsNullOrEmpty(sbHostID.toString())){
			sbHostID.delete(sbHostID.length()-1, sbHostID.length());
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidtrs\" id=\"hidtrs\" value=\"").append(sbHostID.toString()).append("\" />");
		return sbHtml.toString();
	}

}
