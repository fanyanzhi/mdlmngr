package SysMngr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.SourceMngr;
import Util.Common;

/**
 * Servlet implementation class EpubServerListServlet
 */
@WebServlet("/EpubTransServerList.do")
public class EpubTransServerListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EpubTransServerListServlet() {
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
		String strTypeID=request.getParameter("vtypeid");
		
		List<Map<String, Object>> list = SourceMngr.getSourceType();
		if(Common.IsNullOrEmpty(strTypeID)&&list!=null&&list.size()>0){
				Integer id = (Integer)list.get(0).get("id");
				strTypeID = String.valueOf(id);
		}
		request.setAttribute("HandlerURL", "EpubTransServerListHandler.do");
		String strTypeCH ="";
		//String strTypeEN="";
		List<Map<String, Object>> lstType = new ArrayList<Map<String,Object>>();
		if(strTypeID !=null){
			lstType = SourceMngr.getSourceType(strTypeID);
		}
		if (lstType.size()>0) {
			strTypeCH = String.valueOf(lstType.get(0).get("name_ch"));
			//strTypeEN = String.valueOf(lstType.get(0).get("name_en"));
		}
		request.setAttribute("TypeID", strTypeID);
		request.setAttribute("PageSize", 20);
		request.setAttribute("TypeNameCH", strTypeCH);
		request.getRequestDispatcher("/SysMngr/epubtransserverlist.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
