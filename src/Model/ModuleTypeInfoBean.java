package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

public class ModuleTypeInfoBean {
	private int id;
	private int tableId;
	private String typeName;
	private String typeName_EN;
	private String typeName_CH;
	private boolean isDelete;
	private String description;
	private String time;
	private String deleleTime;
	private int status;
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

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
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getTableId() {
		return tableId;
	}
	public void setTableId(int tableId) {
		this.tableId = tableId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
	public String getTypeName_EN() {
		return typeName_EN;
	}
	public void setTypeName_EN(String typeName_EN) {
		this.typeName_EN = typeName_EN;
	}
	public String getTypeName_CH() {
		return typeName_CH;
	}
	public void setTypeName_CH(String typeName_CH) {
		this.typeName_CH = typeName_CH;
	}
	public boolean getIsDelete() {
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
	public ModuleTypeInfoBean(){
		
	}
	
	public ModuleTypeInfoBean(Map<String, Object> mapData) {
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
