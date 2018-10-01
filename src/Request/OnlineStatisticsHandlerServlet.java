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
import BLL.UserInfoMngr;

/**
 * Servlet implementation class OnlinePicServlet
 */
@WebServlet("/OnlineStatisticsHandler.do")
public class OnlineStatisticsHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OnlineStatisticsHandlerServlet() {
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
		if ("getStatistics".equals(request.getParameter("do"))) {
			strResult = getStatistics(request);
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
		// TODO Auto-generated method stub
	}

	private String getStatistics(HttpServletRequest request) {
		String strStartDate = request.getParameter("startdate");
		List<Map<String, Object>> lstInfo = UserInfoMngr.getStatistics(strStartDate);
		if (lstInfo == null) {
			return null;
		}
		
		JSONArray jsonArray = new JSONArray();
		if (lstInfo != null) {
			for (Map<String, Object> map : lstInfo) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("count", map.get("count"));
				jsonObj.put("spottime", String.valueOf(map.get("spottime")));
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.toString();
		
	}
}
