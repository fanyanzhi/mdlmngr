package Init;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;

import BLL.Logger;
import BLL.ODataHelper;
import DAL.DBHelper;
import Model.HotLiterature;
import Model.HotTemp;
import Util.Common;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class HotStatisticSubTaskTwo implements Callable<Boolean> {
	
	private List<HotTemp> hotlist;
	
	public HotStatisticSubTaskTwo(List<HotTemp> hotlist){
		this.hotlist = hotlist;
	}

	@Override
	public Boolean call() throws Exception {
		if(null == hotlist || hotlist.size() == 0) {
			return false;
		}
		StringBuilder query = new StringBuilder();
		for (HotTemp hotTemp : hotlist) {
			query.append("id eq '"+hotTemp.getFileid()+"' or ");
		}
		String strquery = query.toString();
		strquery = strquery.substring(0, strquery.length()-3);
		JSONObject odataList = ODataHelper.GetObjDataLists("Literature{CJFD,CDFD,CMFD,CCND,CPFD}",
				 "id,Title,Creator,Date,Year,issue,IndustryCatagoryCode,Source,SourceCode", strquery, "", "", 0, hotlist.size());
		if(null == odataList) {
			return false;
		}
		
		HashSet<HotLiterature> hset = new HashSet<HotLiterature>();
		JSONArray jsonArray = odataList.getJSONArray("Rows");
		for(int i=0; i<jsonArray.size(); i++) {
			HotLiterature pojo = new HotLiterature();
			
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String idstr = jsonObject.getString("Id").trim().toLowerCase();
			for (HotTemp hotTemp : hotlist) {
				String fileid = hotTemp.getFileid();
				String fileidLow = fileid.trim().toLowerCase();
				if(Common.IsNullOrEmpty(fileidLow)) {
					continue;
				}
				if(idstr.contains(fileidLow)) {
					pojo.setFileid(fileid);
					pojo.setTypeid(hotTemp.getTypeid());
					pojo.setFactor(hotTemp.getFactor());
					break;
				}
			}
			
			JSONArray jsonArray2 = jsonObject.getJSONArray("Cells");
			for(int j=0; j<jsonArray2.size(); j++) {
				JSONObject jsonObject2 = jsonArray2.getJSONObject(j);
				String key = jsonObject2.getString("Name");
				String value = jsonObject2.getString("Value");
				if("Title".equalsIgnoreCase(key)) {
					pojo.setFilename(value);
				}else if("Creator".equalsIgnoreCase(key)) {
					pojo.setAuthor(value);
				}else if("Date".equalsIgnoreCase(key)) {
					pojo.setDate(value);
				}else if("Year".equalsIgnoreCase(key)) {
					pojo.setYear(value);
				}else if("Issue".equalsIgnoreCase(key)) {
					pojo.setIssue(value);
				}else if("IndustryCatagoryCode".equalsIgnoreCase(key)) {
					if(Common.IsNullOrEmpty(value)) {
						continue;
					}
					pojo.setCode(value);
				}else if("Source".equalsIgnoreCase(key)) {
					pojo.setSourcech(value);
				}else if("SourceCode".equalsIgnoreCase(key)) {
					pojo.setSource(value);
				}
			}
			
			DateTime dt = new DateTime();
			dt = dt.minusDays(1);
			pojo.setTime(dt.toString("yyyy-MM-dd HH:mm:ss"));
			String code = pojo.getCode();
			if(!Common.IsNullOrEmpty(code)) {
				hset.add(pojo);
			}
		}
		
		//批量插入
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance("Behaviour");
			String sql = "insert into hotliteraturedaily(fileid,typeid,filename,author,date,year,issue,code,source,sourcech,factor,time) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			dbHelper.ExecuteSqlHotStatistic(sql, hset);
		} catch (Exception e) {
			System.out.println(e);
			Logger.WriteException(e);
		}
		
		return true;
	}
	
}
