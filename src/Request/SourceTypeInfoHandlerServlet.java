package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

import BLL.Logger;
import BLL.SourceMngr;

/**
 * Servlet implementation class SourceTypeInfoHandlerServlet
 */
@WebServlet("/SourceTypeInfoHandler.do")
public class SourceTypeInfoHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SourceTypeInfoHandlerServlet() {
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

		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSourceTypeInfo(request);
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
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");

		PrintWriter out = response.getWriter();
		String strResult = "";

		strResult = saveSourceTypeInfo(request);
		
		out.write(strResult);
		out.flush();
		out.close();

	}

	protected String getSourceTypeInfo(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID = request.getParameter("tid");
		if (Common.IsNullOrEmpty(strTypeID)) {
			return "";
		}
		StringBuilder sbHtml = new StringBuilder();
		sbHtml.append(getSearchFieldTab(strTypeID)).append(getDisplayFieldHtml(strTypeID)).append(getOrderFieldHtml(strTypeID));
		return sbHtml.toString();
	}

	private String getSearchFieldTab(String TypeID) {
		StringBuilder sbSechFieldHtml = new StringBuilder();
		sbSechFieldHtml.append("<table id=\"tabseafield\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbSechFieldHtml.append("<tr>");
		sbSechFieldHtml.append("<th width=\"60\">&nbsp;</th>");
		sbSechFieldHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbSechFieldHtml.append("<th width=\"30%\">中文名称</th>");
		sbSechFieldHtml.append("<th width=\"30%\">英文名称</th>");
		sbSechFieldHtml.append("<th>操作</th>");
		sbSechFieldHtml.append("</tr>");
		List<Map<String, Object>> lstSearchField = SourceMngr.getSearchField(TypeID);
		if (lstSearchField == null) {
			sbSechFieldHtml.append("<tr class=\"tabtotopt\">");
			sbSechFieldHtml.append("<td></td>");
			sbSechFieldHtml.append("<td class=\"num\">+</td>");
			sbSechFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('tabseafield');\" class=\"addnewtd\">新增</a></td>");
			sbSechFieldHtml.append("</tr>");
			sbSechFieldHtml.append("</table>");
			sbSechFieldHtml.append("<input type=\"hidden\" id=\"seacount\" name=\"seacount\" value=\"0\"/>");
			return sbSechFieldHtml.toString();
		}

		Iterator<Map<String, Object>> iterator = lstSearchField.iterator();
		Map<String, Object> imap = null;

		int inum = 0;
		while (iterator.hasNext()) {
			inum = inum + 1;
			imap = iterator.next();
			sbSechFieldHtml.append("<tr>");
			sbSechFieldHtml.append("<td><a href=\"javascript:void(0)\" onclick=\"goup(this,'tabseafield');\" class=\"downbtn\" title=\"向上\"></a> <a href=\"javascript:void(0)\"  onclick=\"godown(this,'tabseafield');\" class=\"upbtn\" title=\"向下\"></a></td>");
			sbSechFieldHtml.append("<td class=\"num\"><span name='tdnum'>").append(inum).append("</span></td>");
			sbSechFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"snamech").append(inum).append("\" id=\"snamech").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_ch"))).append("\"/></td>");
			sbSechFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"snameen").append(inum).append("\" id=\"snameen").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_en"))).append("\"/></td>");
			sbSechFieldHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0)\"  onclick=\"deltabrow(this,'tabseafield');\" class=\"del\" title=\"删除\"> </a></td>");
			sbSechFieldHtml.append("</tr>");

		}
		sbSechFieldHtml.append("<tr class=\"tabtotopt\">");
		sbSechFieldHtml.append("<td></td>");
		sbSechFieldHtml.append("<td class=\"num\">+</td>");
		sbSechFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('tabseafield');\" class=\"addnewtd\">新增</a></td>");
		sbSechFieldHtml.append("</tr>");
		sbSechFieldHtml.append("</table>");
		sbSechFieldHtml.append("<input type=\"hidden\" id=\"seacount\" name=\"seacount\" value=\"").append(inum).append("\"/>");
		return sbSechFieldHtml.toString();
	}

	private String getDisplayFieldHtml(String TypeID) {
		StringBuilder sbDisplayFieldHtml = new StringBuilder();
		sbDisplayFieldHtml.append("<table id=\"tabdisplayfield\" style=\"display: none\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbDisplayFieldHtml.append("<tr>");
		sbDisplayFieldHtml.append("<th width=\"60\">&nbsp;</th>");
		sbDisplayFieldHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbDisplayFieldHtml.append("<th width=\"30%\">中文名称</th>");
		sbDisplayFieldHtml.append("<th width=\"30%\">英文名称</th>");
		sbDisplayFieldHtml.append("<th>操作</th>");
		sbDisplayFieldHtml.append("</tr>");
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(TypeID);
		if (lstDisplayField == null) {
			sbDisplayFieldHtml.append("<tr class=\"tabtotopt\">");
			sbDisplayFieldHtml.append("<td></td>");
			sbDisplayFieldHtml.append("<td class=\"num\">+</td>");
			sbDisplayFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('tabdisplayfield');\" class=\"addnewtd\">新增</a></td>");
			sbDisplayFieldHtml.append("</tr>");
			sbDisplayFieldHtml.append("</table>");
			sbDisplayFieldHtml.append("<input type=\"hidden\" id=\"displaycount\" name=\"displaycount\" value=\"0\"/>");
			return sbDisplayFieldHtml.toString();
		}

		Iterator<Map<String, Object>> iterator = lstDisplayField.iterator();
		Map<String, Object> imap = null;

		int inum = 0;
		while (iterator.hasNext()) {
			inum = inum + 1;
			imap = iterator.next();
			sbDisplayFieldHtml.append("<tr>");
			sbDisplayFieldHtml.append("<td><a href=\"javascript:void(0)\" class=\"downbtn\" onclick=\"goup(this,'tabdisplayfield');\" title=\"向上\"></a> <a href=\"javascript:void(0)\" class=\"upbtn\" onclick=\"godown(this,'tabdisplayfield');\" title=\"向下\"></a></td>");
			sbDisplayFieldHtml.append("<td class=\"num\"><span name='tdnum'>").append(inum).append("</span></td>");
			sbDisplayFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"dnamech").append(inum).append("\" id=\"dnamech").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_ch"))).append("\"/></td>");
			sbDisplayFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"dnameen").append(inum).append("\" id=\"dnameen").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_en"))).append("\"/></td>");
			sbDisplayFieldHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0)\"   onclick=\"deltabrow(this,'tabdisplayfield');\" class=\"del\" title=\"删除\"> </a></td>");
			sbDisplayFieldHtml.append("</tr>");

		}
		sbDisplayFieldHtml.append("<tr class=\"tabtotopt\">");
		sbDisplayFieldHtml.append("<td></td>");
		sbDisplayFieldHtml.append("<td class=\"num\">+</td>");
		sbDisplayFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('tabdisplayfield');\" class=\"addnewtd\">新增</a></td>");
		sbDisplayFieldHtml.append("</tr>");
		sbDisplayFieldHtml.append("</table>");
		sbDisplayFieldHtml.append("<input type=\"hidden\" id=\"displaycount\" name=\"displaycount\" value=\"").append(inum).append("\"/>");
		return sbDisplayFieldHtml.toString();
	}
	
	
	private String getOrderFieldHtml(String TypeID) {
		StringBuilder sbOrderFieldHtml = new StringBuilder();
		sbOrderFieldHtml.append("<table id=\"taborderfield\" style=\"display: none\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbOrderFieldHtml.append("<tr>");
		sbOrderFieldHtml.append("<th width=\"60\">&nbsp;</th>");
		sbOrderFieldHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbOrderFieldHtml.append("<th width=\"30%\">中文名称</th>");
		sbOrderFieldHtml.append("<th width=\"30%\">英文名称</th>");
		sbOrderFieldHtml.append("<th>操作</th>");
		sbOrderFieldHtml.append("</tr>");
		List<Map<String, Object>> lstOrderField = SourceMngr.getOrderField(TypeID);
		if (lstOrderField == null) {
			sbOrderFieldHtml.append("<tr class=\"tabtotopt\">");
			sbOrderFieldHtml.append("<td></td>");
			sbOrderFieldHtml.append("<td class=\"num\">+</td>");
			sbOrderFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('taborderfield');\" class=\"addnewtd\">新增</a></td>");
			sbOrderFieldHtml.append("</tr>");
			sbOrderFieldHtml.append("</table>");
			sbOrderFieldHtml.append("<input type=\"hidden\" id=\"ordercount\" name=\"ordercount\" value=\"0\"/>");
			return sbOrderFieldHtml.toString();
		}

		Iterator<Map<String, Object>> iterator = lstOrderField.iterator();
		Map<String, Object> imap = null;

		int inum = 0;
		while (iterator.hasNext()) {
			inum = inum + 1;
			imap = iterator.next();
			sbOrderFieldHtml.append("<tr>");
			sbOrderFieldHtml.append("<td><a href=\"javascript:void(0)\" class=\"downbtn\" onclick=\"goup(this,'taborderfield');\" title=\"向上\"></a> <a href=\"javascript:void(0)\" class=\"upbtn\" onclick=\"godown(this,'taborderfield');\" title=\"向下\"></a></td>");
			sbOrderFieldHtml.append("<td class=\"num\"><span name='tdnum'>").append(inum).append("</span></td>");
			sbOrderFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"onamech").append(inum).append("\" id=\"onamech").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_ch"))).append("\"/></td>");
			sbOrderFieldHtml.append("<td class=\"tabcent\"><input type=\"text\" class=\"addvalue\" name=\"onameen").append(inum).append("\" id=\"onameen").append(inum).append("\" value=\"").append(String.valueOf(imap.get("name_en"))).append("\"/></td>");
			sbOrderFieldHtml.append("<td class=\"tabopt tableft\"><a href=\"javascript:void(0)\"   onclick=\"deltabrow(this,'taborderfield');\" class=\"del\" title=\"删除\"> </a></td>");
			sbOrderFieldHtml.append("</tr>");

		}
		sbOrderFieldHtml.append("<tr class=\"tabtotopt\">");
		sbOrderFieldHtml.append("<td></td>");
		sbOrderFieldHtml.append("<td class=\"num\">+</td>");
		sbOrderFieldHtml.append("<td colspan=\"3\"><a href=\"javascript:void(0)\" onclick=\"addtabrow('taborderfield');\" class=\"addnewtd\">新增</a></td>");
		sbOrderFieldHtml.append("</tr>");
		sbOrderFieldHtml.append("</table>");
		sbOrderFieldHtml.append("<input type=\"hidden\" id=\"ordercount\" name=\"ordercount\" value=\"").append(inum).append("\"/>");
		return sbOrderFieldHtml.toString();
	}

	/*******************************************************/

	private String saveSourceTypeInfo(HttpServletRequest request) throws ServletException, IOException {
		String strTypeID=request.getParameter("hidtid");

		String strFieldNameCH=request.getParameter("txtFieldNameCH");
		String strFieldNameEN=request.getParameter("txtFieldNameEN");
		String strNewODataName=request.getParameter("txtNewName");
		
		Map<String,List<Map<String, String>>> mapSourceVal=new HashMap<String,List<Map<String, String>>>();
		mapSourceVal.put("seafield", getSeaField(request));
		mapSourceVal.put("displayfield", getDisplayField(request));
		mapSourceVal.put("orderfield", getOrderField(request));
		if (Common.IsNullOrEmpty(strTypeID)) {
			if(SourceMngr.saveSourceTypeInfo(strTypeID,strFieldNameCH,strFieldNameEN,strNewODataName,mapSourceVal) && SourceMngr.cretSourceTypeTab(strFieldNameEN)){
				return "1";
			}else{
				return "0";
			}
		}else{
			if(SourceMngr.saveSourceTypeInfo(strTypeID,strFieldNameCH,strFieldNameEN,strNewODataName,mapSourceVal)){
				return "1";
			}else{
				return "0";
			}
		}
		
		
	}

	private List<Map<String, String>> getSeaField(HttpServletRequest request) throws ServletException, IOException {
		String strFlag = request.getParameter("hidseacount");
		int iFlag = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<Map<String, String>> lstSeaField=new ArrayList<Map<String, String>>();
		Map<String, String> mapSeaField=null;
		for(int i=1;i<=iFlag;i++){
			mapSeaField=new HashMap<String,String>();
			mapSeaField.put("name_ch", request.getParameter("snamech"+i));
			mapSeaField.put("name_en", request.getParameter("snameen"+i));
			mapSeaField.put("showorder",String.valueOf(i));
			lstSeaField.add(mapSeaField);
		}
		return lstSeaField;
	}

	private List<Map<String, String>> getDisplayField(HttpServletRequest request) throws ServletException, IOException {
		String strFlag = request.getParameter("hiddisplaycount");
		int iFlag = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<Map<String, String>> lstDisField=new ArrayList<Map<String, String>>();
		Map<String, String> mapDisField=null;
		for(int i=1;i<=iFlag;i++){
			mapDisField=new HashMap<String,String>();
			mapDisField.put("name_ch", request.getParameter("dnamech"+i));
			mapDisField.put("name_en", request.getParameter("dnameen"+i));
			mapDisField.put("showorder", String.valueOf(i));
			lstDisField.add(mapDisField);
		}
		return lstDisField;
	}
	
	private List<Map<String, String>> getOrderField(HttpServletRequest request) throws ServletException, IOException {
		String strFlag = request.getParameter("hidordercount");
		int iFlag = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<Map<String, String>> lstOrderField=new ArrayList<Map<String, String>>();
		Map<String, String> mapDisField=null;
		for(int i=1;i<=iFlag;i++){
			mapDisField=new HashMap<String,String>();
			mapDisField.put("name_ch", request.getParameter("onamech"+i));
			mapDisField.put("name_en", request.getParameter("onameen"+i));
			mapDisField.put("showorder", String.valueOf(i));
			lstOrderField.add(mapDisField);
		}
		return lstOrderField;
	}

}
