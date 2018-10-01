package DownloadMngr;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.ODataMngr;
import BLL.UploadMngr;
import Model.UploadInfoBean;
import Util.Common;

/**
 * Servlet implementation class AddUserDataServlet
 */
@WebServlet("/AddUserData.do")
public class AddUserDataServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddUserDataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		request.getRequestDispatcher("/DownloadMngr/adduserdata.jsp").forward(request, response);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		String strTypeID = request.getParameter("txtTypeID");
		String strFileID = request.getParameter("txtFileID");
		String strUsers = request.getParameter("txtUsers");
		if(Common.IsNullOrEmpty(strTypeID)||Common.IsNullOrEmpty(strFileID)||Common.IsNullOrEmpty(strUsers)){
			request.setAttribute("errmsg", "请将信息补充完整");
			request.getRequestDispatcher("/DownloadMngr/adduserdata.jsp").forward(request, response);
			return;
		}
		String strCajTabName = ODataMngr.getFileTable(strTypeID, strFileID);
		Map<String, Object> mapFileInfo = ODataMngr.getFileInfo(strTypeID, strFileID);
		if(mapFileInfo == null || mapFileInfo.size()==0){
			request.setAttribute("errmsg", "文献信息不存在，不能够补录数据");
			request.getRequestDispatcher("/DownloadMngr/adduserdata.jsp").forward(request, response);
			return;
		}
		long lFileLength = (long) (mapFileInfo.get("filesize"));
		UploadInfoBean FileInfo = new UploadInfoBean();
		FileInfo.setFileID(strFileID);
		FileInfo.setFileName(String.valueOf(mapFileInfo.get("filename")));
		FileInfo.setUserName(strUsers);
		FileInfo.setTypeName("caj");
		FileInfo.setFileLength(lFileLength);
		FileInfo.setRange("0-".concat(String.valueOf(lFileLength - 1)));
		FileInfo.setIsCompleted(1);
		FileInfo.setClient("backstage");//表示为补录数据
		FileInfo.setAddress("");
		FileInfo.setFileMd5(String.valueOf(mapFileInfo.get("filemd5")));
		FileInfo.setIsHahepub(mapFileInfo.get("ishasepub") == null ? 0 : Integer.parseInt(String.valueOf(mapFileInfo.get("ishasepub"))));
		FileInfo.setTypeid(strTypeID);
		FileInfo.setFileTable(strCajTabName);
		FileInfo.setDskFileName(strFileID.concat(".caj"));
		if(UploadMngr.addUploadInfo(FileInfo)){
			request.setAttribute("errmsg", "补录成功");
		}else{
			request.setAttribute("errmsg", "补录失败，请检查数据");
		}
		request.getRequestDispatcher("/DownloadMngr/adduserdata.jsp").forward(request, response);
	}

}
