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

import BLL.Logger;
import BLL.SearchSourceMngr;
import BLL.SourceMngr;

/**
 * Servlet implementation class RecommendTypeOrderHandlerServlet
 */
@WebServlet("/SearchSourceTypeOrderHandler.do")
public class SearchSourceTypeOrderHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchSourceTypeOrderHandlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		PrintWriter out = response.getWriter();
		String strResult = "";

		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSourceTypeList(request);
		} 
		out.write(strResult);
		out.flush();
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");

		PrintWriter out = response.getWriter();
		String strResult = "";

		strResult = saveRecomdSourceType(request);
		
		out.write(strResult);
		out.flush();
		out.close();
	}
	
	private String saveRecomdSourceType(HttpServletRequest request) throws ServletException, IOException {
		String strFlag = request.getParameter("hidcount");
		int iFlag = 0;
		try {
			iFlag = Integer.parseInt(strFlag);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		List<Map<String, String>> lstRecomType=new ArrayList<Map<String, String>>();
		Map<String, String> mapTypeInfo=null;
		for(int i=1;i<=iFlag;i++){
			mapTypeInfo=new HashMap<String,String>();
			mapTypeInfo.put("sourcedb", request.getParameter("hidid"+i));
			mapTypeInfo.put("showorder",String.valueOf(i));
			lstRecomType.add(mapTypeInfo);
		}
		if(SearchSourceMngr.saveSearchSourceTypeOrder(lstRecomType)){
			return "1";
		}else{
			return "0";
		}		
	}
	
	protected String getSourceTypeList(HttpServletRequest request) throws ServletException, IOException {

		List<Map<String, Object>> lstSourceType = SearchSourceMngr.getSearchSourceTypeList();
		if (lstSourceType == null) {
			return "";
		}
		Iterator<Map<String, Object>> iMap = lstSourceType.iterator();
		Map<String, Object> mapData = null;
		StringBuilder sbHtml = new StringBuilder();

		sbHtml.append("<table id=\"taborder\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"60\" >&nbsp;</th>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th width=\"100\">分类名称</th>");
		sbHtml.append("<th width=\"34%\">搜索属性</th>");
		sbHtml.append("<th>显示属性</th>");
		sbHtml.append("<th width=\"50\"> 操作</th>");
		sbHtml.append(" </tr>");
		
		int iNum = 0;
		while (iMap.hasNext()) {
			mapData = iMap.next();
			iNum=iNum+1;
			String  strTypeID = String.valueOf(mapData.get("id"));
			
			sbHtml.append("<tr>");
			sbHtml.append("<td> <a href=\"javascript:void(0)\" class=\"downbtn\" title=\"向上\" onclick=\"ordergoup(this);\"></a></td>");
			sbHtml.append("<td class=\"num\"><span name='tdnum'>").append(iNum).append("</span><input type=\"hidden\" id=\"hidid").append(iNum).append("\" name=\"hidid").append(iNum).append("\" value=\"").append(strTypeID).append("\" /></td>");
			sbHtml.append("<td class=\"tabcent\">").append(String.valueOf(mapData.get("name_ch"))).append("</td>");
			sbHtml.append("<td>").append(getSearchFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("<td>").append(getDisplayFieldHtml(strTypeID)).append("</td>");
			sbHtml.append("<td class=\"tabcent\"><a href=\"javascript:void(0)\" class=\"del\" title=\"删除\" onclick=\"delorderrow(this,'taborder')\"> </a>").append("</td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table><input type=\"hidden\" name=\"hidcount\" id=\"hidcount\" value=\"").append(iNum).append("\"/>");
		return sbHtml.toString();
	}

	private String getSearchFieldHtml(String TypeID) {
		List<Map<String, Object>> lstSearchField = SourceMngr.getSearchField(TypeID);
		if (lstSearchField == null) {
			return "&nbsp;";
		}
		StringBuilder sbSechFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstSearchField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbSechFieldHtml.append("<dfn>");
			sbSechFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbSechFieldHtml.append("</dfn>");
		}
		return sbSechFieldHtml.toString();
	}

	private String getDisplayFieldHtml(String TypeID) {
		List<Map<String, Object>> lstDisplayField = SourceMngr.getDisplayField(TypeID);
		if (lstDisplayField == null) {
			return "&nbsp;";
		}
		StringBuilder sbDisplayFieldHtml = new StringBuilder();
		Iterator<Map<String, Object>> iterator = lstDisplayField.iterator();
		Map<String, Object> imap = null;
		while (iterator.hasNext()) {
			imap = iterator.next();
			sbDisplayFieldHtml.append("<dfn>");
			sbDisplayFieldHtml.append(String.valueOf(imap.get("name_ch")));
			sbDisplayFieldHtml.append("</dfn>");
		}
		return sbDisplayFieldHtml.toString();
	}


}
