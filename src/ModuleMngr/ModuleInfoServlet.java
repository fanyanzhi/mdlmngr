package ModuleMngr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;
import Model.ModuleInfoBean;
import Util.Common;

/**
 * Servlet implementation class ModuleInfoServlet
 */
@WebServlet("/ModuleInfo.do")
public class ModuleInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModuleInfoServlet() {
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

		String strModuleID = request.getParameter("md");
		if (strModuleID != null) {
			request.setAttribute("ModuleID", strModuleID);
			ModuleInfoBean moduleInfo = ModuleMngr.getModuleInfo(strModuleID);
			if (moduleInfo != null) {
				request.setAttribute("ModuleInfo", moduleInfo);
			}
		}
		request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		// 判断名称是否有重复的。
		ModuleInfoBean moduleInfo = setModuldInfoBean(request, response);
		if (moduleInfo.getId() != 0) {
			request.setAttribute("ModuleID", moduleInfo.getId());
		}
		if (ModuleMngr.existsTableName(moduleInfo.getTableName(), String.valueOf(moduleInfo.getId()))) {
			if (moduleInfo.getId() != 0) {
				request.setAttribute("ModuleID", moduleInfo.getId());
			}
			request.setAttribute("ModuleInfo", moduleInfo);
			request.setAttribute("errmsg", "该模块名称已经存在");
			request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);
			return;
		}
		if (ModuleMngr.existsTableNameEN(moduleInfo.getTableName_EN(), String.valueOf(moduleInfo.getId()))) {
			if (moduleInfo.getId() != 0) {
				request.setAttribute("ModuleID", moduleInfo.getId());
			}
			request.setAttribute("errmsg", "该模块英文名称已经存在");
			request.setAttribute("ModuleInfo", moduleInfo);
			request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);
			return;
		}

		if (ModuleMngr.existsTableNameCH(moduleInfo.getTableName_CH(), String.valueOf(moduleInfo.getId()))) {
			if (moduleInfo.getId() != 0) {
				request.setAttribute("ModuleID", moduleInfo.getId());
			}
			request.setAttribute("errmsg", "该模块 中文名称已经存在");
			request.setAttribute("ModuleInfo", moduleInfo);
			request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);
			return;
		}

		if (moduleInfo.getId() == 0) {
			if (ModuleMngr.addModuleInfo(moduleInfo)) {
				response.sendRedirect("ModuleList.do");
			} else {
				request.setAttribute("ModuleInfo", moduleInfo);
				request.setAttribute("errmsg", "保存失败");
				request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);
			}
		} else {
			if (ModuleMngr.updateModuleInfo(moduleInfo)) {
				response.sendRedirect("ModuleList.do");
			} else {
				request.setAttribute("ModuleInfo", moduleInfo);
				request.setAttribute("errmsg", "保存失败");
				request.getRequestDispatcher("/ModuleMngr/moduleinfo.jsp").forward(request, response);
			}
		}
	}

	private ModuleInfoBean setModuldInfoBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html utf-8");
		ModuleInfoBean moduleInfo = new ModuleInfoBean();
		String strID = request.getParameter("hidid");
		if (!Common.IsNullOrEmpty(strID)) {
			moduleInfo.setId(Integer.parseInt(strID));
		}
		moduleInfo.setTableName(request.getParameter("txtModuleName"));
		moduleInfo.setTableName_EN(request.getParameter("txtModuleNameEN"));
		moduleInfo.setTableName_CH(request.getParameter("txtModuleNameCH"));
		moduleInfo.setStatus(Integer.parseInt(request.getParameter("isValid")));
		moduleInfo.setIsDisplay(Boolean.parseBoolean(request.getParameter("isDisplay")));
		moduleInfo.setDescription(request.getParameter("txtDescription"));
		return moduleInfo;
	}

	
}
