package Request;

import java.io.IOException;
import java.io.PrintWriter;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import BLL.BehaviourMngr;

/**
 * Servlet implementation class OnlinePicServlet
 */
@WebServlet("/UserAlivePic.do")
public class UserAlivePic extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserAlivePic() {
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
			strResult = getOnlinePic(request);
		}
		strResult = strResult == null ? "" : strResult;
		out.write(strResult);
		out.flush();
		out.close();
		// response.getOutputStream().write(getOnlinePic().getBytes());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private String getOnlinePic(HttpServletRequest request) {		
		List<Map<String, Object>> lstInfo = BehaviourMngr.getAliveUserInTime();
		if (lstInfo == null) {
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		if (lstInfo != null) {
			for (Map<String, Object> map : lstInfo) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("count", map.get("count"));
				jsonObj.put("date", String.valueOf(map.get("date")));
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.toString();
	}
}
