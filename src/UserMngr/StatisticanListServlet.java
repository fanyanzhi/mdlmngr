package UserMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import Util.Common;

/**
 * Servlet implementation class StatisticanListServlet
 */
@WebServlet("/StatisticanList.do")
public class StatisticanListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatisticanListServlet() {
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
		StringBuilder sbParam=new StringBuilder();
		
		StringBuilder sbTipMsg=new StringBuilder();
		String strTmal=null;
		String strSingle=null;
		String strStartDate=null;
		String strEndDate=null;
		
		if(request.getParameter("uname")!=null){
			sbParam.append("uname=").append(request.getParameter("uname")).append("&");
		}
		if(request.getParameter("startdate")!=null){
			strStartDate=request.getParameter("startdate");
			sbParam.append("startdate=").append(request.getParameter("startdate")).append("&");
		}
		if(request.getParameter("enddate")!=null){
			strEndDate=request.getParameter("enddate");
			sbParam.append("enddate=").append(request.getParameter("enddate")).append("&");
		}
		if(request.getParameter("tmal")!=null){
			strTmal=request.getParameter("tmal");
			sbParam.append("tmal=").append(request.getParameter("tmal")).append("&");	
		}
		if(request.getParameter("singleTer")!=null){
			strSingle=request.getParameter("singleTer");
			sbParam.append("singleTer=").append(strSingle).append("&");	
		}
		if(strStartDate!=null&&strEndDate!=null){
			sbTipMsg.append("从").append(strStartDate).append("到").append(strEndDate).append(",");
		}
		sbTipMsg.append("<em id='emUcount'></em>个用户");
		if(strTmal!=null){
			int i=Common.Trim(strTmal,",").split(",").length;
			sbTipMsg.append("在").append(String.valueOf(i)).append("种设备上");
		}
		sbTipMsg.append("共登录<em id='emLcount'></em>次");
		//if(request.getParameter("uid")!=null){
			//sbParam.append("uid='").append(request.getParameter("uid")).append("'&");
		//}
		
		
		//String strOrderType = request.getParameter("ordtype");
		//String strOrder = request.getParameter("order");
		
		request.setAttribute("TipMsg", sbTipMsg.toString());
		request.setAttribute("StrParam", sbParam.toString());
		request.setAttribute("PageSize", "20");
		request.setAttribute("HandlerURL", "StatisticanListHandler.do");
		request.getRequestDispatcher("/UserMngr/statisticanlist.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
