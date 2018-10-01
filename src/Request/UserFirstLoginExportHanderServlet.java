package Request;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import BLL.UserFirstLogMngr;
import Model.UserLoginBean;
import Util.Common;

/**
 * Servlet implementation class UserLoginListHanderServlet
 */
@WebServlet("/UserFirstLoginExportHander.do")
public class UserFirstLoginExportHanderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserFirstLoginExportHanderServlet() {
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
		
		String strStartDate = request.getParameter("startdate");
		String strEndDate = request.getParameter("enddate");
		int role = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getRole();
		String appid = null;
		if (role == 3) {
			appid = ((UserLoginBean) request.getSession().getAttribute("LoginObj")).getAppid();
		}else{
			appid = request.getParameter("appid");
		}
		String strUserName = request.getParameter("uname");
		
		//========
		List<Map<String, Object>> reslist = UserFirstLogMngr.getAll(appid, strUserName, strStartDate, strEndDate);
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("首次登录");
		HSSFRow headRow = sheet.createRow(0);
		headRow.createCell(0).setCellValue("用户名");
		headRow.createCell(1).setCellValue("设备型号");
		headRow.createCell(2).setCellValue("AppId");
		headRow.createCell(3).setCellValue("App版本");
		headRow.createCell(4).setCellValue("IP地址");
		headRow.createCell(5).setCellValue("登录时间");
		
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");

		if (null != reslist && reslist.size() > 0) {
			for(Map<String, Object> map : reslist) {
				HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
				
				Object ousername = map.get("username");
				if(null != ousername) {
					dataRow.createCell(0).setCellValue((String)(map.get("username")));
				}else {
					dataRow.createCell(0).setCellValue("");
				}
				
				Object oclient = map.get("client");
				if(null != oclient) {
					dataRow.createCell(1).setCellValue((String)(map.get("client")));
				}else {
					dataRow.createCell(1).setCellValue("");
				}
				
				Object oappid = map.get("appid");
				if(null != oappid) {
					dataRow.createCell(2).setCellValue((String)(map.get("appid")));
				}else {
					dataRow.createCell(2).setCellValue("");
				}
				
				Object oversion = map.get("version");
				if(null != oversion) {
					dataRow.createCell(3).setCellValue((String)(map.get("version")));
				}else {
					dataRow.createCell(3).setCellValue("");
				}
				
				Object oaddress = map.get("address");
				if(null != oaddress) {
					dataRow.createCell(4).setCellValue((String)(map.get("address")));
				}else {
					dataRow.createCell(4).setCellValue("");
				}
				
				Object otime = map.get("time");
				if(null != otime) {
					String sdate = Common.ConvertToDateTime(String.valueOf(map.get("time")));
					dataRow.createCell(5).setCellValue(sdate);
				}else {
					dataRow.createCell(5).setCellValue("");
				}
			}
		}
		
		String filename = "首次登录.xls";
		String agent = request.getHeader("User-Agent");
		try {
			filename = Util.FileUtils.encodeDownloadFilename(filename, agent);
			ServletOutputStream out = response.getOutputStream();
			String mimeType = request.getServletContext().getMimeType(filename);
			response.setContentType(mimeType);
			response.setHeader("content-disposition", "attachment;filename=" + filename);
			workbook.write(out);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
