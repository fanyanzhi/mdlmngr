package SysMngr;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.filechooser.FileSystemView;

import com.cnki.monitor.model.MonitorInfoBean;
import com.cnki.monitor.service.IMonitorService;
import com.cnki.monitor.service.impl.MonitorServiceImpl;

/**
 * Servlet implementation class PcMonitorServlet
 */
@WebServlet("/PcMonitor.do")
public class PcMonitorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public PcMonitorServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;utf-8");

		IMonitorService service = new MonitorServiceImpl();
		MonitorInfoBean monitorInfo = null;
		try {

			monitorInfo = service.getMonitorInfoBean();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (monitorInfo != null) {
			request.setAttribute("OSInfo", monitorInfo.getOsName());
			request.setAttribute("CPUInfo", monitorInfo.getCpuRatio() + "%");
			request.setAttribute("TotalMemory", monitorInfo.getTotalMemorySize() + "G");
			request.setAttribute("UsedMemory", monitorInfo.getUsedMemory() + "G");
			request.setAttribute("FreeMemory", monitorInfo.getFreePhysicalMemorySize() + "G");
		}
		StringBuilder sbHtml = new StringBuilder();
		FileSystemView fileSys = FileSystemView.getFileSystemView();
		long mb = 1024 * 1024;

		for (File f : File.listRoots()) {
			if (f.getTotalSpace() == 0)
				break;
			sbHtml.append("<ul>").append(fileSys.getSystemDisplayName(f));
			sbHtml.append("<li><label class=\"leb\">").append("总容量:").append("</label>").append(f.getTotalSpace() / mb).append("MB").append("</li>");
			sbHtml.append("<li><label class=\"leb\">").append("可用容量:").append("</label>").append(f.getFreeSpace() / mb).append("MB").append("</li>");
			sbHtml.append("</ul>");
		}
		request.setAttribute("DeskHtml", sbHtml.toString());

		request.getRequestDispatcher("/SysMngr/pcmonitor.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
