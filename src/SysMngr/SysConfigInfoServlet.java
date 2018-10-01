package SysMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import BLL.SysConfigMngr;

import Util.Common;

/**
 * Servlet implementation class SysConfigInfoServlet
 */
@WebServlet("/SysConfigInfo.do")
public class SysConfigInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SysConfigInfoServlet() {
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
		
		String strSysID=request.getParameter("sid");
		Map<String,Object> mapSysInfo=null;
		if(!Common.IsNullOrEmpty(strSysID)){
			mapSysInfo=SysConfigMngr.getConfigInfo(strSysID);
			if(mapSysInfo!=null){
				request.setAttribute("HidID", strSysID);
				request.setAttribute("TxtName", mapSysInfo.get("name"));
				request.setAttribute("TxtVal", mapSysInfo.get("value"));
			}
		}
		request.getRequestDispatcher("/SysMngr/sysconfiginfo.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		String strSysID=request.getParameter("hidsid");
		String strTextName=request.getParameter("txtProName");
		String strTextValue=request.getParameter("txtProVal");
		
		if(!Common.IsNullOrEmpty(strSysID)){
			if(SysConfigMngr.updateConfigInfo(strSysID, strTextName, strTextValue)){
				response.sendRedirect("SysConfigList.do");
				return;
			}
		}else{
			if(SysConfigMngr.addConfigInfo(strTextName, strTextValue)){
				response.sendRedirect("SysConfigList.do");
				return;
			}
		}
		request.setAttribute("errmsg", "保存失败");
		request.setAttribute("HidID", strSysID);
		request.setAttribute("TxtName", strTextName);
		request.setAttribute("TxtVal", strTextValue);
		request.getRequestDispatcher("/SysMngr/sysconfiginfo.jsp").forward(request, response);
	}

}
