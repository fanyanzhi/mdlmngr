package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;
import net.sf.json.JSONObject;

public class RecommendationInfoBean {
	private int id;
	private String title;
	private int typeId;
	private String fileId;
	private String description;
	private int important;
	
	private String time;

	
	public int getImportant() {
		return important;
	}

	public void setImportant(int important) {
		this.important = important;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}


	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public static RecommendationInfoBean getRecommendationInfoBean(String json) {
		JSONObject joRecommendationInfo = JSONObject.fromObject(json);
		return (RecommendationInfoBean) JSONObject.toBean(joRecommendationInfo, RecommendationInfoBean.class);
	}
	
	public RecommendationInfoBean()
	{
		
	}
	
	public RecommendationInfoBean(Map<String, Object> mapData) {
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
