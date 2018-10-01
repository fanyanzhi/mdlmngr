package DAL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MySql extends DBHelper {

	private String mBaseConfName = null;

	public MySql() {
		PropertiesUtils.loadFile("dbconfig.properties");
		mDrive = PropertiesUtils.getPropertyValue("Drive");
		mURL = PropertiesUtils.getPropertyValue("URL");
		mUserName = PropertiesUtils.getPropertyValue("UserName");
		mPassWord = PropertiesUtils.getPropertyValue("PassWord");
		String strConnections = PropertiesUtils.getPropertyValue("MaxConnections");
		if (strConnections == null) {
			mMaxConnections = 0;
		} else {
			mMaxConnections = Integer.valueOf(strConnections);
		}
	}

	public MySql(String ConfName) {
		mBaseConfName = ConfName;
		PropertiesUtils.loadFile("dbconfig.properties");
		mDrive = PropertiesUtils.getPropertyValue(mBaseConfName.concat("Drive"));
		mURL = PropertiesUtils.getPropertyValue(mBaseConfName.concat("URL"));
		mUserName = PropertiesUtils.getPropertyValue(mBaseConfName.concat("UserName"));
		mPassWord = PropertiesUtils.getPropertyValue(mBaseConfName.concat("PassWord"));
		String strConnections = PropertiesUtils.getPropertyValue(mBaseConfName.concat("MaxConnections"));
		if (strConnections == null) {
			mMaxConnections = 0;
		} else {
			mMaxConnections = Integer.valueOf(strConnections);
		}
	}

	public MySql(String DBType, String Drive, String URL, String UserName, String PassWord, Integer MaxConnections) {
		mDrive = Drive;
		mURL = URL;
		mUserName = UserName;
		mPassWord = PassWord;
		mMaxConnections = MaxConnections;
	}

	@Override
	protected ConnectionModel GetConnection() throws SQLException, ClassNotFoundException {
		ConnectionModel connModel = null;
		boolean bolFlg = true;
		if (mMaxConnections > 0) {
			try {
				connModel = ConnectionPool.GetConnection(mDrive, mURL, mUserName, mPassWord, mMaxConnections, false);
			} catch (Exception ex) {
				bolFlg = false;
			}
		} else {
			Class.forName(mDrive);
			try {
				Connection conn = DriverManager.getConnection(mURL, mUserName, mPassWord);
				connModel = new ConnectionModel(conn, false, new java.util.Date());
			} catch (Exception ex) {
				bolFlg = false;
			}
		}
		if (bolFlg) {
			return connModel;
		}

		return GetSalveConnection(false);
	}

	@Override
	protected ConnectionModel GetConnection(boolean IsLock) throws SQLException, ClassNotFoundException {
		ConnectionModel connModel = null;
		boolean bolFlg = true;
		if (mMaxConnections > 0) {
			try {
				connModel = ConnectionPool.GetConnection(mDrive, mURL, mUserName, mPassWord, mMaxConnections, IsLock);
			} catch (Exception ex) {
				bolFlg = false;
			}
		} else {
			Class.forName(mDrive);
			try {
				Connection conn = DriverManager.getConnection(mURL, mUserName, mPassWord);
				connModel = new ConnectionModel(conn, IsLock, new java.util.Date());
				;
			} catch (Exception ex) {
				bolFlg = false;
			}
		}
		if (bolFlg) {
			return connModel;
		}

		return GetSalveConnection(IsLock);
	}

	private ConnectionModel GetSalveConnection(boolean IsLock) throws ClassNotFoundException, SQLException {
		String strMasterUrlConf = "URL";
		String strSlaveUrlConf = "SlaveURL";
		String strMasterMaxConnectionsConf = "MaxConnections";
		String strSlaveMaxConnectionsConf = "SlaveMaxConnections";

		if (mBaseConfName != null) {
			strMasterUrlConf = mBaseConfName.concat("URL");
			strSlaveUrlConf = mBaseConfName.concat("SlaveURL");
			strMasterMaxConnectionsConf = mBaseConfName.concat("MaxConnections");
			strSlaveMaxConnectionsConf = mBaseConfName.concat("SlaveMaxConnections");
		}
		PropertiesUtils.loadFile("dbconfig.properties");
		String strMasterUrl = PropertiesUtils.getPropertyValue(strMasterUrlConf);
		String strSlaveUrl = PropertiesUtils.getPropertyValue(strSlaveUrlConf);
		String strMasterMaxConnections = PropertiesUtils.getPropertyValue(strMasterMaxConnectionsConf);
		String strSlaveMaxConnections = PropertiesUtils.getPropertyValue(strSlaveMaxConnectionsConf);

		if (strSlaveUrl == null) {
			return null;
		}

		String strMasterAddr = GetDataBaseAddr(strMasterUrl);
		String strSlaveAddr = GetDataBaseAddr(strSlaveUrl);
		String strCurAddr = GetDataBaseAddr(mURL);

		if (!strCurAddr.equals(strMasterAddr) && !strCurAddr.equals(strSlaveAddr)) {
			return null;
		}

		if (strCurAddr.equals(strMasterAddr)) {
			mIsSlaveConnection = false;
		} else {
			mIsSlaveConnection = true;
		}

		if (mIsSlaveConnection) {
			mURL = mURL.replace(strCurAddr, strMasterAddr);
			mMaxConnections = strMasterMaxConnections == null ? 0 : Integer.valueOf(strMasterMaxConnections);
		} else {
			mURL = mURL.replace(strCurAddr, strSlaveAddr);
			mMaxConnections = strSlaveMaxConnections == null ? 0 : Integer.valueOf(strSlaveMaxConnections);
		}

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

	@Override
	public List<Map<String, Object>> ExecuteQuery(String Fields, String Table, String Condition, String Order, int Start, int Length) throws ClassNotFoundException, SQLException {
		StringBuilder sbSql = new StringBuilder("SELECT ").append(Fields).append(" FROM ").append(Table);
		if (!Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		if (!Order.isEmpty()) {
			sbSql.append(" ORDER BY ").append(Order);
		}
		sbSql.append(" LIMIT ").append(Start - 1).append(",").append(Length);
		return ExecuteQuery(sbSql.toString());
	}

	private String GetDataBaseAddr(String URL) {
		String strRet = URL.substring(URL.indexOf("//") + 2, URL.lastIndexOf("/"));
		return strRet;
	}

	@Override
	public String FilterSpecialCharacter(String Condition) {
		if (Condition == null || Condition.isEmpty()) {
			return Condition;
		}
		Condition = Condition.replace("\\", "\\\\");
		Condition = Condition.replace("%", "\\%");
		//Condition = Condition.replace("_", "\\_");
		Condition = Condition.replace("'", "\\'");
		return Condition;
	}

	@Override
	public String GetSql(String Fields, String Table, String Condition, String Order, int Start, int Length) {
		StringBuilder sbSql = new StringBuilder("SELECT ").append(Fields).append(" FROM ").append(Table);
		if (!Condition.isEmpty()) {
			sbSql.append(" WHERE ").append(Condition);
		}
		if (!Order.isEmpty()) {
			sbSql.append(" ORDER BY ").append(Order);
		}
		sbSql.append(" LIMIT ").append(Start - 1).append(",").append(Length);
		return sbSql.toString();
	}
}
