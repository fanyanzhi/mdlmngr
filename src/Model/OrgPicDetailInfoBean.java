package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;
public class OrgPicDetailInfoBean {
	//BelongsPicIndexID,PicFileName,PicFormat,PicID,PicResolution,PicSize,PicTheme
	private String belongspicindexid;
	private String picfilename;
	private String picformat ;
	private String picid;
	private String picresolution;
	private String picsize;
	private String pictheme;
	public OrgPicDetailInfoBean(Map<String, Object> mapData) {
		Field[] fileds = this.getClass().getDeclaredFields();
		for (int i = 0; i < fileds.length; i++) {
			try {
				Object objData=mapData.get(fileds[i].getName());
				if(objData!=null&&String.valueOf(fileds).length()>0){
					fileds[i].set(this, Common.CovertToObject(mapData.get(fileds[i].getName()), fileds[i].getType()));
				}
			} catch (Exception e) {
				Logger.WriteException(e);
			}
		}
		
	}
	public String getBelongspicindexid() {
		return belongspicindexid;
	}
	public void setBelongspicindexid(String belongspicindexid) {
		this.belongspicindexid = belongspicindexid;
	}
	public String getPicfilename() {
		return picfilename;
	}
	public void setPicfilename(String picfilename) {
		this.picfilename = picfilename;
	}
	public String getPicformat() {
		return picformat;
	}
	public void setPicformat(String picformat) {
		this.picformat = picformat;
	}
	public String getPicid() {
		return picid;
	}
	public void setPicid(String picid) {
		this.picid = picid;
	}
	public String getPicresolution() {
		return picresolution;
	}
	public void setPicresolution(String picresolution) {
		this.picresolution = picresolution;
	}
	public String getPicsize() {
		return picsize;
	}
	public void setPicsize(String picsize) {
		this.picsize = picsize;
	}
	public String getPictheme() {
		return pictheme;
	}
	public void setPictheme(String pictheme) {
		this.pictheme = pictheme;
	}
}
