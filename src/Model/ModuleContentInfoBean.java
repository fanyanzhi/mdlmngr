package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

public class ModuleContentInfoBean {
	private int id;
	private int tableID;
	private String fieldName;
	private String fieldName_EN;
	private String fieldName_CH;
	private String fieldType;
	private int fieldLength;
	private boolean isAutoIncrement;
	private boolean isPrimKey;
	private boolean isIndex;
	private boolean isNull;
	private boolean isDisplay;
	private String time;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getTableID() {
		return tableID;
	}

	public void setTableID(int tableID) {
		this.tableID = tableID;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName_EN() {
		return fieldName_EN;
	}

	public void setFieldName_EN(String fieldName_EN) {
		this.fieldName_EN = fieldName_EN;
	}

	public String getFieldName_CH() {
		return fieldName_CH;
	}

	public void setFieldName_CH(String fieldName_CH) {
		this.fieldName_CH = fieldName_CH;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	public int getFieldLength() {
		return fieldLength;
	}

	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public void setAutoIncrement(boolean isAutoIncrement) {
		this.isAutoIncrement = isAutoIncrement;
	}

	public boolean isPrimKey() {
		return isPrimKey;
	}

	public void setPrimKey(boolean isPrimKey) {
		this.isPrimKey = isPrimKey;
	}

	public boolean isIndex() {
		return isIndex;
	}

	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}

	public boolean isNull() {
		return isNull;
	}

	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	public boolean isDisplay() {
		return isDisplay;
	}

	public void setDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public ModuleContentInfoBean() {

	}
	
	public ModuleContentInfoBean(Map<String, Object> mapData) {
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
