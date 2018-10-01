package BLL;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import DAL.DBHelper;
import Util.Common;

public class CommonModuleInfoMngr {
	public static String addModuleInfo(Map<String, Object> ModuleInfo) {
		boolean bolRet = false;
		String strRet = null;
		String strTableName = getTableName(ModuleInfo); // 默认表名有效，无效是客户端发的不对

		// 临时策略
		strTableName = getUserTable(strTableName, String.valueOf(ModuleInfo.get("username")));

		Map<String, Object> mapContent = getFieldNameAndValue(ModuleInfo, true);
		if (mapContent == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_COMMONMODULEFIELDS.code));
		}

		int iSize = mapContent.size();
		String[] arrName = new String[iSize];
		Object[] arrValue;
		arrName = mapContent.keySet().toArray(arrName);
		arrValue = mapContent.values().toArray();

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		boolean bolDoAdd = true;
		StringBuilder sbCondition = new StringBuilder();
		String strKey = null;
		String strValue = null;
		List<Map<String, Object>> lstPrimKeys = ModuleMngr.getPrimKeyFieldNames(ModuleInfo.get("name").toString());
		if (lstPrimKeys != null && lstPrimKeys.size() > 0) {
			for (Map<String, Object> mapKey : lstPrimKeys) {
				strKey = mapKey.get("fieldname").toString();
				if (mapContent.containsKey(strKey)) {
					if (mapContent.get(strKey) == null) {
						continue;
					}
					strValue = String.valueOf(mapContent.get(strKey));
					if (Common.IsNullOrEmpty(strValue)) {
						continue;
					}
					if (strValue.startsWith("[") && strValue.endsWith("]")) {
						strValue = strValue.substring(1, strValue.length() - 1).trim();
						sbCondition.append("(");
						for (String strValueTemp : strValue.split(",")) {
							strValueTemp = strValueTemp.trim();
							strValueTemp = strValueTemp.substring(1, strValueTemp.length() - 1).trim();
							sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValueTemp)).append("' or ");
						}
						sbCondition.delete(sbCondition.length() - 3, sbCondition.length());
						sbCondition.append(") and ");
					} else {
						sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValue)).append("' and ");
					}
				}
			}
		}

		try {
			if (sbCondition.length() > 0) {
				sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
				bolDoAdd = dbHelper.GetCount(strTableName, sbCondition.toString()) == 0;
			}
			if (bolDoAdd) {
				bolRet = dbHelper.Insert(strTableName, arrName, arrValue);
			} else {
				strRet = updateModuleInfo(ModuleInfo);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		if (bolRet) {
			strRet = (String) mapContent.get("time");
		} else {
			if (bolDoAdd) {
				strRet = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
			}
			// else {
			// strRet =
			// "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_DUPPRIMKEYS.code));
			// }
		}
		mapContent = null;
		// sbSql = null;
		// sbValues = null;

		return strRet;
	}

	/**
	 * 针对单条数据的更新
	 * @param ModuleInfo
	 * @return
	 */
	public static String updateModuleInfo(Map<String, Object> ModuleInfo) {
		boolean bolRet = false;
		String strRet = null;
		String strTableName = getTableName(ModuleInfo);// 默认表名有效，无效是客户端发的不对

		// 临时策略
		strTableName = getUserTable(strTableName, String.valueOf(ModuleInfo.get("username")));

		Map<String, Object> mapContent = getFieldNameAndValue(ModuleInfo, true);
		if (mapContent == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_COMMONMODULEFIELDS.code));
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}

		StringBuilder sbCondition = new StringBuilder();
		String strKey = null;
		String strValue = null;
		List<Map<String, Object>> lstPrimKeys = ModuleMngr.getPrimKeyFieldNames(ModuleInfo.get("name").toString());
		if (lstPrimKeys == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PRIMKEYSNOTEXIST.code));
		}

		for (Map<String, Object> mapKey : lstPrimKeys) {
			strKey = mapKey.get("fieldname").toString();
			if (mapContent.containsKey(strKey)) {
				if (mapContent.get(strKey) == null) {
					continue;
				}
				strValue = String.valueOf(mapContent.get(strKey));
				if (Common.IsNullOrEmpty(strValue)) {
					continue;
				}
				if (strValue.startsWith("[") && strValue.endsWith("]")) {
					strValue = strValue.substring(1, strValue.length() - 1).trim();
					sbCondition.append("(");
					for (String strValueTemp : strValue.split(",")) {
						strValueTemp = strValueTemp.trim();
						strValueTemp = strValueTemp.substring(1, strValueTemp.length() - 1).trim();
						sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValueTemp)).append("' or ");
					}
					sbCondition.delete(sbCondition.length() - 3, sbCondition.length());
					sbCondition.append(") and ");
				} else {
					sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValue)).append("' and ");
				}
				// sbCondition.append(strKey).append(" ='").append(strValue).append("' and ");
			}
			mapContent.remove(strKey);
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		} else {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PRIMKEYSNOTEXIST.code));
		}

		int iSize = mapContent.size();
		String[] arrName = new String[iSize];
		Object[] arrValue;
		arrName = mapContent.keySet().toArray(arrName);
		arrValue = mapContent.values().toArray();
		try {
			// DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(strTableName, sbCondition.toString(), arrName, arrValue);
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		if (bolRet) {
			strRet = (String) mapContent.get("time");
		} else {
			strRet = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_UPDATEDATA.code));
		}
		mapContent = null;

		return strRet;
	}
	
	/**
	 * 针对一组数据的更新
	 * @param ModuleInfo
	 * @return
	 */
	public static String updatesModuleInfo(Map<String, Object> ModuleInfo) {
		boolean bolRet = false;
		String strRet = null;
		String strTableName = getTableName(ModuleInfo);// 默认表名有效，无效是客户端发的不对

		// 临时策略
		strTableName = getUserTable(strTableName, String.valueOf(ModuleInfo.get("username")));

		Map<String, Object> mapContent = getFieldNameAndValue(ModuleInfo, true);
		if (mapContent == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_COMMONMODULEFIELDS.code));
		}
		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}

		StringBuilder sbCondition = new StringBuilder();
		String strKey = null;
		String strValue = null;
		List<Map<String, Object>> lstPrimKeys = ModuleMngr.getPrimKeyFieldNames(ModuleInfo.get("name").toString());
		if (lstPrimKeys == null) {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PRIMKEYSNOTEXIST.code));
		}

		for (Map<String, Object> mapKey : lstPrimKeys) {
			strKey = mapKey.get("fieldname").toString();
			if (mapContent.containsKey(strKey)) {
				if (mapContent.get(strKey) == null) {
					continue;
				}
				strValue = String.valueOf(mapContent.get(strKey));
				if (Common.IsNullOrEmpty(strValue)) {
					continue;
				}
				if (strValue.startsWith("[") && strValue.endsWith("]")) {
					strValue = strValue.substring(1, strValue.length() - 1).trim();
					sbCondition.append("(");
					for (String strValueTemp : strValue.split(",")) {
						strValueTemp = strValueTemp.trim();
						strValueTemp = strValueTemp.substring(1, strValueTemp.length() - 1).trim();
						sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValueTemp)).append("' or ");
					}
					sbCondition.delete(sbCondition.length() - 3, sbCondition.length());
					sbCondition.append(") and ");
				} else {
					sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValue)).append("' and ");
				}
				// sbCondition.append(strKey).append(" ='").append(strValue).append("' and ");
			}
			mapContent.remove(strKey);
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		} else {
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_PRIMKEYSNOTEXIST.code));
		}

		int iSize = mapContent.size();
		String[] arrName = new String[iSize];
		Object[] arrValue;
		arrName = mapContent.keySet().toArray(arrName);
		arrValue = mapContent.values().toArray();
		try {
			// DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Update(strTableName, sbCondition.toString(), arrName, arrValue);
		} catch (Exception e) {
			Logger.WriteException(e);
			return "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code));
		}
		if (bolRet) {
			strRet = (String) mapContent.get("time");
		} else {
			strRet = "@".concat(String.valueOf(SysConfigMngr.ERROR_CODE.ERROR_UPDATEDATA.code));
		}
		mapContent = null;

		return strRet;
	}

	public static int delModuleInfo(Map<String, Object> ModuleInfo) {
		String strTableName = getTableName(ModuleInfo);
		// 默认表名有效，无效是客户端发的不对

		// 临时策略
		strTableName = getUserTable(strTableName, String.valueOf(ModuleInfo.get("username")));

		Map<String, Object> mapContent = getFieldNameAndValue(ModuleInfo, false);
		if (mapContent == null) {
			return SysConfigMngr.ERROR_CODE.ERROR_COMMONMODULEFIELDS.code;
		}

		DBHelper dbHelper = null;
		try {
			dbHelper = DBHelper.GetInstance();
		} catch (Exception e) {
			Logger.WriteException(e);
			return SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code;
		}

		StringBuilder sbCondition = new StringBuilder();
		String strKey = null;
		String strValue = null;
		for (Entry<String, Object> entry : mapContent.entrySet()) {
			if (entry.getValue() == null) {
				continue;
			}
			strKey = entry.getKey();
			strValue = String.valueOf(entry.getValue());
			if (Common.IsNullOrEmpty(strValue)) {
				continue;
			}

			if (strValue.startsWith("[") && strValue.endsWith("]")) {
				strValue = strValue.substring(1, strValue.length() - 1).trim();
				sbCondition.append("(");
				for (String strValueTemp : strValue.split(",")) {
					strValueTemp = strValueTemp.trim();
					strValueTemp = strValueTemp.substring(1, strValueTemp.length() - 1).trim();
					sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValueTemp)).append("' or ");
				}
				sbCondition.delete(sbCondition.length() - 3, sbCondition.length());
				sbCondition.append(") and ");
			} else {
				sbCondition.append(strKey).append(" ='").append(dbHelper.FilterSpecialCharacter(strValue)).append("' and ");
			}
		}

		if (sbCondition.length() > 0) {
			sbCondition.delete(sbCondition.length() - 4, sbCondition.length());
		} else {
			return SysConfigMngr.ERROR_CODE.ERROR_PRIMKEYSNOTEXIST.code;
		}

		// String strIDs = (String) mapContent.get("id");
		// if (strIDs == null || strIDs.isEmpty()) {
		// return bolRet;
		// }
		// if (strIDs.startsWith(",")) {
		// strIDs = strIDs.substring(1);
		// }
		// if (strIDs.endsWith(",")) {
		// strIDs = strIDs.substring(0, strIDs.length() - 1);
		// }
		//
		// String strCondition = "id in (".concat(strIDs).concat(")");
		boolean bolRet = false;
		int iRet = SysConfigMngr.ERROR_CODE.NO_ERROR.code;
		try {
			// DBHelper dbHelper = DBHelper.GetInstance();
			bolRet = dbHelper.Delete(strTableName, sbCondition.toString());
		} catch (Exception e) {
			Logger.WriteException(e);
			return SysConfigMngr.ERROR_CODE.ERROR_ACCESSDATEBASE.code;
		}

		if (!bolRet) {
			iRet = SysConfigMngr.ERROR_CODE.ERROR_DELETEDATA.code;
		}
		return iRet;
	}

	public static List<Map<String, Object>> getModuleList(Map<String, Object> ModuleInfo) {
		int iStart = 1;
		int iLen = 20;
		boolean bReocrdPage = true;
		String strOrder = "id desc";
		String strTableName = getTableName(ModuleInfo);// 默认表名有效，无效是客户端发的不对

		// 临时策略
		strTableName = getUserTable(strTableName, String.valueOf(ModuleInfo.get("username")));

		Map<String, String> mapFieldName = getFieldNames(ModuleInfo);
		if (mapFieldName == null) {
			return null;
		}

		StringBuilder sbFields = new StringBuilder();
		StringBuilder sbConditions = new StringBuilder();
		String strTemp;
		for (Entry<String, String> entry : mapFieldName.entrySet()) {
			sbFields.append(entry.getValue()).append(" ").append(entry.getKey()).append(",");
			if (ModuleInfo.get(entry.getKey()) == null) {
				continue;
			}
			strTemp = String.valueOf(ModuleInfo.get(entry.getKey()));
			if (strTemp == null || "".equals(strTemp)) {
				continue;
			}
			strTemp = strTemp.trim();
			String strFix1 = strTemp.substring(0, 1);
			String strFix2 = "";
			if (strTemp.length() > 1) {
				strFix2 = strTemp.substring(0, 2);
			}

			if (strTemp.endsWith("?")) {
				sbConditions.append(entry.getValue()).append(" like '").append(strTemp.substring(0, strTemp.length() - 1)).append("%' and ");
			} else if (">=".equals(strFix2) || "<=".equals(strFix2) || "!=".equals(strFix2)) {
				sbConditions.append(entry.getValue()).append(strFix2).append(" '").append(strTemp.substring(2, strTemp.length())).append("' and ");
			} else if (">".equals(strFix1) || "<".equals(strFix1) || "=".equals(strFix1)) {
				sbConditions.append(entry.getValue()).append(strFix1).append(" '").append(strTemp.substring(1, strTemp.length())).append("' and ");
			} else {
				sbConditions.append(entry.getValue()).append(" = '").append(strTemp).append("' and ");
			}
		}

		sbFields.deleteCharAt(sbFields.length() - 1);
		int iConditionLen = sbConditions.length();
		if (iConditionLen > 0) {
			sbConditions.delete(iConditionLen - 4, iConditionLen);
		}
		if (ModuleInfo.containsKey("start") && ModuleInfo.get("start") != null) {
			iStart = Integer.valueOf(ModuleInfo.get("start").toString());
		} else {
			bReocrdPage = false;
		}
		if (ModuleInfo.containsKey("length") && ModuleInfo.get("length") != null) {
			iLen = Integer.valueOf(ModuleInfo.get("length").toString());
		} else {
			bReocrdPage = false;
		}
		if (ModuleInfo.containsKey("order")) {
			strOrder = ModuleInfo.get("order").toString();
		}
		List<Map<String, Object>> lstInfo = null;
		try {
			DBHelper dbHelper = DBHelper.GetInstance();
			if (bReocrdPage) {
				lstInfo = dbHelper.ExecuteQuery(sbFields.toString(), strTableName, sbConditions.toString(), strOrder, iStart, iLen);
			} else {
				lstInfo = dbHelper.ExecuteQuery(sbFields.toString(), strTableName, sbConditions.toString(), strOrder);
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		mapFieldName = null;
		sbFields = null;

		return lstInfo;
	}

	private static String getTableName(Map<String, Object> ModuleInfo) {
		String strRet;
		String strName = ModuleInfo.get("name").toString();
		if (ModuleInfo.containsKey("type")) {
			strRet = ModuleMngr.getTableName(strName, ModuleInfo.get("type").toString());
		} else {
			strRet = ModuleMngr.getTableName(strName);
		}
		return strRet;
	}

	private static Map<String, Object> getFieldNameAndValue(Map<String, Object> ModuleInfo, boolean AddTime) {
		String strTableNameEN = ModuleInfo.get("name").toString();
		List<Map<String, Object>> lstFileds = ModuleMngr.getFieldNames(strTableNameEN);

		if (lstFileds == null) {
			return null;
		}

		Map<String, Object> mapInfo = new HashMap<String, Object>();

		Iterator<Map<String, Object>> iMap = lstFileds.iterator();
		Map<String, Object> mapTemp;
		String strFieldName = null;
		String strValue = null;
		while (iMap.hasNext()) {
			mapTemp = iMap.next();
			if (ModuleInfo.containsKey(mapTemp.get("fieldname_en"))) {
				strFieldName = mapTemp.get("fieldname").toString();
				strValue = ModuleInfo.get(mapTemp.get("fieldname_en")).toString();
			}

			// if ("time".equalsIgnoreCase(strFieldName)) {
			// mapInfo.put(strFieldName, Common.GetDateTime());
			// continue;
			// }
			if (strFieldName == null) {
				continue;
			}
			mapInfo.put(strFieldName, strValue);
			strFieldName = null;
		}

		if (!mapInfo.containsKey("time") && AddTime) {
			mapInfo.put("time", Common.GetDateTime());
		}
		if (mapInfo.size() == 0) {
			return null;
		}

		return mapInfo;
	}

	private static Map<String, String> getFieldNames(Map<String, Object> ModuleInfo) {
		String strTableNameEN = ModuleInfo.get("name").toString();
		List<Map<String, Object>> lstFileds = ModuleMngr.getFieldNames(strTableNameEN);

		if (lstFileds == null) {
			return null;
		}

		Map<String, String> mapInfo = new HashMap<String, String>();

		Iterator<Map<String, Object>> iMap = lstFileds.iterator();
		Map<String, Object> mapTemp;
		String strFieldNameEN = null;
		String strFieldName = null;
		while (iMap.hasNext()) {
			mapTemp = iMap.next();
			if (ModuleInfo.containsKey(mapTemp.get("fieldname_en"))) {
				strFieldNameEN = mapTemp.get("fieldname_en").toString();
				strFieldName = mapTemp.get("fieldname").toString();
			} else {
				continue;
			}

			mapInfo.put(strFieldNameEN, strFieldName);
		}
		if (mapInfo.size() == 0) {
			return null;
		}

		return mapInfo;
	}
	
	private static String getUserTable(String Table, String UserName) {
		return Table.concat(String.valueOf(Math.abs(UserName.hashCode())).substring(0, 1));
	}
}
