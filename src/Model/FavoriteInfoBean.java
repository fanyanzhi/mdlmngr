package Model;

import net.sf.json.JSONObject;

public class FavoriteInfoBean {
	private int ID;
	private String FavoriteName;
	private String UserName;
	private String FavoriteContent;
	private String FavoriteType;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getFavoriteName() {
		return FavoriteName;
	}

	public void setFavoriteName(String favoriteName) {
		FavoriteName = favoriteName;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public String getFavoriteContent() {
		return FavoriteContent;
	}

	public void setFavoriteContent(String favoriteContent) {
		FavoriteContent = favoriteContent;
	}

	public String getFavoriteType() {
		return FavoriteType;
	}

	public void setFavoriteType(String favoriteType) {
		FavoriteType = favoriteType;
	}

	public static FavoriteInfoBean getFavoriteInfoBean(String json) {
		JSONObject joFavoriteInfo = JSONObject.fromObject(json);
		return (FavoriteInfoBean) JSONObject.toBean(joFavoriteInfo, FavoriteInfoBean.class);
	}
}
