package Init;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.joda.time.DateTime;

import BLL.Logger;
import BLL.ODataHelper;
import DAL.DBHelper;
import Model.BeanDateConnverter;
import Model.HotTemp;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HotStatisticTask implements Runnable {

	@Override
	public void run() {
		System.out.println("开始时间" + new Date());
		long s = System.currentTimeMillis();
		clear("hottemp1");
		clear("hottemp2");
		insertFromDownAndBrowse("downloadinfo", 3, null); //下载权重是3
		insertFromDownAndBrowse("userbrowse", 7, "Behaviour"); //浏览权重是7
		insertTemp2();
		insertHotliteraturedaily();
		System.out.println("daily结束时间" + new Date());
		statistic();
		System.out.println("整体结束时间" + new Date());
		long e = System.currentTimeMillis();
		System.out.println("时间差" + (e-s)/1000);
	}
	
	// 将昨天的数据插入hottemp1表
	private void insertFromDownAndBrowse(String tablename, int weight, String behaviour) {
		ExecutorService downExecutorService = Executors.newCachedThreadPool();
		List<Future<Boolean>> downloadList = new ArrayList<Future<Boolean>>();
		for (int i = 0; i <= 9; i++) {
			HotStatisticSubTask subTaskDownload = new HotStatisticSubTask(tablename + i, weight, behaviour);
			Future<Boolean> future = downExecutorService.submit(subTaskDownload);
			downloadList.add(future);
		}

		downExecutorService.shutdown();
		for (Future<Boolean> future : downloadList) {
			try {
				future.get();
			} catch (InterruptedException e) {
				Logger.WriteException(e);
			} catch (ExecutionException e) {
				Logger.WriteException(e);
				downExecutorService.shutdownNow();
			}
		}
	}

	// 清空临时表
	private void clear(String tablename) {
		DBHelper dbHelper = null;
		try {
			String sql = "truncate table " + tablename;
			dbHelper = DBHelper.GetInstance();
			dbHelper.ExecuteSql(sql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	// 将昨天的数据插入hottemp2表
	private void insertTemp2() {
		DBHelper dbHelper = null;
		try {
			String sql = "insert into hottemp2(fileid, typeid, factor) SELECT fileid, typeid, sum(factor) FROM `hottemp1` group by fileid order by null";
			dbHelper = DBHelper.GetInstance();
			dbHelper.ExecuteSql(sql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}

	//昨天的数据插入hotliteraturedaily
	private void insertHotliteraturedaily() {
		DBHelper dbHelper = null;
		ExecutorService hotDailyExecutorService = null;
		try {
			dbHelper = DBHelper.GetInstance();
			int rescount = dbHelper.GetCount("hottemp2", null);	//总条数
			int start = 0;
			hotDailyExecutorService = Executors.newFixedThreadPool(10);
			while (start < rescount) {
				String sql = "SELECT * FROM `hottemp2` order by factor desc limit " + start + ", 500";
				start += 500;
				List<Map<String, Object>> list = dbHelper.ExecuteQuery(sql);
				
				ConvertUtils.register(new BeanDateConnverter(), Date.class);
				List<HotTemp> reslist = new ArrayList<HotTemp>(500);
				if (null != list && list.size() > 0) {
					for (Map<String, Object> map : list) {
						HotTemp hotTemp = new HotTemp();
						BeanUtils.populate(hotTemp, map);
						String fileid = hotTemp.getFileid();
						if(Common.IsNullOrEmpty(fileid)) {
							continue;
						}
						String typeid = hotTemp.getTypeid();
						if("journals".equalsIgnoreCase(typeid) || "CAPJ".equalsIgnoreCase(typeid) || "CJFDALL".equalsIgnoreCase(typeid)){
							hotTemp.setTypeid("CJFD");
						}else if("newspapers".equalsIgnoreCase(typeid)){
							hotTemp.setTypeid("CCND");
						}else if("doctortheses".equalsIgnoreCase(typeid)) {
							hotTemp.setTypeid("CDFD");
						}else if("mastertheses".equalsIgnoreCase(typeid)) {
							hotTemp.setTypeid("CMFD");
						}else if("conferences".equalsIgnoreCase(typeid)) {
							hotTemp.setTypeid("CPFD");
						}
						reslist.add(hotTemp);
					}
				}else {
					break;
				}
				
				int listSize = reslist.size();
				int num = listSize / 50;
				
				List<Future<Boolean>> hotdailyList = new ArrayList<Future<Boolean>>();
				for(int i = 0; i < num; i++) {
					List<HotTemp> hotlistTemp = new ArrayList<HotTemp>();
					for(int p=0, k=i*50; p<50; p++, k++) {
						hotlistTemp.add(reslist.get(k));
					}
					
					HotStatisticSubTaskTwo hotStatisticSubTaskTwo = new HotStatisticSubTaskTwo(hotlistTemp);
					Future<Boolean> future = hotDailyExecutorService.submit(hotStatisticSubTaskTwo);
					hotdailyList.add(future);
				}
				
				int remainder = listSize % 50;
				if(remainder > 0) {
					List<HotTemp> hotlistTemp = new ArrayList<HotTemp>();
					for(int i=0, k=num*50; i<remainder; i++, k++) {
						hotlistTemp.add(reslist.get(k));
					}
					HotStatisticSubTaskTwo hotStatisticSubTaskTwo = new HotStatisticSubTaskTwo(hotlistTemp);
					Future<Boolean> future = hotDailyExecutorService.submit(hotStatisticSubTaskTwo);
					hotdailyList.add(future);
				}
				
				for (Future<Boolean> future : hotdailyList) {
					try {
						future.get();
					} catch (InterruptedException e) {
						System.out.println(e);
						Logger.WriteException(e);
					} catch (ExecutionException e) {
						System.out.println(e);
						Logger.WriteException(e);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
			Logger.WriteException(e);
		}finally {
			hotDailyExecutorService.shutdownNow();
		}
	}
	
	private void statistic() {
		DBHelper dbHelper = null;
		try {
			DateTime dendDate = new DateTime();
			DateTime dstartDate = dendDate.minusDays(15);	//15天热度
			String sql = "insert into hotliterature(fileid, typeid, factor,filename,author,date,year,issue,code,source,sourcech,time) "
					+ "SELECT fileid, typeid, sum(factor) s,filename,author,date,year,issue,code,source,sourcech,DATE_ADD(curdate(),INTERVAL -1 day) FROM `hotliteraturedaily` where time >= '"+dstartDate.toString("yyyy-MM-dd")+"' and time < '"+dendDate.toString("yyyy-MM-dd")+"' group by fileid order by s desc";
			dbHelper = DBHelper.GetInstance("Behaviour");
			dbHelper.ExecuteSql(sql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}
	
}
