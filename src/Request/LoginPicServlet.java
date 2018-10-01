package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import BLL.ModuleMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class LoginPicServlet
 */
@WebServlet("/LoginPic.do")
public class LoginPicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginPicServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getpic".equals(request.getParameter("do"))) {
			strResult = getLoginPic(request);
		}
		strResult=strResult==null?"":strResult;
		out.write(strResult);
		out.flush();
		out.close();
		//response.getOutputStream().write(getLoginPic().getBytes());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private String getLoginPic(HttpServletRequest request) {
		String strUserID = request.getParameter("uname");
		if (strUserID != null) {
			if (strUserID.endsWith(",")) {
				strUserID = Common.Trim(strUserID, ",");
			}
			strUserID = "'".concat(strUserID).concat("'").replace(",", "','");
		}
		
		String strTerminal = request.getParameter("tmal");

		String strSingleTer = request.getParameter("singleTer");
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		String strType= request.getParameter("vtype");
		
		List<Map<String, Object>> lstInfo = UserInfoMngr.getLoginCount(strUserID, strTerminal, strStartDate, strEndDate, strType,strSingleTer);
		if (lstInfo == null) {
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		if (lstInfo != null) {
			for (Map<String, Object> map : lstInfo) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("count", map.get("count"));
				jsonObj.put("date", map.get("date"));
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.toString();
	}
}
