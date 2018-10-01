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

import BLL.LiteratureMngr;
import BLL.UserInfoMngr;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class BrowseSearchPicServlet
 */
@WebServlet("/BrowseSearchPic.do")
public class BrowseSearchPicServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BrowseSearchPicServlet() {
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
		if ("getpic".equals(request.getParameter("do"))) {
			strResult = getBrowseSearchPic(request);
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
		doGet(request, response);
	}

	private String getBrowseSearchPic(HttpServletRequest request) {
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		List<Map<String, Object>> lstInfo = LiteratureMngr.getDaySearchCount(strStartDate, strEndDate);
		if (lstInfo == null) {
			return null;
		}
		Map<String, Object> mapInfo = LiteratureMngr.getBrowseStatis(strStartDate, strEndDate);
		JSONArray jsonArray = new JSONArray();
		if (lstInfo != null) {
			for (Map<String, Object> map : lstInfo) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("seacount", map.get("count"));
				jsonObj.put("brocount", mapInfo.get(String.valueOf(map.get("spottime"))));
				jsonObj.put("date", String.valueOf(map.get("spottime")));
				jsonArray.add(jsonObj);
			}
		}
		return jsonArray.toString();
	}
}
