package Model;

import java.lang.reflect.Field;
import java.util.Map;

import BLL.Logger;
import Util.Common;

public class JournalInfoBean {
	private String title;
	private String titleen;
	private String author;
	private String type;
	private String address;
	private String language;
	private String size;
	private String issn;
	private String cn;
	private String mailcode;
	private String impactfactor;
	private String compositeimpactfactor;
	
	
	public JournalInfoBean(Map<String, Object> mapData) {
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


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getAuthor() {
		return author;
	}


	public void setAuthor(String author) {
		this.author = author;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public String getSize() {
		return size;
	}


	public void setSize(String size) {
		this.size = size;
	}


	public String getIssn() {
		return issn;
	}


	public void setIssn(String issn) {
		this.issn = issn;
	}


	public String getCn() {
		return cn;
	}


	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getImpactfactor() {
		return impactfactor;
	}


	public void setImpactfactor(String impactfactor) {
		this.impactfactor = impactfactor;
	}


	public String getCompositeimpactfactor() {
		return compositeimpactfactor;
	}


	public void setCompositeimpactfactor(String compositeimpactfactor) {
		this.compositeimpactfactor = compositeimpactfactor;
	}


	public String getMailcode() {
		return mailcode;
	}


	public void setMailcode(String mailcode) {
		this.mailcode = mailcode;
	}


	public String getTitleen() {
		return titleen;
	}


	public void setTitleen(String titleen) {
		this.titleen = titleen;
	}

}
