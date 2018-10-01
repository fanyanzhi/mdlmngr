package Init;

import java.util.concurrent.Callable;
import org.joda.time.DateTime;
import BLL.Logger;
import DAL.DBHelper;

public class HotStatisticSubTask implements Callable<Boolean>{
	
	private String tablename;
	private int weight;
	private String behaviour;

	public HotStatisticSubTask(String tablename, int weight, String behaviour) {
		this.tablename = tablename;
		this.weight = weight;
		this.behaviour = behaviour;
	}

	@Override
	public Boolean call() {
		boolean bolRet = false;
		DBHelper dbHelper = null;
		DateTime dendDate = new DateTime();
		DateTime dstartDate = dendDate.minusDays(1);	//测试数据半年的，正常应该是1
		try {
			String sql = "";
			if(null != behaviour) {	//behaviour数据库的userbrowse
				dbHelper = DBHelper.GetInstance("Behaviour");
				sql = "insert into mdlmngr.hottemp1(fileid, typeid, factor) select fileid, odatatype, count(*)*"+weight+" FROM mdlbehaviour."+tablename+" where time >= '"+dstartDate.toString("yyyy-MM-dd")+"' and time < '"+dendDate.toString("yyyy-MM-dd")+"' group by fileid order by null";
			}else {
				dbHelper = DBHelper.GetInstance();
				sql = "insert into mdlmngr.hottemp1(fileid, typeid, factor) select fileid, typeid, count(*)*"+weight+" FROM mdlmngr."+tablename+" where time >= '"+dstartDate.toString("yyyy-MM-dd")+"' and time < '"+dendDate.toString("yyyy-MM-dd")+"' group by fileid order by null";
			}
			bolRet = dbHelper.ExecuteSql(sql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

}
