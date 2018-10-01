package BLL;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import DAL.DBHelper;
import Model.ModuleContentInfoBean;
import Model.ModuleInfoBean;
import Model.ModuleTypeInfoBean;
import Util.Common;

public class ModuleMngr {

	public static String getTableName(String TableNameEN) {
		List<Map<String, Object>> lstTable = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstTable = dbHelper.ExecuteQuery("TableName", "moduleinfo", "TableName_EN='".concat(TableNameEN).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstTable == null) {
			return null;
		}
		String strRet = lstTable.get(0).get("tablename").toString();
		lstTable = null;
		return strRet;
	}

	public static String getTableName(String TableNameEN, String TypeNameEN) {
		List<Map<String, Object>> lstTable = null;
		String strSql = "select lower(concat(taba.TableName,'_',tabb.TypeName)) tablename from moduleinfo taba left join moduletypeinfo tabb on taba.id=tabb.tableid where taba.TableName_EN='".concat(TableNameEN).concat("' and tabb.TypeName_EN='").concat(TypeNameEN).concat("'");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstTable = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstTable == null) {
			return null;
		}
		String strRet = lstTable.get(0).get("tablename").toString();
		lstTable = null;
		return strRet;
	}

	public static String getTableName(int TableID) {
		List<Map<String, Object>> lstTable = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstTable = dbHelper.ExecuteQuery("TableName", "moduleinfo", "id='".concat(dbHelper.FilterSpecialCharacter(String.valueOf(TableID))).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstTable == null) {
			return null;
		}
		String strRet = lstTable.get(0).get("tablename").toString();
		lstTable = null;
		return strRet;
	}

	public static String getTableName(int TableID, int TypeID) {
		List<Map<String, Object>> lstTable = null;
		String strSql = "select concat(taba.TableName,'_',tabb.TypeName) as tablename from moduleinfo taba,moduletypeinfo  tabb where taba.id=".concat(String.valueOf(TableID)).concat(" and tabb.ID=").concat(String.valueOf(TypeID));
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstTable = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstTable == null) {
			return null;
		}
		String strRet = lstTable.get(0).get("tablename").toString();
		lstTable = null;
		return strRet;
	}

	public static List<Map<String, Object>> getFieldNames(String TableNameEN) {
		String strSql = "select FieldName,FieldName_EN from modulecontentinfo where TableID=(select ID from moduleinfo where TableName_EN='".concat(TableNameEN).concat("' )");
		List<Map<String, Object>> lstField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstField = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstField;
	}

	public static List<Map<String, Object>> getPrimKeyFieldNames(String TableNameEN) {
		String strSql = "select FieldName,FieldName_EN from modulecontentinfo where TableID=(select ID from moduleinfo where TableName_EN='".concat(TableNameEN).concat("' ) and isprimkey=1");
		List<Map<String, Object>> lstField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstField = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstField;
	}

	public static String getFieldNameEN(String TableNameEN, String FieldName) {
		String strSql = "select FieldName_EN from modulecontentinfo where TableID=(select ID from moduleinfo where TableName_EN='".concat(TableNameEN).concat("' ) and FieldName ='".concat(FieldName).concat("'"));
		List<Map<String, Object>> lstField = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstField = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstField == null) {
			return null;
		}
		String strRet = lstField.get(0).get("fieldname_en").toString();
		return strRet;
	}

	// /删除模块 好几个

	/*
	 * 获取单个模块信息
	 */
	public static ModuleInfoBean getModuleInfo(String ID) {
		List<Map<String, Object>> lstModuleInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleInfo = dbHelper.ExecuteQuery("tablename,tablename_en,tablename_ch,isdisplay,status,description,isdelete", "moduleinfo", "id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleInfo == null) {
			return null;
		}
		ModuleInfoBean moduleInfo = new ModuleInfoBean(lstModuleInfo.get(0));
		return moduleInfo;
	}

	// /*
	// * 获取模块列表信息
	// */
	// public static List<Map<String, Object>> getModuleList(int Start, int
	// Length) {
	// List<Map<String, Object>> lstModule = new ArrayList<Map<String,
	// Object>>();
	// try {
	// DBHelper dbHelper = DBHelper.GetInstance();
	// lstModule = dbHelper.ExecuteQuery("*", "moduleinfo", "", "TIME DESC",
	// Start, Length);
	// } catch (Exception e) {
	// Logger.WriteException(e);
	// return null;
	// }
	// return lstModule;
	// }

	public static int getModuleCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduleinfo", "isdelete=0");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	/*
	 * 获取模块列表信息
	 */
	public static List<Map<String, Object>> getDisplayModuleList() {
		List<Map<String, Object>> lstModule = new ArrayList<Map<String, Object>>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModule = dbHelper.ExecuteQuery("ID,TableName_CH", "moduleinfo", "IsDelete=0 and isdisplay=1");
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return lstModule;
	}

	public static List<Map<String, Object>> getModuleList(String IsDelete) {
		List<Map<String, Object>> lstModule = new ArrayList<Map<String, Object>>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModule = dbHelper.ExecuteQuery("ID,TableName,TableName_CH,TableName_EN,IsDisplay,Status,IsDelete", "moduleinfo", "IsDelete=".concat(IsDelete));
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return lstModule;
	}

	/*
	 * 获取模块列表信息
	 */
	public static List<Map<String, Object>> getModuleList() {
		List<Map<String, Object>> lstModule = new ArrayList<Map<String, Object>>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModule = dbHelper.ExecuteQuery("ID,TableName,TableName_CH,TableName_EN,IsDisplay,Status,IsDelete,DeleteTime", "moduleinfo");
		} catch (Exception e) {
			Logger.WriteException(e);
			return null;
		}
		return lstModule;
	}

	public static int getDeletedModuleCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduleinfo", "IsDelete=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static boolean isValidModuleID(String ModuleID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (dbHelper.GetCount("moduleinfo", "IsDelete=0 and IsDisplay=1 and ID=".concat(dbHelper.FilterSpecialCharacter(ModuleID))) > 0) {
				bResult = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	/** 模块详情页面 ********************************************************/
	/*
	 * 添加模块信息
	 */
	public static boolean addModuleInfo(ModuleInfoBean ModuleInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("moduleinfo", new String[] { "tablename", "tablename_en", "tablename_ch", "isdisplay", "status", "isdelete", "description", "time" }, new Object[] { ModuleInfo.getTableName(), ModuleInfo.getTableName_EN(), ModuleInfo.getTableName_CH(), ModuleInfo.getIsDisplay() ? 1 : 0, ModuleInfo.getStatus(), ModuleInfo.isDelete() ? 1 : 0, ModuleInfo.getDescription(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 修改模块信息
	 */
	public static boolean updateModuleInfo(ModuleInfoBean ModuleInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			// bolRet = dbHelper.Update("moduleinfo",
			// "ID='".concat(dbHelper.FilterSpecialCharacter(String.valueOf(ModuleInfo.getId()))).concat("'"),
			// new String[] { "TableName", "TableName_EN", "TableName_CH",
			// "IsDisplay", "Status", "description", "Time" }, new Object[] {
			// ModuleInfo.getTableName(), ModuleInfo.getTableName_EN(),
			// ModuleInfo.getTableName_CH(), ModuleInfo.getIsDisplay(),
			// ModuleInfo.getStatus(), ModuleInfo.getDescription(),
			// Common.GetDateTime() });
			bolRet = dbHelper.Update("moduleinfo", "ID='".concat(dbHelper.FilterSpecialCharacter(String.valueOf(ModuleInfo.getId()))).concat("'"), new String[] { "TableName_EN", "TableName_CH", "IsDisplay", "Status", "description", "Time" }, new Object[] { ModuleInfo.getTableName_EN(), ModuleInfo.getTableName_CH(), ModuleInfo.getIsDisplay(), ModuleInfo.getStatus(), ModuleInfo.getDescription(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 删除模块信息
	 */
	public static boolean deleteModuleInfo(String ID) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstSql.add("update moduleinfo set isdelete=1,deleteTime='".concat(Common.GetDateTime()).concat("' where id='").concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
			lstSql.add("update moduletypeinfo set isdelete=1,deleteTime='".concat(Common.GetDateTime()).concat("' where tableid='").concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean existsTableName(String TableName, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TableName)) {
			sbCondition.append("TableName='").append(TableName).append("' and ");
		}
		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("ID!=").append(ID).append(" and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduleinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean existsTableNameEN(String TableNameEN, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TableNameEN)) {
			sbCondition.append("TableName_EN='").append(TableNameEN).append("' and ");
		}
		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("ID!=").append(ID).append(" and ");
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduleinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean existsTableNameCH(String TableNameCH, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TableNameCH)) {
			sbCondition.append("TableName_CH='").append(TableNameCH).append("' and ");
		}
		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("ID!=").append(ID).append(" and ");
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduleinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}

	}

	/*
	 * 保存模块字段信息：包括增加、删除、修改操作
	 * 
	 * @TableID 模块id
	 * 
	 * @mapModuleContentInfo 字段名称Map
	 * 
	 * @@Result:100，在删除字段时，数据库中有数据不能删除；
	 * 
	 * @@
	 * 
	 * @@Result:1，执行成功；
	 */
	public static String saveModuleContentInfo(String TableID, Map<String, ModuleContentInfoBean> mapModuleContentInfo) {
		String strResult = "1";
		if (mapModuleContentInfo.size() == 0) {
			return strResult;
		}

		// 判断属性名称是否有重复的，因为名称是否有重复的。如果有重复的直接返回。 --这个不用判断了，Key不会相等

		// 取出模块信息
		// ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		// String strMdleTabName = moduleInfoBean.getTableName();

		// 取得模块的属性值
		Map<String, ModuleContentInfoBean> mapModuleContentTab = getModuleContentInfoByTableID(TableID); // 此时
																											// 该值有可能为null，如果为NULL,证明是创建新表

		// 对比页面的属性值和表中属性值的对比
		Map<String, List<ModuleContentInfoBean>> mapComResult = compareModuleContentInfo(mapModuleContentInfo, mapModuleContentTab);

		// 建表:已验
		if (mapComResult.containsKey("create")) {
			List<ModuleContentInfoBean> lstCreateBean = mapComResult.get("create");
			// 建表结构
			if (createModuleTab(TableID, lstCreateBean)) {
				// 插入字段信息
				if (addModuleContentInfos(lstCreateBean)) {
					return strResult;
				} else {
					if (dropModuleTables(TableID)) {
						return "2";
					} else {
						return "3";
					}
				}
			} else {
				return "4";
			}
		}
		// 表存在 ，增删改操作
		// 判断是否已经有数据存在，这个 是不是放这里，应该好好想想
		boolean isHasData = IsHasData(TableID);
		// 判断是否有删除操作，如果有删除操作，判断表中是否有数据，如果有数据，然后返回false；或者返回100：代表有数据，如果要删除，需要先清除数据后，才能删除字段
		if (isHasData) {
			if (mapComResult.containsKey("droptable")) {
				return "15";
			}
			if (mapComResult.containsKey("delete")) {
				return "5";
			}
			if (mapComResult.containsKey("istnotnull")) {
				return "6";
			}
			if (mapComResult.containsKey("alertabwithcheck")) {
				return "7";
			}
		}

		// 删除表
		if (mapComResult.containsKey("droptable")) {
			if (dropModuleTables(TableID)) {
				if (deleteModuleContentInfo(TableID)) {
					return strResult;
				}
			}
		}

		// 通过比较结果，执行操作,需要判断是否
		List<ModuleContentInfoBean> lstInsert = new ArrayList<ModuleContentInfoBean>();
		if (mapComResult.containsKey("istnotnull")) {
			lstInsert = mapComResult.get("istnotnull");
		}
		if (mapComResult.containsKey("istnull")) {
			lstInsert.addAll(mapComResult.get("istnull"));
		}
		if (lstInsert.size() > 0) {
			if (addTableColumn(TableID, lstInsert)) {
				if (!addModuleContentInfos(lstInsert)) {
					dropTableColumn(TableID, lstInsert);
					strResult = "9"; // 未判断
				}
			} // else {
				// dropTableColumn(TableID, lstInsert);
				// strResult = "10";
			// }
		}
		// 修改
		List<ModuleContentInfoBean> lstUpdate = new ArrayList<ModuleContentInfoBean>();
		boolean bolAlterTab = mapComResult.containsKey("updateall") || mapComResult.containsKey("alertabwithcheck");
		if (mapComResult.containsKey("updateall")) {
			lstUpdate.addAll(mapComResult.get("updateall"));
		}
		// if (mapComResult.containsKey("updatelen")) {
		// lstUpdate.addAll(mapComResult.get("updatelen"));
		// }
		// if (mapComResult.containsKey("updatenotnull")) {
		// lstUpdate.addAll(mapComResult.get("updatenotnull"));
		// }
		if (mapComResult.containsKey("updateonly")) {
			lstUpdate.addAll(mapComResult.get("updateonly"));
		}
		if (mapComResult.containsKey("alertabwithcheck")) {
			lstUpdate.addAll(mapComResult.get("alertabwithcheck"));
		}
		if (lstUpdate.size() > 0) {
			if (bolAlterTab) {
				if (modifyTabColumn(TableID, lstUpdate)) {
					if (!updateModuleContentInfos(TableID, lstUpdate)) {
						revModifyTableStrut(TableID);
						strResult = "11";
					}
				} else {
					strResult = "8";
				}
			} else {
				if (!updateModuleContentInfos(TableID, lstUpdate)) {
					strResult = "12";
				}
			}
		}
		if (mapComResult.containsKey("delete")) {
			List<ModuleContentInfoBean> lstDelete = mapComResult.get("delete");
			if (dropTableColumn(TableID, lstDelete)) {
				if (!delModuleContentInfos(TableID, lstDelete)) {
					revModifyTableStrut(TableID);
					strResult = "13";
				}
			} else {
				strResult = "14";
			}
		}
		return strResult;
	}

	/*
	 * 根据TableID取出ModuleContentInfo表中的对应的字段，然后进行对比
	 */
	private static Map<String, ModuleContentInfoBean> getModuleContentInfoByTableID(String TableID) {
		List<Map<String, Object>> lstModuleContentInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleContentInfo = dbHelper.ExecuteQuery("FieldName,FieldName_EN,FieldName_CH,FieldType,FieldLength,IsAutoIncrement,IsPrimKey,IsIndex,IsNull,Time", "modulecontentinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleContentInfo == null) {
			return null;
		}
		Map<String, ModuleContentInfoBean> mapModuleContentInfo = new LinkedHashMap<String, ModuleContentInfoBean>();
		for (Map<String, Object> map : lstModuleContentInfo) {
			ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean(map);
			mapModuleContentInfo.put(String.valueOf(map.get("fieldname")).toLowerCase(), moduleContentInfo);
		}
		return mapModuleContentInfo;
	}

	/*
	 * 判断对应的表中是否TableID对应的表中是否有数据,如果有数据，不允许删除字段名称，基础表未加判断
	 */
	public static boolean IsHasData(String TableID) {
		ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		String strModuleName = moduleInfoBean.getTableName();

		if (!existsTable(strModuleName)) {
			return false;
		}

		DBHelper dbHelper = null;
		String strModuleTypeTabName = null;
		int iCount = 0;
		try {
			dbHelper = DBHelper.GetInstance();
//			iCount = dbHelper.GetCount(strModuleName, "");
			Object[] arrParam = new Object[3];
			arrParam[0] = strModuleName;
			arrParam[1] = "";
			arrParam[2] = "cloudtable";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
			iCount = Integer.valueOf(list.get(0).get("totalcount").toString());
			if (iCount > 0) {
				return true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		boolean bolRet = false;
		List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);
		if (lstModuleType != null) {
			Iterator<Map<String, Object>> iterator = lstModuleType.iterator();
			Map<String, Object> iMap = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				iMap = iterator.next();
				strModuleTypeTabName = strModuleName.concat("_").concat(iMap.get("typename").toString());
				try {
					dbHelper = DBHelper.GetInstance();
//					iCount = dbHelper.GetCount(strModuleTypeTabName, "");
					Object[] arrParam = new Object[3];
					arrParam[0] = strModuleTypeTabName;
					arrParam[1] = null;
					arrParam[2] = "cloudtable";
					List<Map<String, Object>> list = null;
					list = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
					iCount = Integer.valueOf(list.get(0).get("totalcount").toString());
					if (iCount > 0) {
						bolRet = true;
						break;
					}
				} catch (Exception e) {
					Logger.WriteException(e);
				}
			}
		}
		return bolRet;
	}

	/*
	 * 比较字段 需要修改 Object--》ModuleContentInfoBean
	 */
	private static Map<String, List<ModuleContentInfoBean>> compareModuleContentInfo(Map<String, ModuleContentInfoBean> mapModuleContentInfo, Map<String, ModuleContentInfoBean> TTTmapModuleContentTab) {

		Map<String, List<ModuleContentInfoBean>> mapComResult = new LinkedHashMap<String, List<ModuleContentInfoBean>>();

		List<ModuleContentInfoBean> lstCreate = new ArrayList<ModuleContentInfoBean>();

		// 没有字段信息，证明此处为新建表，直接返回
		if (TTTmapModuleContentTab == null || TTTmapModuleContentTab.size() == 0) {
			for (Entry<String, ModuleContentInfoBean> entry : mapModuleContentInfo.entrySet()) {
				lstCreate.add(entry.getValue());
			}
			mapComResult.put("create", lstCreate);
			return mapComResult;
		}
		// 删除表的情况
		if (mapModuleContentInfo.size() == 1 && TTTmapModuleContentTab.size() > 0) {
			mapComResult.put("droptable", null);
			return mapComResult;
		}

		List<ModuleContentInfoBean> lstDelete = new ArrayList<ModuleContentInfoBean>();
		List<ModuleContentInfoBean> lstIstNotNull = new ArrayList<ModuleContentInfoBean>(); // 插入不允许为空的字段，重点判断
		List<ModuleContentInfoBean> lstIstNull = new ArrayList<ModuleContentInfoBean>(); // 插入为空的字段
		// List<ModuleContentInfoBean> lstUpdateNotNull = new
		// ArrayList<ModuleContentInfoBean>(); // 从允许为空，改为不允许为空
		// List<ModuleContentInfoBean> lstUpdateLenth = new
		// ArrayList<ModuleContentInfoBean>();

		// List<ModuleContentInfoBean> lstUpdateType = new
		// ArrayList<ModuleContentInfoBean>();

		// List<ModuleContentInfoBean> lstUpdateLenNull = new
		// ArrayList<ModuleContentInfoBean>();
		List<ModuleContentInfoBean> lstUpdateALL = new ArrayList<ModuleContentInfoBean>();
		List<ModuleContentInfoBean> lstUpdateALLWithCheck = new ArrayList<ModuleContentInfoBean>();
		List<ModuleContentInfoBean> lstUpdateOnly = new ArrayList<ModuleContentInfoBean>();

		List<String> lstKey = new ArrayList<String>(); // 放置表中的key，此处Key值为字段信息表已经存在的值
		for (Entry<String, ModuleContentInfoBean> entry : TTTmapModuleContentTab.entrySet()) {
			lstKey.add(entry.getKey());
		}

		for (Entry<String, ModuleContentInfoBean> entry : mapModuleContentInfo.entrySet()) {
			if (TTTmapModuleContentTab.containsKey(entry.getKey())) { // 更新
				lstKey.remove(entry.getKey());
				// if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 0) {
				// continue;
				// }else{
				int iCompare = compareMap(entry.getValue(), TTTmapModuleContentTab.get(entry.getKey()));
				if (iCompare == 0) {
					continue;
				} else if (iCompare == 1) {
					lstUpdateOnly.add(entry.getValue());
				} else if (iCompare == 2) {
					lstUpdateALL.add(entry.getValue());
				} else if (iCompare == 3) {
					lstUpdateALLWithCheck.add(entry.getValue());
				}
				// }
				// } else if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 1) { //
				// 只修改字段表中数据
				// lstUpdateOnly.add(entry.getValue());
				// } else if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 2) {
				// //修改数据和表结构但是不需要判断是否有数据
				// lstUpdateALL.add(entry.getValue());
				// } else if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 4) {
				// lstUpdateLenth.add(entry.getValue());
				// } else if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 5) {
				// lstUpdateNotNull.add(entry.getValue());
				// } else if (compareMap(entry.getValue(),
				// TTTmapModuleContentTab.get(entry.getKey())) == 6) {
				// lstUpdateType.add(entry.getValue());
				// }
			} else { // 新增加数据
				if (entry.getValue().isNull()) {
					lstIstNull.add(entry.getValue());
				} else {
					lstIstNotNull.add(entry.getValue());
				}
			}
		}

		if (lstKey.size() > 0) { // 删除的数据
			for (String str : lstKey) {
				lstDelete.add(TTTmapModuleContentTab.get(str));
			}
		}

		if (lstIstNull.size() > 0) {
			mapComResult.put("istnull", lstIstNull);
		}
		if (lstIstNotNull.size() > 0) {
			mapComResult.put("istnotnull", lstIstNotNull);
		}

		if (lstDelete.size() > 0) {
			mapComResult.put("delete", lstDelete);
		}

		if (lstUpdateOnly.size() > 0) {
			mapComResult.put("updateonly", lstUpdateOnly);
		}
		if (lstUpdateALL.size() > 0) {
			mapComResult.put("updateall", lstUpdateALL);
		}
		if (lstUpdateALLWithCheck.size() > 0) {
			mapComResult.put("alertabwithcheck", lstUpdateALLWithCheck);
		}

		// // if (lstUpdateLenNull.size() > 0) {
		// // mapComResult.put("updatelennull", lstUpdateLenNull);
		// // }
		// if (lstUpdateLenth.size() > 0) {
		// mapComResult.put("updatelen", lstUpdateLenth);
		// }
		// if (lstUpdateNotNull.size() > 0) {
		// mapComResult.put("updatenotnull", lstUpdateNotNull);
		// }
		// if (lstUpdateType.size() > 0) {
		// mapComResult.put("updatetype", lstUpdateType);
		// }
		return mapComResult;
	}

	/*
	 * @@Result int:0 ，没有更新；int：1，只需要更新数据；int：2，表结构需要更新，数据需要更新;int:3.为长度变小了
	 * 是不是需要比较时间或者id呢？这个得想想
	 */
	private static int compareMap(ModuleContentInfoBean newModuleContentInfo, ModuleContentInfoBean oldModuleContentInfo) {
		int iResult = 0;
		int iOnlyData = 0;
		int iFiledLength = 0;
		int iNull = 0;
		int iType = 0;
		if (oldModuleContentInfo.getFieldType().equals(newModuleContentInfo.getFieldType())) {
			if (oldModuleContentInfo.getFieldLength() != newModuleContentInfo.getFieldLength()) {
				if (oldModuleContentInfo.getFieldLength() > newModuleContentInfo.getFieldLength()) { // 长度变小了
					iFiledLength = 1; // 长度变小了，需要判断数据表是否为空，需要修改表结构
				} else {
					iResult = 2; // 变大了，只需要需该表结构
				}
			}
		} else {
			iType = 1; // 类型换了，需要判断数据表是否为空，需要修改表结构
		}
		if (newModuleContentInfo.isIndex() != oldModuleContentInfo.isIndex()) {
			iResult = 2; // 只需要需该表结构
		}
		if (newModuleContentInfo.isNull() != oldModuleContentInfo.isNull()) {
			if (!newModuleContentInfo.isNull()) {
				iNull = 1; // 需要判断数据表是否为空，需要修改表结构
			} else {
				iResult = 2; // 变大了，只需要需该表结构
			}
		}
		if (!newModuleContentInfo.getFieldName_CH().equals(oldModuleContentInfo.getFieldName_CH()) || !newModuleContentInfo.getFieldName_EN().equals(oldModuleContentInfo.getFieldName_EN()) || (newModuleContentInfo.isPrimKey() != oldModuleContentInfo.isPrimKey())) {
			iOnlyData = 1; // 修改数据
		}

		if (iType > 0 || iFiledLength > 0 || iNull > 0) { // 6代表有数据类型改变
			iResult = 3;
			return iResult;
		}

		if (iResult > 0) {
			return iResult;
		}
		return iOnlyData;

		// if (iFiledLength > 0) {
		// iResult = 4; // 4代表有长度变小
		// return iResult;
		// }
		// if (iNull > 0) {
		// iResult = 5; // 5代表有长度变小
		// return iResult;
		// }
		// if (iResult == 2) {
		// return iResult;
		// } else {
		// return iOnlyData;
		// }
	}

	public static boolean existsTable(String TableName) {
		List<Map<String, Object>> lstResult = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("TABLE_NAME", "`INFORMATION_SCHEMA`.`TABLES`", "`TABLE_SCHEMA`='".concat("mdlmngr").concat("' and `TABLE_NAME`='").concat(TableName).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstResult == null) {
			return false;
		} else {
			return true;
		}
	}

	/*
	 * 创建基础模块信息表 需要修改
	 */
	public static boolean createModuleTab(String TableID, List<ModuleContentInfoBean> lstTabFileds) {
		boolean bolRet = true;

		ModuleInfoBean moduleInfo = getModuleInfo(TableID);
		String strModuleName = moduleInfo.getTableName();

		StringBuilder sbSql = new StringBuilder();
		List<String> lstIndex = new ArrayList<String>();
		sbSql.append(" CREATE TABLE `").append(strModuleName).append("` (").append("\r\n");
		sbSql.append(" `ID` int(11) NOT NULL AUTO_INCREMENT,\r\n");
		ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean();
		Iterator<ModuleContentInfoBean> iterator = lstTabFileds.iterator();
		while (iterator.hasNext()) {
			moduleContentInfo = iterator.next();
			String strFiledName = moduleContentInfo.getFieldName().toLowerCase();
			if ("id".equals(strFiledName) || "time".equals(strFiledName)) {
				continue;
			}
			if ("varchar".equals(moduleContentInfo.getFieldType().toLowerCase())) {
				sbSql.append(" `").append(strFiledName).append("` varchar(").append(String.valueOf(moduleContentInfo.getFieldLength())).append(") ");
				if (moduleContentInfo.isNull()) {
					sbSql.append(" DEFAULT NULL").append(",\r\n");
				} else {
					sbSql.append(" NOT NULL").append(",\r\n");
				}
			} else if ("int".equals(moduleContentInfo.getFieldType().toLowerCase())) {
				sbSql.append(" `").append(strFiledName).append("` int(").append(String.valueOf(moduleContentInfo.getFieldLength())).append(") ");
				if (moduleContentInfo.isNull()) {
					sbSql.append(" DEFAULT NULL").append(",\r\n");
				} else {
					sbSql.append(" NOT NULL").append(",\r\n");
				}
			} else if ("datetime".equals(moduleContentInfo.getFieldType().toLowerCase())) {
				sbSql.append(" `").append(strFiledName).append("` datetime ");
				if (moduleContentInfo.isNull()) {
					sbSql.append(" DEFAULT NULL").append(",\r\n");
				} else {
					sbSql.append(" NOT NULL").append(",\r\n");
				}
			} else if ("text".equals(moduleContentInfo.getFieldType().toLowerCase())) {
				sbSql.append(" `").append(strFiledName).append("` text ");
				if (moduleContentInfo.isNull()) {
					sbSql.append(" DEFAULT NULL").append(",\r\n");
				} else {
					sbSql.append(" NOT NULL").append(",\r\n");
				}
			}

			if (moduleContentInfo.isIndex()) {
				lstIndex.add(strFiledName);
			}
		}
		sbSql.append("`time` datetime NOT NULL,\r\n");
		sbSql.append(" PRIMARY KEY (`ID`),\r\n");
		if (lstIndex.size() > 0) {
			for (String strIxField : lstIndex) {
				sbSql.append(" KEY `IX_").append(strIxField).append("` (`").append(strIxField).append("`),\r\n");
			}
		}
		sbSql.delete(sbSql.length() - 3, sbSql.length());
		sbSql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8");
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			dbHelper.ExecuteSql(sbSql.toString());
			createSeqTable(strModuleName);
		} catch (Exception e) {
			bolRet = false;
			Logger.WriteException(e);
		}
		if (bolRet) {
			List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);
			if (lstModuleType != null) {
				Iterator<Map<String, Object>> iModuleType = lstModuleType.iterator();
				Map<String, Object> iMap = new HashMap<String, Object>();
				while (iModuleType.hasNext()) {
					iMap = iModuleType.next();
					String ModuleTypeName = strModuleName.concat("_").concat(String.valueOf(iMap.get("typename")));
					bolRet = createModuleTypeTable(strModuleName, ModuleTypeName);
					createSeqTable(ModuleTypeName);
				}
			}
			lstModuleType = null;
		} else {
			return bolRet;
		}
		sbSql = null;
		return bolRet;
	}

	/*
	 * 删除创建的模块信息表
	 */
	private static boolean dropModuleTables(String TableID) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();

		ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		String strMdleTabName = moduleInfoBean.getTableName();
		lstSql.add("drop table ".concat(strMdleTabName));

		List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);
		if (lstModuleType != null) {
			Iterator<Map<String, Object>> iterator = lstModuleType.iterator();
			Map<String, Object> iMap = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				iMap = iterator.next();
				lstSql.add("drop table ".concat(strMdleTabName.concat("_").concat(String.valueOf(iMap.get("typename")))));
				for(int i=1;i<10;i++){
					lstSql.add("drop table ".concat(strMdleTabName.concat("_").concat(String.valueOf(iMap.get("typename")))).concat(String.valueOf(i)));
				}
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean deleteModuleContentInfo(String TableID) {
		boolean bResult = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete("modulecontentinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}

	private static boolean addTableColumn(String TableID, List<ModuleContentInfoBean> LstColumn) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();

		ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		String strMdleTabName = moduleInfoBean.getTableName();

		List<String> lstTempSql = addTabColSql(strMdleTabName, LstColumn);
		if (lstTempSql.size() > 0) {
			lstSql.addAll(lstTempSql);
		}
		List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);
		if (lstModuleType != null) {
			Iterator<Map<String, Object>> iterator = lstModuleType.iterator();
			Map<String, Object> iMap = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				iMap = iterator.next();
				lstTempSql = addTabColSql(strMdleTabName.concat("_").concat(String.valueOf(iMap.get("typename"))), LstColumn);
				if (lstTempSql.size() > 0) {
					lstSql.addAll(lstTempSql);
				}
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 知道List<ModuleContentInfoBean>
	 * lstModuleContentInfo，里面包括修改的字段和新添加的字段,没有判断是否加括号（）如time类型
	 */
	private static List<String> addTabColSql(String TableName, List<ModuleContentInfoBean> LstColumn) {
		List<String> lstSql = new ArrayList<String>();

		for (ModuleContentInfoBean moduleContentInfo : LstColumn) {
			String strFieldName = moduleContentInfo.getFieldName().toLowerCase();
			if ("id".equals(strFieldName)) {
				continue;
			}
			// lstSql.add("alter table ".concat(TableName).concat(" add COLUMN `").concat(strFieldName).concat("` varchar").concat("(").concat(String.valueOf(moduleContentInfo.getFieldLength())).concat(") ").concat(moduleContentInfo.isNull()
			// ? " DEFAULT NULL" : " NOT NULL"));
			lstSql.add(addNewTabColumn(TableName, strFieldName, moduleContentInfo.getFieldType(), moduleContentInfo.getFieldLength(), moduleContentInfo.isNull()));
			for(int i=1;i<10;i++){
				lstSql.add(addNewTabColumn(TableName+i, strFieldName, moduleContentInfo.getFieldType(), moduleContentInfo.getFieldLength(), moduleContentInfo.isNull()));			}
			if (moduleContentInfo.isIndex()) {
				lstSql.add("alter table ".concat(TableName).concat(" ADD INDEX IX_").concat(strFieldName).concat("(").concat(strFieldName).concat(")"));
				for(int i=1;i<10;i++){
					lstSql.add("alter table ".concat(TableName).concat(String.valueOf(i)).concat(" ADD INDEX IX_").concat(strFieldName).concat("(").concat(strFieldName).concat(")"));
				}
			}
		}
		return lstSql;
	}

	private static boolean dropTableColumn(String TableID, List<ModuleContentInfoBean> LstColumn) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();

		ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		String strMdleTabName = moduleInfoBean.getTableName();
		List<String> lstTempSql = dropTabColSql(strMdleTabName, LstColumn);
		if (lstTempSql.size() > 0) {
			lstSql.addAll(lstTempSql);
		}
		List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);	
		if (lstModuleType != null) {
			Iterator<Map<String, Object>> iterator = lstModuleType.iterator();
			Map<String, Object> iMap = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				iMap = iterator.next();
				lstTempSql = dropTabColSql(strMdleTabName.concat("_").concat(String.valueOf(iMap.get("typename"))), LstColumn);
				if (lstTempSql.size() > 0) {
					lstSql.addAll(lstTempSql);
				}
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static List<String> dropTabColSql(String TableName, List<ModuleContentInfoBean> LstColumn) {
		List<String> lstSql = new ArrayList<String>();
		for (ModuleContentInfoBean moduleContentInfo : LstColumn) {
			String strFieldName = moduleContentInfo.getFieldName().toLowerCase();
			lstSql.add("alter table `".concat(TableName).concat("` drop column ").concat(strFieldName));
			for(int i=1;i<10;i++){
				lstSql.add("alter table `".concat(TableName).concat(String.valueOf(i)).concat("` drop column ").concat(strFieldName));
			}
		}
		return lstSql;
	}

	public static List<String> getTabIndexColumn(String TableName) {
		List<String> lstIndex = new ArrayList<String>();
		List<Map<String, Object>> lstResult = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstResult = dbHelper.ExecuteQuery("show INDEX from ".concat(TableName));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstResult == null) {
			return null;
		}
		for (Map<String, Object> map : lstResult) {
			lstIndex.add(String.valueOf(map.get("column_name")));
		}
		return lstIndex;
	}

	private static boolean modifyTabColumn(String TableID, List<ModuleContentInfoBean> LstColumn) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();

		ModuleInfoBean moduleInfoBean = getModuleInfo(TableID);
		String strMdleTabName = moduleInfoBean.getTableName();

		List<String> lstTempSql = modifyTabColSql(strMdleTabName, LstColumn);
		if (lstTempSql.size() > 0) {
			lstSql.addAll(lstTempSql);
		}
		List<Map<String, Object>> lstModuleType = getModuleTypeList(TableID);
		if (lstModuleType != null) {
			Iterator<Map<String, Object>> iterator = lstModuleType.iterator();
			Map<String, Object> iMap = new HashMap<String, Object>();
			while (iterator.hasNext()) {
				iMap = iterator.next();
				lstTempSql = modifyTabColSql(strMdleTabName.concat("_").concat(String.valueOf(iMap.get("typename"))), LstColumn);
				if (lstTempSql.size() > 0) {
					lstSql.addAll(lstTempSql);
				}
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static List<String> modifyTabColSql(String TableName, List<ModuleContentInfoBean> LstColumn) {
		List<String> lstSql = new ArrayList<String>();
		List<String> lstIndex = getTabIndexColumn(TableName);
		for (ModuleContentInfoBean moduleContentInfo : LstColumn) {
			String strFieldName = moduleContentInfo.getFieldName().toLowerCase();
			if ("id".equals(strFieldName)) {
				continue;
			}
			// lstSql.add("alter table ".concat(TableName).concat(" modify ").concat(strFieldName).concat(" varchar").concat("(").concat(String.valueOf(moduleContentInfo.getFieldLength())).concat(") ").concat(moduleContentInfo.isNull()
			// ? " DEFAULT NULL" : " NOT NULL"));
			lstSql.add(modifyOldTabColumn(TableName, strFieldName, moduleContentInfo.getFieldType(), moduleContentInfo.getFieldLength(), moduleContentInfo.isNull()));
			for(int i=1;i<10;i++){
				lstSql.add(modifyOldTabColumn(TableName.concat(String.valueOf(i)), strFieldName, moduleContentInfo.getFieldType(), moduleContentInfo.getFieldLength(), moduleContentInfo.isNull()));
			}
			if (moduleContentInfo.isIndex()) {
				if (!lstIndex.contains(strFieldName)) {
					lstSql.add("alter table ".concat(TableName).concat(" ADD INDEX IX_").concat(strFieldName).concat("(").concat(strFieldName).concat(")"));
					for(int i=1;i<10;i++){
						lstSql.add("alter table ".concat(TableName).concat(String.valueOf(i)).concat(" ADD INDEX IX_").concat(strFieldName).concat("(").concat(strFieldName).concat(")"));
					}
				}
			} else {
				if (lstIndex.contains(strFieldName)) {
					lstSql.add("alter table ".concat(TableName).concat(" DROP INDEX IX_").concat(strFieldName));
					for(int i=1;i<10;i++){
						lstSql.add("alter table ".concat(TableName).concat(String.valueOf(i)).concat(" DROP INDEX IX_").concat(strFieldName));
					}
				}
			}
		}
		return lstSql;
	}

	/*
	 * 恢复通过更新字段改变的表的结构
	 * 
	 * @TableID 模块id
	 * 
	 * @LstColumn 插入的字段信息
	 */
	private static boolean revModifyTableStrut(String TableID) {
		List<ModuleContentInfoBean> lstTabFileds = getModuleContentInfoByTabID(TableID);
		return modifyTabColumn(TableID, lstTabFileds);

	}

	public static boolean addModuleContentInfo(ModuleContentInfoBean ModuleContentInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("modulecontentinfo", new String[] { "tableid", "fieldname", "fieldname_en", "fieldname_cn", "isautoincrement", "isprimkey", "isindex", "isnull", "time" }, new Object[] { ModuleContentInfo.getTableID(), ModuleContentInfo.getFieldName(), ModuleContentInfo.getFieldName_EN(), ModuleContentInfo.getFieldName_CH(), ModuleContentInfo.isAutoIncrement(), ModuleContentInfo.isPrimKey(), ModuleContentInfo.isIndex(), ModuleContentInfo.isNull(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 添加模块字段信息
	 */
	public static boolean addModuleContentInfos(List<ModuleContentInfoBean> lstModuleContentInfo) {
		boolean bolRet = false;
		List<String> lstSql = new ArrayList<String>();
		for (ModuleContentInfoBean McInfoBean : lstModuleContentInfo) {
			if (!"id".equals(McInfoBean.getFieldName())) {
				lstSql.add(getInsertSql(McInfoBean, "modulecontentinfo"));
			}
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean updateModuleContentInfos(String TableID, List<ModuleContentInfoBean> lstModuleContentInfo) {
		boolean bolRet = false;
		String strTableName = "modulecontentinfo";
		List<String> lstSql = new ArrayList<String>();
		for (ModuleContentInfoBean McInfoBean : lstModuleContentInfo) {
			lstSql.add(getUpdateSql(McInfoBean, strTableName, "fieldname='".concat(McInfoBean.getFieldName()).concat("' and tableid=").concat(String.valueOf(TableID))));
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	private static boolean delModuleContentInfos(String TableID, List<ModuleContentInfoBean> lstModuleContentInfo) {
		boolean bolRet = false;
		String strTableName = "modulecontentinfo";
		List<String> lstSql = new ArrayList<String>();
		for (ModuleContentInfoBean McInfoBean : lstModuleContentInfo) {
			lstSql.add("delete from ".concat(strTableName).concat(" where fieldname='").concat(McInfoBean.getFieldName()).concat("' and tableid=").concat(String.valueOf(TableID)));
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql(lstSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static ModuleContentInfoBean getModuleContentInfo(String ID) {
		List<Map<String, Object>> lstModuleContentInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleContentInfo = dbHelper.ExecuteQuery("TableID,FieldName,FieldName_EN,FieldName_CH,FieldLength,IsAutoIncrement,IsPrimKey,IsIndex,IsNull,Time", "modulecontentinfo", "ID='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleContentInfo == null) {
			return null;
		}
		ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean(lstModuleContentInfo.get(0));
		lstModuleContentInfo = null;
		return moduleContentInfo;
	}

	public static int getModuleContentInfoCountByTabID(String TableID) {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("modulecontentinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	/*
	 * 通过TableID获取模块的所有字段信息
	 * 
	 * @Result List<Map<String, Object>>
	 */
	public static List<ModuleContentInfoBean> getModuleContentInfoByTabID(String TableID) {
		List<Map<String, Object>> lstModuleContentInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleContentInfo = dbHelper.ExecuteQuery("TableID,FieldName,FieldName_EN,FieldName_CH,FieldType,FieldLength,IsAutoIncrement,IsPrimKey,IsIndex,IsNull,IsDisplay,Time", "modulecontentinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleContentInfo == null) {
			return null;
		}
		List<ModuleContentInfoBean> lstModuleContentInfoBean = new ArrayList<ModuleContentInfoBean>();
		for (Map<String, Object> mapModuleContentInfo : lstModuleContentInfo) {
			ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean(mapModuleContentInfo);
			lstModuleContentInfoBean.add(moduleContentInfo);
		}
		return lstModuleContentInfoBean;

	}

	public static List<ModuleContentInfoBean> getDisplayModuleContentByTabID(String TableID) {
		List<Map<String, Object>> lstModuleContentInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleContentInfo = dbHelper.ExecuteQuery("TableID,FieldName,FieldName_EN,FieldName_CH,FieldLength,IsAutoIncrement,IsPrimKey,IsIndex,IsNull,IsDisplay,Time", "modulecontentinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("' and IsDisplay=1"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleContentInfo == null) {
			return null;
		}
		List<ModuleContentInfoBean> lstModuleContentInfoBean = new ArrayList<ModuleContentInfoBean>();
		for (Map<String, Object> mapModuleContentInfo : lstModuleContentInfo) {
			ModuleContentInfoBean moduleContentInfo = new ModuleContentInfoBean(mapModuleContentInfo);
			lstModuleContentInfoBean.add(moduleContentInfo);
		}
		return lstModuleContentInfoBean;

	}

	/*
	 * 获取模块类别信息
	 */
	public static ModuleTypeInfoBean getModuleTypeInfo(String ID) {
		List<Map<String, Object>> lstModuleType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleType = dbHelper.ExecuteQuery("TableID,TypeName,TypeName_EN,TypeName_CH,Status,IsDelete,DeleteTime,Description,Time", "moduletypeinfo", "ID='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstModuleType == null) {
			return null;
		}
		ModuleTypeInfoBean moduleTypeInfo = new ModuleTypeInfoBean(lstModuleType.get(0));
		return moduleTypeInfo;
	}

	public static boolean existsModuleTypeName(String TypeName, String TableID, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TypeName)) {
			sbCondition.append("typename='").append(TypeName).append("' and ");
		}
		if (!Common.IsNullOrEmpty(TableID)) {
			sbCondition.append("tableid=").append(TableID).append(" and ");
		}

		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("id!=").append(ID).append(" and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduletypeinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean existsModuleTypeNameEN(String TypeNameEN, String TableID, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TypeNameEN)) {
			sbCondition.append("typename_en='").append(TypeNameEN).append("' and ");
		}
		if (!Common.IsNullOrEmpty(TableID)) {
			sbCondition.append("tableid=").append(TableID).append(" and ");
		}

		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("id!=").append(ID).append(" and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduletypeinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean existsModuleTypeNameCH(String TypeNameCH, String TableID, String ID) {
		int iCount = 0;
		StringBuilder sbCondition = new StringBuilder();
		if (!Common.IsNullOrEmpty(TypeNameCH)) {
			sbCondition.append("typename_en='").append(TypeNameCH).append("' and ");
		}
		if (!Common.IsNullOrEmpty(TableID)) {
			sbCondition.append("tableid=").append(TableID).append(" and ");
		}

		if (!Common.IsNullOrEmpty(ID)) {
			sbCondition.append("id!=").append(ID).append(" and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduletypeinfo", sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		sbCondition = null;
		if (iCount > 0) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * 获取模块类别列表
	 */
	public static List<Map<String, Object>> getModuleTypeList(String TableID) {
		List<Map<String, Object>> lstModuleType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleType = dbHelper.ExecuteQuery("ID,TypeName,TypeName_EN,TypeName_CH,Status,IsDelete,DeleteTime,Time", "moduletypeinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstModuleType;
	}

	public static int getDeletedModuleTypeCount() {
		int iCount = 0;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			iCount = dbHelper.GetCount("moduletypeinfo", "IsDelete=1");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	/*
	 * 获取模块类别列表
	 */
	public static List<Map<String, Object>> getModuleTypeList(String TableID, String IsDelete) {
		List<Map<String, Object>> lstModuleType = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstModuleType = dbHelper.ExecuteQuery("ID,TypeName,TypeName_EN,TypeName_CH,Status,IsDelete,DeleteTime,Time", "moduletypeinfo", "TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("' and IsDelete=").concat(IsDelete));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstModuleType;
	}

	/*
	 * 添加模块类别信息
	 */
	public static boolean addModuleTypeInfo(ModuleTypeInfoBean ModuleTypeInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Insert("moduletypeinfo", new String[] { "tableid", "typename", "typename_en", "typename_ch", "status", "isdelete", "description", "time" }, new Object[] { ModuleTypeInfo.getTableId(), ModuleTypeInfo.getTypeName(), ModuleTypeInfo.getTypeName_EN(), ModuleTypeInfo.getTypeName_CH(), ModuleTypeInfo.getStatus(), ModuleTypeInfo.getIsDelete() ? "1" : "0", ModuleTypeInfo.getDescription(), Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 修改模块类别信息
	 */
	public static boolean updateModuleTypeInfo(ModuleTypeInfoBean ModuleTypeInfo) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("moduletypeinfo", "ID=".concat(String.valueOf(ModuleTypeInfo.getId())), new String[] { "tableid", "typename", "typename_en", "typename_ch", "status", "description", "isdelete", "time" }, new Object[] { ModuleTypeInfo.getTableId(), ModuleTypeInfo.getTypeName(), ModuleTypeInfo.getTypeName_EN(), ModuleTypeInfo.getTypeName_CH(), ModuleTypeInfo.getStatus(), ModuleTypeInfo.getDescription(), ModuleTypeInfo.getIsDelete() ? "1" : "0", Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean existsModuleTypeData(String TypeID, String TableID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			String tablename = getTableName(Integer.parseInt(TableID), Integer.parseInt(TypeID));
			int iCount = 0;
			dbHelper = DBHelper.GetInstance();
			Object[] arrParam = new Object[3];
			arrParam[0] = tablename;
			arrParam[1] = "";
			arrParam[2] = "cloudtable";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
			iCount = Integer.valueOf(list.get(0).get("totalcount").toString());
			if (iCount> 0) {
				bolRet = true;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	/*
	 * 删除模块类别信息
	 */
	public static boolean deleteModuleTypeInfo(String ID) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update("moduletypeinfo", "ID='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'"), new String[] { "isDelete", "deleteTime" }, new String[] { "1", Common.GetDateTime() });
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean recycleModuleInfo(String ID) {
		boolean bolRet = false;

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			String strSql = "update moduleinfo set isdelete=0,deleteTime=null where id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'");
			bolRet = dbHelper.ExecuteSql(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;

	}

	public static boolean recycleModuleTypeInfo(String ID) {
		boolean bolRet = false;

		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			String strSql = "update moduletypeinfo set isdelete=0,deleteTime=null where id='".concat(dbHelper.FilterSpecialCharacter(ID)).concat("'");
			bolRet = dbHelper.ExecuteSql(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;

	}

	/*
	 * 建模块类别表
	 */
	public static boolean createModuleTypeTable(String ModuleName, String ModuleTypeName) {
		boolean bolRet = true;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			dbHelper.ExecuteSql("create table if not exists ".concat(ModuleTypeName).concat(" like ").concat(ModuleName));
			createSeqTable(ModuleTypeName);
		} catch (Exception e) {
			Logger.WriteException(e);
			bolRet = false;
		}
		return bolRet;
	}

	/*
	 * 生成Insert语句
	 */
	public static String getInsertSql(Object obj, String TabName) {
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbVals = new StringBuilder();
		Field[] fields = obj.getClass().getDeclaredFields();
		sbSql.append("insert into ").append(TabName).append("(");
		Object objVal = null;
		for (int i = 0; i < fields.length; i++) {
			String strFieldName = fields[i].getName();
			if ("id".equals(strFieldName.toLowerCase())) {
				continue;
			}
			try {
				fields[i].setAccessible(true);
				objVal = fields[i].get(obj);
			} catch (Exception e) {
				Logger.WriteException(e);
			}
			if (objVal == null) {
				continue;
			}
			sbFields.append(strFieldName).append(",");
			if (objVal instanceof String) {
				sbVals.append("'").append(objVal).append("',");
			} else if (objVal instanceof Boolean) {
				sbVals.append((Boolean) objVal ? 1 : 0).append(",");
			} else {
				sbVals.append(objVal).append(",");
			}
		}
		if (sbFields.length() == 0) {
			return null;
		} else {
			sbFields.delete(sbFields.length() - 1, sbFields.length());
			sbVals.delete(sbVals.length() - 1, sbVals.length());
			sbSql.append(sbFields.toString()).append(") values(").append(sbVals.toString()).append(")");
		}
		sbFields = null;
		sbVals = null;
		return sbSql.toString();
	}

	/*
	 * 生成Update语句
	 */
	public static String getUpdateSql(Object obj, String TabName, String Condition) {
		StringBuilder sbSql = new StringBuilder();
		Field[] fields = obj.getClass().getDeclaredFields();
		sbSql.append("update ").append(TabName).append(" set ");
		Object objVal = null;
		for (int i = 0; i < fields.length; i++) {
			String strFieldName = fields[i].getName();
			if ("id".equals(strFieldName.toLowerCase())) {
				continue;
			}
			try {
				fields[i].setAccessible(true);
				objVal = fields[i].get(obj);
			} catch (Exception e) {
				Logger.WriteException(e);
			}
			sbSql.append(strFieldName).append("=");
			if (objVal instanceof String) {
				sbSql.append("'").append(objVal).append("',");
			} else if (objVal instanceof Boolean) {
				sbSql.append((Boolean) objVal ? 1 : 0).append(",");
			} else {
				sbSql.append(objVal).append(",");
			}
		}
		sbSql.delete(sbSql.length() - 1, sbSql.length());
		if (Condition.length() > 0) {
			sbSql.append(" where ").append(Condition);
		}
		return sbSql.toString();
	}

	private static boolean dropDataTable(String TableName) {
		boolean bolRet = false;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.ExecuteSql("drop table ".concat(TableName));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bolRet;
	}

	public static boolean checkModuleTable() {
		int iDay = 7;
		String strKeepDays = Common.GetConfig("DBKeepDays");
		if (strKeepDays != null) {
			iDay = Integer.valueOf(strKeepDays);
		}
		DBHelper dbHelper = null;
		String strTime = Common.GetDateTime(-iDay * 86400000);
		List<Map<String, Object>> lstModule = null;
		List<Map<String, Object>> lstType = null;
		List<String> lstContengSql = new ArrayList<String>();
		try {
			dbHelper = DBHelper.GetInstance();
			lstModule = dbHelper.ExecuteQuery("id,tablename,deletetime", "moduleinfo", "isdelete=1 and deletetime < '".concat(strTime).concat("'"));
			lstType = dbHelper.ExecuteQuery("select CONCAT(tablename,'_',TypeName) as tablename,taba.deletetime from moduletypeinfo taba left JOIN moduleinfo tabb on taba.TableID=tabb.ID where taba.IsDelete=1 and taba.deletetime < '".concat(strTime).concat("'"));
			dbHelper.Delete("moduleinfo", "isdelete=1 and deletetime < '".concat(strTime).concat("'"));
			dbHelper.Delete("moduletypeinfo", "isdelete=1 and deletetime < '".concat(strTime).concat("'"));
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		}

		if (lstModule == null && lstType == null) {
			return true;
		}
		List<String> lstTable = new ArrayList<String>();
		if (lstModule != null) {
			for (Map<String, Object> map : lstModule) {
				lstTable.add((String) map.get("tablename"));
				lstContengSql.add("delete from modulecontentinfo where tableid=".concat(String.valueOf(map.get("id"))));
			}
		}

		if (lstType != null) {
			for (Map<String, Object> map : lstType) {
				lstTable.add((String) map.get("tablename"));
			}
		}
		boolean bolRet = true;
		for (String strTableName : lstTable) {
			if (!dropDataTable(strTableName)) {
				bolRet = false;
			}
		}
		if (lstContengSql.size() > 0) {
			try {
				dbHelper.ExecuteSql(lstContengSql);
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		return bolRet;
	}

	public static int getUserModuleContentCount(String ModuleID, String ModuleTypeID,String username, String StartDate, String EndDate) {
		int iCount = 0;
		String strTableName = "";
		String strModuleName = getTableName(Integer.parseInt(ModuleID));
		if (ModuleTypeID == null) {
			strTableName = strModuleName;
		} else {
			ModuleTypeInfoBean moduleTypeInfo = getModuleTypeInfo(ModuleTypeID);
			strTableName = strModuleName.concat("_").concat(moduleTypeInfo.getTypeName());
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		StringBuilder sbCondition = new StringBuilder();
		if(username !=null){
			sbCondition.append("username like '").append(username).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <'").append(dbHelper.FilterSpecialCharacter(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000))).append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[3];
		arrParam[0] = strTableName;
		arrParam[1] = sbCondition.toString();
		arrParam[2] = "cloudtable";
		List<Map<String, Object>> list = null;
		try {
			list = dbHelper.ExecuteQueryProc("sp_getDataCount", arrParam);
			iCount = Integer.valueOf(list.get(0).get("totalcount").toString());
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return iCount;
	}

	public static List<Map<String, Object>> getUserModuleContentList(String Fields, String ModuleID, String ModuleTypeID,String username, String StartDate, String EndDate, int Start, int Length) {
		List<Map<String, Object>> lstUserModuleContent = null;
		String strTableName = "";
		String strModuleName = getTableName(Integer.parseInt(ModuleID));

		if (ModuleTypeID == null) {
			strTableName = strModuleName;
		} else {
			ModuleTypeInfoBean moduleTypeInfo = getModuleTypeInfo(ModuleTypeID);
			strTableName = strModuleName.concat("_").concat(moduleTypeInfo.getTypeName());
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		StringBuilder sbCondition = new StringBuilder();
		if(username !=null){
			sbCondition.append("username like '").append(username).append("%' and ");
		}
		if (StartDate != null) {
			sbCondition.append("time >='").append(dbHelper.FilterSpecialCharacter(StartDate)).append("' and ");
		}
		if (EndDate != null) {
			sbCondition.append("time <='").append(Common.ConvertToDateTime(EndDate, "yyyy-MM-dd", 24 * 3600 * 1000)).append("' and ");
		}
		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		}
		Object[] arrParam = new Object[6];
		arrParam[0] = strTableName;
		arrParam[1] = Fields;
		arrParam[2] = sbCondition.toString();
		arrParam[3] = "cloudtable";
		arrParam[4] = Start;
		arrParam[5] = Length;
		try {
			lstUserModuleContent = dbHelper.ExecuteQueryProc("sp_getDataRecord", arrParam);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserModuleContent;
	}

	public static List<Map<String, Object>> getUserModuleContentDetail(String Fields, String tablename, String RecordID) {
		List<Map<String, Object>> lstUserModuleContentDetail = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstUserModuleContentDetail = dbHelper.ExecuteQuery(Fields, tablename, "id='".concat(dbHelper.FilterSpecialCharacter(RecordID).concat("'")));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstUserModuleContentDetail;
	}

	public static int getSumFieldLength(String TableID) {
		List<Map<String, Object>> lstSumFieldLen = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			String strSql = "select SUM(FieldLength) as alllen from modulecontentinfo where TableID='".concat(dbHelper.FilterSpecialCharacter(TableID)).concat("'");
			lstSumFieldLen = dbHelper.ExecuteQuery(strSql);
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (lstSumFieldLen == null) {
			return 0;
		}
		return Integer.parseInt(String.valueOf(lstSumFieldLen.get(0).get("alllen")));
	}

/*	public static boolean delModuleMessage(String RecordID, String ModuleID, String ModuleTypeID) {
		boolean bResult = false;
		String strTableName = "";
		String strModuleName = getTableName(Integer.parseInt(ModuleID));

		if (ModuleTypeID == null) {
			strTableName = strModuleName;
		} else {
			ModuleTypeInfoBean moduleTypeInfo = getModuleTypeInfo(ModuleTypeID);
			strTableName = strModuleName.concat("_").concat(moduleTypeInfo.getTypeName());
		}
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			bResult = dbHelper.Delete(strTableName, "id in (".concat(RecordID).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}*/
	public static boolean delModuleMessage(String strRecdId, String tablename) {
		boolean bResult = false;
		List<String> lstSql = new ArrayList<String>();
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if(tablename.equals("undefined")){
				String[] array = strRecdId.split(",");
				if(array.length>0){
					for(int i=0;i<array.length;i++){
						String[] context = array[i].split(":");
						lstSql.add("delete from ".concat(context[1]).concat(" where id= ").concat(context[0]));
					}
				}
				bResult = dbHelper.ExecuteSql(lstSql);
			}else{
				bResult = dbHelper.Delete(tablename, "id =".concat(strRecdId));
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return bResult;
	}
	/*********************************/
	// 需要移到其他文件
	public static List<Map<String, Object>> getOperatorSystem() {
		List<Map<String, Object>> lstOpSystem = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystem = dbHelper.ExecuteQuery("OSNAME", "operationsystem");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystem;
	}

	public static List<Map<String, Object>> getOperatorSystemID() {
		List<Map<String, Object>> lstOpSystemID = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystemID = dbHelper.ExecuteQuery("BASEOSNAME", "operationsystem");
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystemID;
	}

	public static List<Map<String, Object>> getOperatorSystemName(String OSID) {
		List<Map<String, Object>> lstOpSystemName = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			lstOpSystemName = dbHelper.ExecuteQuery("OSNAME", "operationsystem", "id in(".concat(OSID).concat(")"));
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		return lstOpSystemName;
	}

	private static String addNewTabColumn(String TableName, String FieldName, String FieldType, int FieldLen, boolean isNull) {
		switch (FieldType) {
		case "varchar":
			return "alter table ".concat(TableName).concat(" add COLUMN `").concat(FieldName).concat("` varchar").concat("(").concat(String.valueOf(FieldLen)).concat(") ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "int":
			return "alter table ".concat(TableName).concat(" add COLUMN `").concat(FieldName).concat("` int").concat("(").concat(String.valueOf(FieldLen)).concat(") ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "datetime":
			return "alter table ".concat(TableName).concat(" add COLUMN `").concat(FieldName).concat("` datetime ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "text":
			return "alter table ".concat(TableName).concat(" add COLUMN `").concat(FieldName).concat("` text ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		default:
			return "";
		}

	}

	private static String modifyOldTabColumn(String TableName, String FieldName, String FieldType, int FieldLen, boolean isNull) {
		switch (FieldType) {
		case "varchar":
			return "alter table ".concat(TableName).concat(" modify ").concat(FieldName).concat(" varchar").concat("(").concat(String.valueOf(FieldLen)).concat(") ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "int":
			return "alter table ".concat(TableName).concat(" modify `").concat(FieldName).concat("` int").concat("(").concat(String.valueOf(FieldLen)).concat(") ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "datetime":
			return "alter table ".concat(TableName).concat(" modify `").concat(FieldName).concat("` datetime ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		case "text":
			return "alter table ".concat(TableName).concat(" modify `").concat(FieldName).concat("` text ").concat(isNull ? " DEFAULT NULL" : " NOT NULL");
		default:
			return "";
		}

	}
	//创建对应模块的1-9个表
	public static void createSeqTable(String UserName) {
		
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
			for(int i =1;i<10;i++){
				String strTableName = UserName+i;
				if (dbHelper.GetCount("cloudtable", "tablename='".concat(strTableName).concat("'")) == 0) {
					List<String> lstSql = new ArrayList<String>();
					lstSql.add("create table ".concat(strTableName).concat(" like ").concat(UserName));
					lstSql.add("insert into cloudtable(tablename,time) values('".concat(strTableName).concat("','" + Common.GetDateTime() + "')"));
					dbHelper.ExecuteSql(lstSql);
				}
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
	}
	
}


