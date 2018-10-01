package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Iterator;
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

import BLL.SignMngr;
import Util.Common;

/**
 * Servlet implementation class UserSignHandlerServlet
 */
@WebServlet("/UserSignHandler.do")
public class UserSignHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UserSignHandlerServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("html/text;utf-8");
		String strResult = "";
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getUserSignCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getSearchData(request);
		} else if ("excel".equals(request.getParameter("do"))) {
			strResult = exportExcel(request, response);
		}
		if (!Common.IsNullOrEmpty(strResult)) {
			PrintWriter out = response.getWriter();
			out.write(strResult);
			out.flush();
			out.close();
		}
	}

	protected String getUserSignCount(HttpServletRequest request) {
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String txtscount = request.getParameter("txtscount");
		String txtssum = request.getParameter("txtssum");
		int count = SignMngr.getUserSignCount(txtUserName, txtStartDate, txtEndDate, txtscount, txtssum);
		return String.valueOf(count);
	}

	protected String getSearchData(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String txtscount = request.getParameter("txtscount");
		String txtssum = request.getParameter("txtssum");
		String start = request.getParameter("start");
		String len = request.getParameter("len");
		List<Map<String, Object>> userSignList = SignMngr.getUserSignList(txtUserName, txtStartDate, txtEndDate,
				txtscount, txtssum, Integer.parseInt(start), Integer.parseInt(len));
		Iterator<Map<String, Object>> iMap = userSignList.iterator();
		Map<String, Object> mapData = null;
		sbHtml.append(
				"<table width=\"100%\" id=\"tabserinfo\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th width=\"40\" class=\"num\">&nbsp;</th>");
		sbHtml.append("<th>用户名</th>");
		sbHtml.append("<th>连续签到天数</th>");
		sbHtml.append("<th>累计签到天数</th>");
		sbHtml.append("<th>积分</th>");
		sbHtml.append("<th>最近签到时间</th>");
		sbHtml.append("<th>签到日志</th>");
		sbHtml.append("</tr>");
		int iNum = Integer.parseInt(start);
		while (iMap.hasNext()) {
			mapData = iMap.next();
			sbHtml.append("<td class=\"num\">").append(iNum++).append("</td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("username")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("scount")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("ssum")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">").append(mapData.get("score")).append("</a></td>");
			sbHtml.append("<td class=\"tabcent\">")
					.append(Common.ConvertToDateTime(String.valueOf(mapData.get("time")))).append("</td>");
			sbHtml.append("<td class=\"tabopt\"><a href=\"UserSignDetail.do?uname=").append(mapData.get("username"))
					.append("\" class=\"view\" title=\"查看\"></a></td>");
			sbHtml.append("</tr> ");
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	protected String exportExcel(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String txtUserName = request.getParameter("txtUserName");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String txtscount = request.getParameter("txtscount");
		String txtssum = request.getParameter("txtssum");
		List<Map<String, Object>> userSignList = SignMngr.exportUserSignList(txtUserName, txtStartDate, txtEndDate,
				txtscount, txtssum);
		if (userSignList != null && userSignList.size() > 0) {
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("签到日志");
			HSSFRow headRow = sheet.createRow(0);
			headRow.createCell(0).setCellValue("用户名");
			headRow.createCell(1).setCellValue("连续签到天数");
			headRow.createCell(2).setCellValue("累计签到天数");
			headRow.createCell(3).setCellValue("积分");
			headRow.createCell(4).setCellValue("最近签到时间");

			if (null != userSignList && userSignList.size() > 0) {
				for (Map<String, Object> signmap : userSignList) {
					HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
					dataRow.createCell(0).setCellValue(signmap.get("username").toString());
					dataRow.createCell(1).setCellValue(signmap.get("scount").toString());
					dataRow.createCell(2).setCellValue(signmap.get("ssum").toString());
					dataRow.createCell(3).setCellValue(signmap.get("score").toString());
					dataRow.createCell(4).setCellValue(Common.ConvertToDateTime(String.valueOf(signmap.get("time"))));
				}
			}

			String filename = "签到日志.xls";
			try {
				filename = URLEncoder.encode(filename, "utf-8");
				ServletOutputStream out = response.getOutputStream();
				String mimeType = request.getServletContext().getMimeType(filename);
				response.setContentType(mimeType);
				response.setHeader("content-disposition", "attachment;filename=" + filename);
				workbook.write(out);
			} catch (IOException e) {
			}
		} else {
			return "数据为空";
		}
		return "";
	}

}
