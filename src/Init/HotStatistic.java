package Init;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import BLL.ODataHelper;
import Util.Common;
import net.sf.json.JSONObject;

public class HotStatistic implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		String swtich = Common.GetConfig("switch");
		if (!"1".equals(swtich)) {
			return;
		}

		String runtimestr = Common.GetConfig("runtime");
		long oneDay = 24 * 3600;
		long initDelay = (getTimeMillis(runtimestr) - System.currentTimeMillis())/1000;
		initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
		ScheduledExecutorService newScheduledThreadPool = Executors.newScheduledThreadPool(1, new ThreadFactory() {
			public Thread newThread(Runnable r) {
				Thread s = Executors.defaultThreadFactory().newThread(r);
				s.setDaemon(true);
				return s;
			}
		});
		//这个延迟1也只是测试用的，应该是initDelay
		newScheduledThreadPool.scheduleAtFixedRate(new HotStatisticTask(), initDelay, oneDay, TimeUnit.SECONDS);
	}

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
	
	public static void main(String[] args) {
		JSONObject odataList = ODataHelper.GetObjDataLists("Literature{CJFD,CDFD,CMFD,CCND,CPFD}",
				 "id,Title,Creator,Date,Year,issue,IndustryCatagoryCode,Source,SourceCode", "id eq '1014296358.nh' or id eq 'JJDL201703009'", "", "", 0, 10);
	}
}
