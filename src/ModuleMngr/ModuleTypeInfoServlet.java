package ModuleMngr;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ModuleMngr;
import Model.ModuleTypeInfoBean;
import Util.Common;

/**
 * Servlet implementation class ModuleTypeInfoServlet
 */
@WebServlet("/ModuleTypeInfo.do")
public class ModuleTypeInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public ModuleTypeInfoServlet() {
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

		String strModuleID = request.getParameter("tabid");
		String strModuleName = request.getParameter("tabname");
		String strModuleTypeID = request.getParameter("id");
		if (strModuleID == null) {
			response.sendRedirect("ModuleList.do");
			return;
		}
		request.setAttribute("ModuleID", strModuleID);
		request.setAttribute("ModuleName", strModuleName);
		if (strModuleTypeID != null) {
			request.setAttribute("ModuleTypeID", strModuleTypeID);
			ModuleTypeInfoBean moduleTypeInfo = ModuleMngr.getModuleTypeInfo(strModuleTypeID);
			if (moduleTypeInfo != null) {
				request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
			}
		}

		request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);

		// TreeMap<String, Object> mapModuleInfo = new TreeMap<String,
		// Object>();
		// List<Map<String, Object>> lstModuleInfo = null;
		// lstModuleInfo = ModuleMngr.getModuleList();
		// if (lstModuleInfo == null) {
		// mapModuleInfo.put("暂无模块", "");
		// } else {
		// mapModuleInfo.put("-请选择模块-", "");
		// Iterator<Map<String, Object>> iMap = lstModuleInfo.iterator();
		// Map<String, Object> mapData = null;
		// while (iMap.hasNext()) {
		// mapData = iMap.next();
		// mapModuleInfo.put(mapData.get("tablename_ch").toString(),
		// mapData.get("id").toString());
		// }
		// }
		// request.setAttribute("DropListModule", mapModuleInfo);
		//
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html utf-8");

		ModuleTypeInfoBean moduleTypeInfo = setModuldTypeInfoBean(request, response);
		String strModuleName=request.getParameter("hidtabname");
		request.setAttribute("ModuleName", strModuleName);
		
		if (ModuleMngr.existsModuleTypeName(moduleTypeInfo.getTypeName(), String.valueOf(moduleTypeInfo.getTableId()), String.valueOf(moduleTypeInfo.getId()))) {
			if (moduleTypeInfo.getId() != 0) {
				request.setAttribute("ModuleTypeID", moduleTypeInfo.getId());
			}
			request.setAttribute("errmsg", "该模块名称已经存在");
			request.setAttribute("ModuleID", moduleTypeInfo.getTableId());
			request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
			request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);
			return;
		}
		if (ModuleMngr.existsModuleTypeNameEN(moduleTypeInfo.getTypeName_EN(), String.valueOf(moduleTypeInfo.getTableId()), String.valueOf(moduleTypeInfo.getId()))) {
			if (moduleTypeInfo.getId() != 0) {
				request.setAttribute("ModuleTypeID", moduleTypeInfo.getId());
			}
			request.setAttribute("errmsg", "该模块英文名称已经存在");
			request.setAttribute("ModuleID", moduleTypeInfo.getTableId());
			request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
			request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);
			return;
		}

		if (ModuleMngr.existsModuleTypeNameCH(moduleTypeInfo.getTypeName_CH(), String.valueOf(moduleTypeInfo.getTableId()), String.valueOf(moduleTypeInfo.getId()))) {
			if (moduleTypeInfo.getId() != 0) {
				request.setAttribute("ModuleTypeID", moduleTypeInfo.getId());
			}
			request.setAttribute("errmsg", "该模块中文名称已经存在");
			request.setAttribute("ModuleID", moduleTypeInfo.getTableId());
			request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
			request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);
			return;
		}

		if (moduleTypeInfo.getId() == 0) {
			if (ModuleMngr.addModuleTypeInfo(moduleTypeInfo)) {
				String strTableName=ModuleMngr.getTableName(moduleTypeInfo.getTableId());
				if(ModuleMngr.existsTable(strTableName)){
					ModuleMngr.createModuleTypeTable(strTableName, strTableName.concat("_").concat(moduleTypeInfo.getTypeName()));
				}
				response.sendRedirect("ModuleList.do");
			} else {
				if (moduleTypeInfo.getId() != 0) {
					request.setAttribute("ModuleTypeID", moduleTypeInfo.getId());
				}
				request.setAttribute("errmsg", "添加失败");
				request.setAttribute("ModuleID", moduleTypeInfo.getTableId());
				request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
				request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);
			}
		} else {
			if (ModuleMngr.updateModuleTypeInfo(moduleTypeInfo)) {
				response.sendRedirect("ModuleList.do");
			} else {
				if (moduleTypeInfo.getId() != 0) {
					request.setAttribute("ModuleTypeID", moduleTypeInfo.getId());
				}
				request.setAttribute("errmsg", "更新失败");
				request.setAttribute("ModuleID", moduleTypeInfo.getTableId());
				request.setAttribute("ModuleTypeInfo", moduleTypeInfo);
				request.getRequestDispatcher("/ModuleMngr/moduletypeinfo.jsp").forward(request, response);
			}
		}
	}

	private ModuleTypeInfoBean setModuldTypeInfoBean(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html utf-8");
		ModuleTypeInfoBean moduleTypeInfo = new ModuleTypeInfoBean();
		String strID = request.getParameter("hidid");
		if (!Common.IsNullOrEmpty(strID)) {
			moduleTypeInfo.setId(Integer.parseInt(strID));
		}
		moduleTypeInfo.setTypeName(request.getParameter("txtTypeName"));
		moduleTypeInfo.setTypeName_EN(request.getParameter("txtTypeNameEN"));
		moduleTypeInfo.setTypeName_CH(request.getParameter("txtTypeNameCH"));
		moduleTypeInfo.setStatus(Integer.parseInt(request.getParameter("isValid")));
		moduleTypeInfo.setTableId(Integer.parseInt(request.getParameter("hidtabid")));
		moduleTypeInfo.setDescription(request.getParameter("txtDescription"));
		// moduleTypeInfo.setTableId(Integer.parseInt(request.getParameter("ddlModuleInfo_SelectValue")));
		return moduleTypeInfo;
	}

}
