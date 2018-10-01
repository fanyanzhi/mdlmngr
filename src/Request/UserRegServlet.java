package Request;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
//import BLL.CnkiMngr;
//import BLL.SysConfigMngr;
//import BLL.UserInfoMngr;
//import Util.Common;

/**
 * Servlet implementation class UserRegServlet
 */
@WebServlet("/UserReg")
public class UserRegServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserRegServlet() {
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
		response.setContentType("text/html;charset=utf-8");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

//	private String UserReg(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		String ip = Common.getClientIP(request);
//		String strDo = request.getParameter("do");
//		if (Common.IsNullOrEmpty(strDo)) {
//			return "";
//		}
//		HttpSession session = request.getSession();
//		String accessToken = (String) session.getAttribute("demo_access_token");
//		String openID = (String) session.getAttribute("demo_openid");
//		if ("reg".equals(strDo)) {
//			String strEmail = request.getParameter("email");
//			String strPwd = request.getParameter("password");
//			String checkUserRet = net.cnki.mngr.TUserMngr.IsExistUserName(strEmail);
//			if (checkUserRet.equals("有")) {
//				return "用户名已经存在";
//			}
//			String regUserRet = net.cnki.mngr.TUserMngr.CreatPersonLib(strEmail, strPwd, strEmail, "云阅读系统");
//			if (!Common.IsNullOrEmpty(regUserRet)) {
//				return "{\"result\":false,\"message\":\"".concat(regUserRet).concat("\",\"errorcode\":").concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_REGISTER.code)).concat("}");
//			}
//		} else if ("bind".equals(strDo)) {
//			String userName = request.getParameter("username");
//			String passWord = request.getParameter("password");
//			boolean bResult = false;
//			CnkiMngr cnkiMngr = new CnkiMngr();
//			int[] iResult = new int[2];
//			for (int i = 0; i < 3; i++) {
//				bResult = cnkiMngr.cnkiUserLogin(userName, passWord, ip, iResult);
//				if (bResult)
//					break;
//			}
//			if (!bResult) {
//				return "@-".concat(String.valueOf(Math.abs(iResult[0])));
//			}
//		} 
//		String userName = UserInfoMngr.checkQQOpenID(openID);
//		
////		String[] arrUserToken = new String[] { "" };
////		if (!checkOnlineCount(userName, strPlatForm, UserInfo.getClientID(), arrUserToken)) {
////			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_CLIENTOVERFULL.code));
////		}
////		String strUserToken = "";
////		boolean bOnline = !Common.IsNullOrEmpty(arrUserToken[0]);
////		if (bOnline) {
////			strUserToken = arrUserToken[0];
////		} else {
////			strUserToken = CreateUserToken(userName);
////		}
//		
//		
//		//else {
//			return "illegal Operation";
//		//}
//
//		//return "";
//	}

}
