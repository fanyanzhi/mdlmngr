package UIL;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.AppInfoMngr;

/**
 * Servlet implementation class UserModuleContentDetailServlet
 */
@WebServlet("/AppCrashContentDetail.do")
public class AppCrashContentDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppCrashContentDetailServlet() {
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
		response.setContentType("text/html;utf-8");

		String strRecordID = request.getParameter("rid");
		StringBuilder sbHtml = new StringBuilder();
		List<Map<String,Object>>  lstField = AppInfoMngr.getAppCrashInfoByID(strRecordID);;
		if (lstField == null) {
			request.setAttribute("detail", "<div class=\"nodata\">还没有数据。</div>");
			request.getRequestDispatcher("crashdetail.jsp").forward(request, response);
			return;
		}
		Map<String,Object> crashMap = null;
		if(lstField.size()>0){
			crashMap = lstField.get(0);
			sbHtml.append("<li><span>APP版本：</span>").append(String.valueOf(crashMap.get("appinfo") == null ? "无" : String.valueOf(crashMap.get("appinfo") == "" ? "无":String.valueOf(crashMap.get("appinfo"))))).append("</li>");
			sbHtml.append("<li><span>设备型号：</span>").append(String.valueOf(crashMap.get("platform") == null ? "无" :crashMap.get("platform") == "" ? "无" : String.valueOf(crashMap.get("platform")))).append("</li>");
			sbHtml.append("<li><span>崩溃信息：</span>").append(String.valueOf(crashMap.get("errorinfo") == null ? "无" :crashMap.get("errorinfo") == "" ? "无" : String.valueOf(crashMap.get("errorinfo"))).replace("\r\n","<br />").replace("\n", "<br />")).append("</li>");
			sbHtml.append("<li><span>崩溃时间：</span>").append(String.valueOf(crashMap.get("time") == null ? "无" : crashMap.get("time") == "" ? "无" : String.valueOf(crashMap.get("time")))).append("</li>");
			
		}else{
			sbHtml.append("<div class=\"nodata\">还没有数据。</div>");
		}
		request.setAttribute("detail", sbHtml.toString());
		request.getRequestDispatcher("crashdetail.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


}
