package SysUserMngr;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import BLL.UserInfoMngr;
import Model.UserLoginBean;

/**
 * Servlet implementation class UpdatePwdServlet
 */
@WebServlet("/UpdatePwd.do")
public class UpdatePwdServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UpdatePwdServlet() {
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
		
		request.getRequestDispatcher("/SysUserMngr/updatepwd.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		
		String strOldPwd=request.getParameter("txtOldPwd");
		String strNewPwd=request.getParameter("txtNewPwd");
		
		HttpSession session=request.getSession();
		UserLoginBean userBean=(UserLoginBean)session.getAttribute("LoginObj");
		if(UserInfoMngr.equalsSysUserPwd(strOldPwd,userBean.getUserName()))
		{
			if(UserInfoMngr.updateSysUserPwd(userBean.getUserName(),strNewPwd)){
				request.setAttribute("errmsg", "保存成功");
			}else{
				request.setAttribute("errmsg", "保存失败");
			}
		}else{
			request.setAttribute("errmsg", "原始密码错误");
		}
		request.setAttribute("oldPwd", strOldPwd);
		request.setAttribute("newPwd", strNewPwd);
		request.getRequestDispatcher("/SysUserMngr/updatepwd.jsp").forward(request, response);
	}

}
