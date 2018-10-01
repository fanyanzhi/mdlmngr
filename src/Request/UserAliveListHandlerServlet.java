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
import BLL.BehaviourMngr;

/**
 * Servlet implementation class UserAliveListHandlerServlet
 */
@WebServlet("/UserAliveListHandler.do")
public class UserAliveListHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAliveListHandlerServlet() {
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
		if ("getlist".equals(request.getParameter("do"))) {
			strResult = getUserAliveList(request);
		}
		strResult = strResult == null ? "" : strResult;
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
	
	private String getUserAliveList(HttpServletRequest request){
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		
		List<Map<String, Object>> lstInfo = BehaviourMngr.getUserAliveList(strStartDate,strEndDate);
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
