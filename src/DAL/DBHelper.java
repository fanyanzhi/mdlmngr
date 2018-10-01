package DAL;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

import Model.HotLiterature;
import Util.Common;

public abstract class DBHelper {

	private static Map<String, DBHelper> mDBHelpers;
	private static DBHelper mDBHelper;
	protected String mDrive;
	protected String mURL;
	protected String mUserName;
	protected String mPassWord;
	protected int mMaxConnections;
	protected Boolean mIsSlaveConnection = false;

	public String getConnectionURL() {
		return mURL;
	}

	public Boolean getIsSlaveConnection() {
		return mIsSlaveConnection;
	}

	static {
		if (mDBHelpers == null) {
			mDBHelpers = new HashMap<String, DBHelper>();
		}
	}

	public DBHelper() {
	}

	@SuppressWarnings("unchecked")
	public static DBHelper GetInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		if (mDBHelper == null) {
			PropertiesUtils.loadFile("dbconfig.properties");
			String strDBType = PropertiesUtils.getPropertyValue("DBType");
			Class<DBHelper> clsDBType = (Class<DBHelper>) Class.forName("DAL.".concat(strDBType));
			mDBHelper = (DBHelper) clsDBType.newInstance();

		}
		return mDBHelper;
	}

	@SuppressWarnings("unchecked")
	public static DBHelper GetInstance(String ConfName) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String strKey = ConfName;
		DBHelper dbHelper;
		PropertiesUtils.loadFile("dbconfig.properties");
		String strDBType = PropertiesUtils.getPropertyValue(ConfName.concat("DBType"));
		if (mDBHelpers.containsKey(strKey)) {
			dbHelper = mDBHelpers.get(strKey);
		} else {
			Class<DBHelper> clsDBType = (Class<DBHelper>) Class.forName("DAL.".concat(strDBType));
			Constructor<DBHelper> conDBType = clsDBType.getConstructor(String.class);
			dbHelper = conDBType.newInstance(ConfName);
			mDBHelpers.put(strKey, dbHelper);
		}
		return dbHelper;
	}

	@SuppressWarnings("unchecked")
	public static DBHelper GetInstance(String DBType, String Drive, String URL, String UserName, String PassWord, int MaxConnections) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String strKey = Drive.concat(URL).toLowerCase();
		DBHelper dbHelper;
		if (mDBHelpers.containsKey(strKey)) {
			dbHelper = mDBHelpers.get(strKey);
		} else {
			Class<DBHelper> clsDBType = (Class<DBHelper>) Class.forName("DAL.".concat(DBType));
			Constructor<DBHelper> conDBType = clsDBType.getConstructor(String.class, String.class, String.class, String.class, String.class, Integer.class);
			dbHelper = conDBType.newInstance(DBType, Drive, URL, UserName, PassWord, MaxConnections);
			mDBHelpers.put(strKey, dbHelper);
		}
		return dbHelper;
	}

	protected ConnectionModel GetConnection() throws SQLException, ClassNotFoundException {
		if (mMaxConnections > 0) {
			return ConnectionPool.GetConnection(mDrive, mURL, mUserName, mPassWord, mMaxConnections, false);
		}

		Class.forName(mDrive);
		Connection conn = DriverManager.getConnection(mURL, mUserName, mPassWord);
		if (conn.isClosed()) {
			return null;
		}
		return new ConnectionModel(conn, false, new java.util.Date());
	}

	protected ConnectionModel GetConnection(boolean IsLock) throws SQLException, ClassNotFoundException {
		if (mMaxConnections > 0) {
			return ConnectionPool.GetConnection(mDrive, mURL, mUserName, mPassWord, mMaxConnections, IsLock);
		}

		Class.forName(mDrive);
		Connection conn = DriverManager.getConnection(mURL, mUserName, mPassWord);
		if (conn.isClosed()) {
			return null;
		}
		return new ConnectionModel(conn, IsLock, new java.util.Date());
	}

	private List<Map<String, Object>> GetListResult(ResultSet RS) throws SQLException {
		int iCount = RS.getMetaData().getColumnCount();
		if (iCount == 0) {
			return null;
		}
		List<Map<String, Object>> lst = new ArrayList<Map<String, Object>>();
		ResultSetMetaData rsmd = RS.getMetaData();
		Object objValue = null;
		while (RS.next()) {
			Map<String, Object> m = new HashMap<String, Object>(iCount);
			for (int i = 1; i <= iCount; i++) {
				objValue = RS.getObject(i);
				if ("DATETIME".equals(rsmd.getColumnTypeName(i)) && objValue != null) {
					objValue = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(objValue);
				}
				m.put(rsmd.getColumnLabel(i).toLowerCase(), objValue);
			}
			lst.add(m);
		}
		return lst;
	}

	public List<Map<String, Object>> ExecuteQuery(String Sql) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		if (conn == null) {
			return null;
		}
		PreparedStatement pstm = conn.prepareStatement(Sql);
		ResultSet rs = pstm.executeQuery();
		List<Map<String, Object>> lst = GetListResult(rs);
		rs.close();
		pstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		if (lst.size() == 0) {
			lst = null;
		}
		return lst;
	}
	public Integer ExecuteQueryObject(String Sql) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		if (conn == null) {
			return null;
		}
		Integer sumInt = null;
		PreparedStatement pstm = conn.prepareStatement(Sql);
		ResultSet rs = pstm.executeQuery();
		while (rs.next())
		{
			sumInt =rs.getInt("sum");
		}
		rs.close();
		pstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		return sumInt;
	}

	public List<Map<String, Object>> ExecuteQuery(String Fields, String Table) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder("SELECT ").append(Fields).append(" FROM ").append(Table);
		return ExecuteQuery(sbSql.toString());
	}

	public List<Map<String, Object>> ExecuteQuery(String Fields, String Table, String Condition) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder("SELECT ").append(Fields).append(" FROM ").append(Table);
		if (Condition != null && !Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		String strSql = sbSql.toString();
		sbSql = null;
		return ExecuteQuery(strSql);
	}

	public List<Map<String, Object>> ExecuteQuery(String Fields, String Table, String Condition, String Order) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder("SELECT ").append(Fields).append(" FROM ").append(Table);
		if (Condition != null && !Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		if (Order != null && !Order.isEmpty()) {
			sbSql.append(" ORDER BY ").append(Order);
		}
		return ExecuteQuery(sbSql.toString());
	}

	public List<Map<String, Object>> ExecuteQuery(String Fields, String Table, String Condition, String Order, int Start, int Length) throws ClassNotFoundException, SQLException {
		return null;
	}
	
	private PreparedStatement GetPreparedStatement(Connection Conn, String Sql, Object[] Param) throws SQLException {
		String strParamType = null;
		Object objParamValue;
		PreparedStatement pstm = Conn.prepareStatement(Sql);
		for (int i = 1; i <= Param.length; i++) {
			objParamValue = Param[i - 1];
			if (objParamValue != null) {
				strParamType = objParamValue.getClass().getSimpleName().toLowerCase();
			} else {
				strParamType = null;
			}
			if ("string".equals(strParamType)) {
				pstm.setString(i, (String) objParamValue);
			} else if ("integer".equals(strParamType)) {
				pstm.setInt(i, (int) objParamValue);
			} else if ("long".equals(strParamType)) {
				pstm.setLong(i, (long) objParamValue);
			} else if ("date".equals(strParamType)) {
				pstm.setDate(i, (Date) objParamValue);
			} else if ("date".equals(strParamType)) {
				pstm.setDate(i, (Date) objParamValue);
			} else if ("boolean".equals(strParamType)) {
				pstm.setBoolean(i, (boolean) objParamValue);
			} else {
				pstm.setObject(i, objParamValue);
			}
		}
		return pstm;
	}

	public Boolean ExecuteSql(String Sql) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		PreparedStatement pstm = conn.prepareStatement(Sql);
		int iCount = pstm.executeUpdate();
		pstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		return iCount > 0 ? true : false;
	}

	public Boolean ExecuteSql(String Sql, Object[] Param) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		PreparedStatement pstm = GetPreparedStatement(conn, Sql, Param);
		int iCount = pstm.executeUpdate();
		pstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		return iCount > 0 ? true : false;
	}

	public Boolean ExecuteSql(List<String> Sqls) throws ClassNotFoundException, SQLException {
		if (Sqls.size() == 0) {
			return false;
		}
		ConnectionModel connModel = GetConnection(true);
		Connection conn = connModel.getDBConnection();
		PreparedStatement pstm = conn.prepareStatement(Sqls.get(0));
		Sqls.remove(0);
		pstm.addBatch();
		try {
			conn.setAutoCommit(false);
			for (String strSql : Sqls) {
				pstm.addBatch(strSql);
				// stm.execute(strSql);
			}
			pstm.executeBatch();
			conn.commit();
		} catch (Exception ex) {
			conn.rollback();
			throw ex;
		} finally {
			conn.setAutoCommit(true);
			pstm.close();
			connModel.setIsLock(false);
			if (mMaxConnections == 0) {
				conn.close();
			}
		}
		return true;
	}

	private CallableStatement GetCallableStatement(Connection Conn, String ProcName, Object[] Param) throws SQLException {
		String strParamType = null;
		Object objParamValue;
		int iCount = 0;
		if (Param != null) {
			iCount = Param.length;
		}
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("{call ").append(ProcName);
		if (iCount > 0) {
			sbSql.append("(");
			for (int i = 0; i < iCount; i++) {
				sbSql.append("?,");
			}
			sbSql.deleteCharAt(sbSql.length() - 1);
			sbSql.append(")");
		}
		sbSql.append("}");
		CallableStatement cstm = Conn.prepareCall(sbSql.toString());
		for (int i = 1; i <= iCount; i++) {
			objParamValue = Param[i - 1];
			if (objParamValue != null) {
				strParamType = objParamValue.getClass().getSimpleName().toLowerCase();
			} else {
				strParamType = null;
			}
			if ("string".equals(strParamType)) {
				cstm.setString(i, (String) objParamValue);
			} else if ("integer".equals(strParamType)) {
				cstm.setInt(i, (int) objParamValue);
			} else if ("long".equals(strParamType)) {
				cstm.setLong(i, (long) objParamValue);
			} else if ("date".equals(strParamType)) {
				cstm.setDate(i, (Date) objParamValue);
			} else if ("date".equals(strParamType)) {
				cstm.setDate(i, (Date) objParamValue);
			} else if ("boolean".equals(strParamType)) {
				cstm.setBoolean(i, (boolean) objParamValue);
			} else {
				cstm.setObject(i, objParamValue);
			}
		}
		return cstm;
	}

	public Boolean ExecuteProc(String ProcName, Object[] Param) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		CallableStatement cstm = GetCallableStatement(conn, ProcName, Param);
		int iCount = cstm.executeUpdate();
		cstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		return iCount > 0 ? true : false;
	}

	public List<Map<String, Object>> ExecuteQueryProc(String ProcName, Object[] Param) throws ClassNotFoundException, SQLException {
		Connection conn = GetConnection().getDBConnection();
		CallableStatement cstm = GetCallableStatement(conn, ProcName, Param);
		ResultSet rs = cstm.executeQuery();
		List<Map<String, Object>> lstRet = GetListResult(rs);
		rs.close();
		cstm.close();
		if (mMaxConnections == 0) {
			conn.close();
		}
		return lstRet;
	}
	

	public Boolean Insert(String TableName, String[] Fields, Object[] Values) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder();
		StringBuilder sbValues = new StringBuilder();
		sbSql.append("INSERT INTO ").append(TableName).append(" (");
		for (String strField : Fields) {
			sbSql.append(strField).append(",");
			sbValues.append("?,");
		}
		sbSql.deleteCharAt(sbSql.length() - 1);
		sbValues.deleteCharAt(sbValues.length() - 1);
		sbSql.append(") VALUES (").append(sbValues.toString()).append(")");
		sbValues = null;
		return ExecuteSql(sbSql.toString(), Values);
	}

	public Boolean Update(String TableName, String Condition, String[] Fields, Object[] Values) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("UPDATE ").append(TableName).append(" SET ");
		for (String strField : Fields) {
			sbSql.append(strField).append("=?,");
		}
		sbSql.deleteCharAt(sbSql.length() - 1);
		if (Condition != null && !Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		return ExecuteSql(sbSql.toString(), Values);
	}

	public Boolean Delete(String Tablename, String Condition) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("DELETE FROM ").append(Tablename);
		if (Condition != null && !Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		return ExecuteSql(sbSql.toString());
	}

	public int GetCount(String Tablename, String Condition) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT COUNT(1) AS count FROM ").append(Tablename);
		if (Condition != null && !Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		List<Map<String, Object>> lstCount = ExecuteQuery(sbSql.toString());
		int iCount = (int) (long) lstCount.get(0).get("count");
		return iCount;
	}

	public String FilterSpecialCharacter(String Condition) {
		return Condition;
	}

	public String GetSql(String Fields, String Table, String Condition, String Order, int Start, int Length) {
		return null;
	}
	
	//特定方法，不可复用
	public Boolean ExecuteSqlHotStatistic(String sql, HashSet<HotLiterature> listone) throws ClassNotFoundException, SQLException {
		if(null == listone || listone.size() == 0) {
			return false;
		}
		ConnectionModel connModel = GetConnection(true);
		Connection conn = connModel.getDBConnection();
		PreparedStatement pstm = conn.prepareStatement(sql);
		try {
			conn.setAutoCommit(false);
			for (HotLiterature hotLiterature : listone) {
				pstm.setString(1, hotLiterature.getFileid());
				pstm.setString(2, hotLiterature.getTypeid());
				pstm.setString(3, hotLiterature.getFilename());
				pstm.setString(4, hotLiterature.getAuthor());
				String date = hotLiterature.getDate();
				if(Common.IsNullOrEmpty(date)) {
					date = null;
				}
				pstm.setString(5, date);
				pstm.setString(6, hotLiterature.getYear());
				pstm.setString(7, hotLiterature.getIssue());
				pstm.setString(8, hotLiterature.getCode());
				pstm.setString(9, hotLiterature.getSource());
				pstm.setString(10, hotLiterature.getSourcech());
				pstm.setFloat(11, hotLiterature.getFactor());
				pstm.setString(12, hotLiterature.getTime());
				pstm.addBatch();
			}
			pstm.executeBatch();
			conn.commit();
		} catch (Exception ex) {
			throw ex;
		} finally {
			conn.setAutoCommit(true);
			pstm.close();
			connModel.setIsLock(false);
			if (mMaxConnections == 0) {
				conn.close();
			}
		}
		return true;
	}
}
