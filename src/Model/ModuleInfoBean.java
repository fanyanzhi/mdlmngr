package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

public class ModuleInfoBean {
	private int id;
	private String tableName;
	private String tableName_EN;
	private String tableName_CH;
	private boolean isDisplay;
	private int status;
	private boolean isDelete;
	private String description;
	private String deleleTime;
	
	public String getDeleleTime() {
		return deleleTime;
	}

	public void setDeleleTime(String deleleTime) {
		this.deleleTime = deleleTime;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	private String time;

	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName_EN() {
		return tableName_EN;
	}

	public void setTableName_EN(String tableName_EN) {
		this.tableName_EN = tableName_EN;
	}

	public String getTableName_CH() {
		return tableName_CH;
	}

	public void setTableName_CH(String tableName_CH) {
		this.tableName_CH = tableName_CH;
	}

	public boolean getIsDisplay() {
		return isDisplay;
	}

	public void setIsDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ModuleInfoBean()
	{
		
	}
	
	public ModuleInfoBean(Map<String, Object> mapData) {
		Field[] fileds = this.getClass().getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			try {
				Object objData=mapData.get(fileds[i].getName().toLowerCase());
				if(objData!=null&&String.valueOf(fileds).length()>0){
					fileds[i].set(this, Common.CovertToObject(mapData.get(fileds[i].getName().toLowerCase()), fileds[i].getType()));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
	} 
}
