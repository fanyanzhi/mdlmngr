package ModuleMngr;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;
import Model.ModuleContentInfoBean;
import Util.Common;

/**
 * Servlet implementation class UserModuleContentDetailServlet
 */
@WebServlet("/UserModuleContentDetail.do")
public class UserModuleContentDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserModuleContentDetailServlet() {
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

		String strModuleID = request.getParameter("mid");
		String strRecordID = request.getParameter("rid");
		String tablename = request.getParameter("tablename");
		StringBuilder sbHtml = new StringBuilder();
		StringBuilder sbField = new StringBuilder();

		List<ModuleContentInfoBean> lstField = ModuleMngr.getDisplayModuleContentByTabID(strModuleID);;
		if (lstField == null) {
			request.setAttribute("detail", "<div class=\"nodata\">还没有数据。</div>");
			request.getRequestDispatcher("/ModuleMngr/usermodulecontentdetail.jsp").forward(request, response);
			return;
		}
		Map<String, String> mapFields = new LinkedHashMap<String, String>();
		sbField.append("id,");
		for (ModuleContentInfoBean moduleContentInfo : lstField) {
			sbField.append(moduleContentInfo.getFieldName()).append(",");
			if ("time".equals(moduleContentInfo.getFieldName())) {
				continue;
			}
			mapFields.put(moduleContentInfo.getFieldName(), moduleContentInfo.getFieldName_CH());
		}
		mapFields.put("time", "更新时间");
		if (sbField.length() > 0) {
			sbField.delete(sbField.length() - 1, sbField.length());
		}
		String strField = sbField.toString();
		String[] arrFields = strField.split(",");
		List<Map<String, Object>> lstUserModuleContent = ModuleMngr.getUserModuleContentDetail(strField, tablename, strRecordID);
		if (lstUserModuleContent == null) {
			request.setAttribute("detail", "<div class=\"nodata\">还没有数据。</div>");
			request.getRequestDispatcher("/ModuleMngr/usermodulecontentdetail.jsp").forward(request, response);
			return;
		}

		Map<String, Object> mapUserModule = lstUserModuleContent.get(0);
		for (int i = 0; i < arrFields.length; i++) {
			String strTempField = arrFields[i];
			if ("id".equals(strTempField) || "time".equals(strTempField)) {
				continue;
			}
			if("classxml".equals(strTempField)||"item".equals(strTempField)||"docinfo".equals(strTempField)||"docinfo".equals(strTempField)){
				sbHtml.append("<li><span>").append(mapFields.get(strTempField)).append("：</span>").append(Common.transform(Common.base64Decode(String.valueOf(mapUserModule.get(strTempField))))).append("</li>");
			}else{
				sbHtml.append("<li><span>").append(mapFields.get(strTempField)).append("：</span>").append(String.valueOf(mapUserModule.get(strTempField))).append("</li>");
			}
		}
		sbHtml.append("<li><span>更新时间：</span>").append(String.valueOf(mapUserModule.get("time"))).append("</li>");
		request.setAttribute("detail", sbHtml.toString());
		request.getRequestDispatcher("/ModuleMngr/usermodulecontentdetail.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}


}
