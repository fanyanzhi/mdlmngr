package Init;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import BLL.KuaiBaoMngr;
import BLL.ModuleMngr;
import BLL.UserInfoMngr;
import Util.Common;

/**
 * Servlet implementation class AppInitServlet
 */
@WebServlet("/AppInit")
public class AppInitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Timer timer = null;

	@Override
	public void destroy() {
		super.destroy();
		System.err.close();
	}

	public void init() {
		// String strFileName = Common.GetConfig("ErrorLog");
		// String strDate = Common.GetDateTime("MMdd");
		// int iPos = strFileName.lastIndexOf(".");
		// strFileName = strFileName.substring(0,
		// iPos).concat(strDate).concat(strFileName.substring(iPos));
		// File file = new File(strFileName);
		// try {
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		// System.setErr(new PrintStream(new FileOutputStream(file, true)));
		// } catch (Exception ex) {
		// Logger.WriteException(ex);
		// } finally {
		// file = null;
		// }

		if (timer == null && Boolean.valueOf(Common.GetConfig("IsDBSchedule"))) {
			String strScheduleInterval = Common.GetConfig("ScheduleInterval");
			int iInterval = 3600000;
			if (strScheduleInterval != null) {
				iInterval = Integer.valueOf(strScheduleInterval);
			}
			timer = new Timer();
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					TimerCallBack();
				}
			}, 0, iInterval);
		}
	/*	if("1".equals(Common.GetConfig("Push")))
		executeEightAtNightPerDay();*/
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AppInitServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	private void TimerCallBack() {
		//String strDownServer = Common.GetConfig("DownServer");
		//Common.requestUrl(strDownServer+"/BackgroundProcess/examineerrorfile");
		//Common.requestUrl("http://localhost:8080/mdlmngr/BackgroundProcess/transepub");
		//UserInfoMngr.updateUserOnlineCount();
		Calendar now = Calendar.getInstance();
		if (now.get(Calendar.HOUR_OF_DAY) != 0) {
			return;
		}
		ModuleMngr.checkModuleTable();
		UserInfoMngr.checkOnlineUser();
		// TODO
	}
	
	public static void executeEightAtNightPerDay() { 
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);  
		long oneDay = 24 * 60 * 60 * 1000;  
		long initDelay  = getTimeMillis(Common.GetConfig("PushTime")) - System.currentTimeMillis();  
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;  
		//writeLog("jinlaile");
		 executor.scheduleAtFixedRate(  
		          new EchoServer(),  
		            initDelay,  
		           oneDay,  
		           TimeUnit.MILLISECONDS);  
		}  
	public static void writeLog(String data) {
		File file = new File("d:\\rr.txt");
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter sucsessFile = new FileWriter(file, true);
			sucsessFile.write(data + "\r\n");
			sucsessFile.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

		}
	}
	/** 
	* 获取指定时间对应的毫秒数 
	* @param time "HH:mm:ss" 
	* @return 
	*/  
	private static long getTimeMillis(String time) {  
	   try {  
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");  
	        DateFormat dayFormat = new SimpleDateFormat("yy-MM-dd");  
	        Date curDate = dateFormat.parse(dayFormat.format(new Date()) + " " + time);  
	        return curDate.getTime();  
	    } catch (ParseException e) {  
	        e.printStackTrace();  
	    }  
	    return 0;  
	}  


}

class EchoServer implements Runnable {  
    @Override  
    public void run() {  
        try {  
            Thread.sleep(50);  
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
        KuaiBaoMngr.jPushXkcls();
    } 
}  

