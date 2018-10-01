package Request;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.FunctionInfoMngr;
import BLL.Logger;
import DAL.DBHelper;

/**
 * Servlet implementation class StatisticAnalysisHandlerServlet
 */
@WebServlet("/FunctionInfoHandler.do")
public class FunctionInfoHandlerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public FunctionInfoHandlerServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");
		String strResult = "";
		PrintWriter out = response.getWriter();
		if ("getcount".equals(request.getParameter("do"))) {
			strResult = getFunctionInfoCount(request);
		} else if ("getlist".equals(request.getParameter("do"))) {
			strResult = getFunctionInfoList(request);
		}
		out.write(strResult);
		out.flush();
		out.close();
	}

	private String getFunctionInfoCount(HttpServletRequest request) {
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		int count = 0;

		if (txtStartDate.equals("") && txtEndDate.equals("")) {
			count = 1;
		}

		if (!txtStartDate.equals("") && !txtEndDate.equals("")) {
			List<YMD> th = getTH(txtStartDate, txtEndDate);
			count = th.size() / 7;
			int m = th.size() % 7;
			if (m > 0) {
				count += 1;
			}
		}

		return String.valueOf(count);
	}

	// 首页 签到
	// 首页 主题推荐
	// 首页 查看出版物
	// 首页 今日推荐
	// 首页 为您推荐
	//
	// 搜索 文献检索
	// 搜索 出版物检索
	// 搜索 高级检索
	//
	// 我的图书馆 定制添加
	// 我的图书馆 定制删除
	//
	// 文档管理 扫一扫
	// 文档管理 资料库文档查看
	// 文档管理 资料库文档删除
	// 文档管理 资料库文档移动
	// 文档管理 文档云同步
	// 文档管理 文献分享操作
	//
	// 设置 关联操作

	private List<YMD> getTH(String txtStartDate, String txtEndDate) {
		List<YMD> cals = new ArrayList<YMD>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		if (txtStartDate.equals("") && txtEndDate.equals("")) {
			long sevenDay = 7 * 24 * 60 * 60 * 1000;
			long cut = calendar.getTime().getTime() - sevenDay;
			Date firstDate = new Date(cut);
			calendar.setTime(firstDate);
			YMD ymd = new YMD(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DAY_OF_MONTH));
			ymd.setDate(calendar.getTime());
			cals.add(ymd);
			// System.out.println("nulldate:" + calendar.getTime());

			for (int j = 1; j <= 6; j++) {
				calendar.add(Calendar.DAY_OF_MONTH, 1);
				YMD ymd2 = new YMD(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
						calendar.get(Calendar.DAY_OF_MONTH));
				ymd2.setDate(calendar.getTime());
				// System.out.println("nulldate:" + calendar.getTime());
				cals.add(ymd2);
			}

		}

		if (!txtStartDate.equals("") && !txtEndDate.equals("")) {
			try {
				Date startDate = format.parse(txtStartDate);
				Calendar tempStart = Calendar.getInstance();
				tempStart.setTime(startDate);
				Date endDate = format.parse(txtEndDate);
				Calendar tempEnd = Calendar.getInstance();
				tempEnd.setTime(endDate);
				if (tempStart.after(tempEnd)) {
					tempStart.setTime(endDate);
					tempEnd.setTime(startDate);
				}
				// System.out.println("nondate:" + tempStart.getTime());
				YMD ymd = new YMD(tempStart.get(Calendar.YEAR), tempStart.get(Calendar.MONTH),
						tempStart.get(Calendar.DAY_OF_MONTH));
				ymd.setDate(tempStart.getTime());
				cals.add(ymd);
				while (tempStart.before(tempEnd)) {
					tempStart.add(Calendar.DAY_OF_MONTH, 1);
					YMD ymd2 = new YMD(tempStart.get(Calendar.YEAR), tempStart.get(Calendar.MONTH),
							tempStart.get(Calendar.DAY_OF_MONTH));
					ymd2.setDate(tempStart.getTime());
					// System.out.println("nondate:" + tempStart.getTime());
					cals.add(ymd2);
				}

				if (cals.size() < 7) {
					YMD first = cals.get(0);
					calendar.setTime(first.getDate());
					calendar.add(Calendar.DAY_OF_MONTH, -1);
					cals.clear();
					for (int i = 0; i < 7; i++) {
						calendar.add(Calendar.DAY_OF_MONTH, 1);
						YMD ymd2 = new YMD(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
								calendar.get(Calendar.DAY_OF_MONTH));
						ymd2.setDate(calendar.getTime());
						// System.out.println("lastdate:" + calendar.getTime());
						cals.add(ymd2);
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}

		return cals;
	}

	public static void main(String[] args) {
		String start = "2017-11-15";
		String end = "2017-11-20";
		FunctionInfoHandlerServlet servlet = new FunctionInfoHandlerServlet();
		servlet.getTH(start, end);

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			List<Map<String, Object>> executeQuery = dbHelper.ExecuteQuery(
					" select *, DATE_FORMAT(spottime,'%Y-%m-%d')as ymd from appmodulestatis where 1=1 and spottime='2017-11-20' order by spottime asc");
			System.out.println();
		} catch (Exception e1) {
			Logger.WriteException(e1);
		}

	}

	private String getFunctionInfoList(HttpServletRequest request) throws ServletException, IOException {
		StringBuilder sbHtml = new StringBuilder();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String txtStartDate = request.getParameter("txtStartDate");
		String txtEndDate = request.getParameter("txtEndDate");
		String pageIndex = request.getParameter("start");// 从1开始
		// String pageSize = request.getParameter("len");// pageSize为1
		List<TableData> tableList = createTableList();// 创建表格实体
		List<YMD> th = getTH(txtStartDate, txtEndDate);

		int colStartIndex = (Integer.parseInt(pageIndex) - 1) * 7;
		int colEndIndex = colStartIndex + 7;
		colEndIndex = colEndIndex > th.size() ? th.size() : colEndIndex;
		List<YMD> thSub = th.subList(colStartIndex, colEndIndex);

		Date startDate = thSub.get(0).getDate();
		String start = format.format(startDate);
		Date endDate = thSub.get(thSub.size() - 1).getDate();
		String end = format.format(endDate);
		for (int i = 0, len = tableList.size(); i < len; i++) {
			TableData row = tableList.get(i);
			String type = row.getType();
			List<Map<String, Object>> list = FunctionInfoMngr.getFunctionInfoList(type, start, end);
			if (null == list) {
				continue;
			} else {
				// [{id=24, username=null, time=null, count=null,
				// spottime=2017-11-20, baseos=null, type=0101}]
				// {id=24, username=null, time=null, count=null, ymd=2017-11-20,
				// spottime=2017-11-20, baseos=null, type=0101}
				for (Iterator<Map<String, Object>> iterator = list.iterator(); iterator.hasNext();) {
					Map<String, Object> map = (Map<String, Object>) iterator.next();
					for (int j = 0, thSubLen = thSub.size(); j < thSubLen; j++) {
						YMD ymd = thSub.get(j);
						Date thDate = ymd.getDate();
						String thDateColumn = format.format(thDate);
						if (map.get("type").equals(type) && map.get("ymd").equals(thDateColumn)) {
							Object count = map.get("count") == null ? "" : map.get("count");
							row.setColumnValue(j + 1, count.toString());
						}
					}
				}
			}

		}

		sbHtml.append(
				"<table id=\"functionInfoTab\" name=\"functionInfoTab\"  width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\" class=\"tabone\">");
		sbHtml.append("<tr>");
		sbHtml.append("<th></th>");
		sbHtml.append("<th>功能模块</th>");
		// sbHtml.append("<th>1号</th>");
		// sbHtml.append("<th>2号</th>");
		// sbHtml.append("<th>3号</th>");
		// sbHtml.append("<th>4号</th>");
		// sbHtml.append("<th>5号</th>");
		// sbHtml.append("<th>6号</th>");
		// sbHtml.append("<th>7号</th>");
		// int colStartIndex = (Integer.parseInt(pageIndex) - 1) * 7;
		// int colEndIndex = colStartIndex + 7;
		// colEndIndex = colEndIndex > th.size() ? th.size() : colEndIndex;
		// List<YMD> thSub = th.subList(colStartIndex, colEndIndex);
		Calendar thCalendar = Calendar.getInstance();
		int diff = thSub.size();
		for (int i = 0, len = thSub.size(); i < len; i++) {
			YMD ymd = thSub.get(i);
			thCalendar.setTime(ymd.getDate());
			sbHtml.append("<th>");

			sbHtml.append(thCalendar.get(Calendar.MONTH) + 1);
			sbHtml.append("月");
			sbHtml.append(thCalendar.get(Calendar.DAY_OF_MONTH));
			sbHtml.append("号");

			sbHtml.append("</th>");

		}
		if (7 - diff > 0) {
			for (int i = 0; i < 7 - diff; i++) {
				sbHtml.append("<th>");
				sbHtml.append("");
				sbHtml.append("</th>");

			}
		}

		sbHtml.append("</tr>");
		// 获取数据封装好的row
		for (int i = 0, len = tableList.size(); i < len; i++) {
			TableData tableData = tableList.get(i);
			// 列赋值

			StringBuffer row = tableData.getHTML();
			sbHtml.append(row);
		}
		sbHtml.append("</table>");
		return sbHtml.toString();
	}

	private List<TableData> createTableList() {
		ArrayList<TableData> tableList = new ArrayList<TableData>();

		TableData one = new TableData("首页", "签到", "0101", true, 5);
		tableList.add(one);

		TableData two = new TableData("首页", "主题推荐", "0102", false);
		tableList.add(two);

		TableData three = new TableData("首页", "查看出版物", "0103", false);
		tableList.add(three);

		TableData four = new TableData("首页", "今日推荐", "0104", false);
		tableList.add(four);

		TableData five = new TableData("首页", "为您推荐", "0105", false);
		tableList.add(five);

		TableData six = new TableData("搜索", "文献检索", "0201", true, 3);
		tableList.add(six);

		TableData seven = new TableData("搜索", "出版物检索", "0202", false);
		tableList.add(seven);

		TableData eight = new TableData("搜索", "高级检索", "0203", false);
		tableList.add(eight);

		TableData nine = new TableData("我的图书馆", "定制添加", "0301", true, 2);
		tableList.add(nine);

		TableData ten = new TableData("我的图书馆", "定制删除", "0302", false);
		tableList.add(ten);

		TableData eleven = new TableData("文档管理", "扫一扫", "0401", true, 6);
		tableList.add(eleven);

		TableData twelve = new TableData("文档管理", "资料库文档查看", "0402", false);
		tableList.add(twelve);

		TableData thirteen = new TableData("文档管理", "资料库文档删除", "0403", false);
		tableList.add(thirteen);

		TableData fourteen = new TableData("文档管理", "资料库文档移动", "0405", false);
		tableList.add(fourteen);

		TableData fifteen = new TableData("文档管理", "文档云同步", "0406", false);
		tableList.add(fifteen);

		TableData sixteen = new TableData("文档管理", "文献分享操作", "0407", false);
		tableList.add(sixteen);

		TableData seventeen = new TableData("设置", "关联操作", "0501", true);
		tableList.add(seventeen);

		return tableList;
	}

	private class YMD {
		private int year;
		private int month;
		private int day;
		private Date date;

		public YMD(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		@Override
		public String toString() {
			return this.year + "-" + this.month + "-" + this.day;
		}
	}

	private class TableData {
		private int rowspan = 1;// 1表示单行
		private boolean hasFirstTitle = true;// 表示单行
		private String firstTitle;
		private String secondTitle;
		private String type;
		private String first;
		private String second;
		private String third;
		private String fourth;
		private String fifth;
		private String sixth;
		private String seventh;

		public String getColumn(int index) {
			if (index == 1) {
				return first == null ? "" : first;
			} else if (index == 2) {
				return second == null ? "" : second;
			} else if (index == 3) {
				return third == null ? "" : third;
			} else if (index == 4) {
				return fourth == null ? "" : fourth;
			} else if (index == 5) {
				return fifth == null ? "" : fifth;
			} else if (index == 6) {
				return sixth == null ? "" : sixth;
			} else if (index == 7) {
				return seventh == null ? "" : seventh;
			}
			return "";
		}

		public void setColumnValue(int index, String value) {
			if (index == 1) {
				first = value;
			} else if (index == 2) {
				second = value;
			} else if (index == 3) {
				third = value;
			} else if (index == 4) {
				fourth = value;
			} else if (index == 5) {
				fifth = value;
			} else if (index == 6) {
				sixth = value;
			} else if (index == 7) {
				seventh = value;
			}
		}

		public TableData(String firstTitle, String secondTitle, String type, boolean hasFirstTitle) {
			this(firstTitle, secondTitle, type, hasFirstTitle, 1);
		}

		public TableData(String firstTitle, String secondTitle, String type, boolean hasFirstTitle, int rowspan) {
			this.firstTitle = firstTitle;
			this.secondTitle = secondTitle;
			this.type = type;
			this.hasFirstTitle = hasFirstTitle;
			this.rowspan = rowspan;
		}

		public String getType() {
			return type;
		}

		public StringBuffer getHTML() {

			StringBuffer html = new StringBuffer();
			html.append("<tr>");

			// 一级模块
			if (hasFirstTitle) {
				html.append("<td");
				if (rowspan > 1) {
					html.append(" rowspan=\"");
					html.append(rowspan);
					html.append("\"");
				}
				html.append(" align=\"center\"");
				html.append(">");
				html.append(firstTitle);
				html.append("</td>");
			}

			// 二级模块
			html.append("<td>");
			html.append(secondTitle);
			html.append("</td>");

			// 七个列
			for (int i = 1; i <= 7; i++) {
				html.append("<td class=\"tabcent\">");
				html.append(getColumn(i));
				html.append("</td>");
			}
			html.append("</tr>");
			return html;
		}
	}

}
