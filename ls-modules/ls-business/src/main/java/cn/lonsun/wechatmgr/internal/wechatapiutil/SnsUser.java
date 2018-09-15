package cn.lonsun.wechatmgr.internal.wechatapiutil;

import java.util.List;

public class SnsUser {

	private String openid;
	
	private String nickname;
	
	private String sex;
	
	private String province;
	
	private String city;
	
	private String country;
	
	private String headimgurl;
	
	private List<String> privilege;
	
	private String unionid;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public List<String> getPrivilege() {
		return privilege;
	}

	public void setPrivilege(List<String> privilege) {
		this.privilege = privilege;
	}

	public String getUnionid() {
		return unionid;
	}

	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
	
	public static void main(String[] args) {
//		SnsUser u=new SnsUser();
//		u.setOpenid("asass");
//		u.setNickname("好滴");
//		u.setCity("asa");
//		u.setCountry("adsd");
//		u.setHeadimgurl("hhh");
//		u.setPrivilege(new ArrayList<String>(){{add("111");add("222222222");}});
//		u.setProvince("ada");
//		u.setSex("nv");
//		u.setUnionid("asadsad");
//		JSONObject json = JSONObject.fromObject(u);
//		System.out.println(json.toString());
//		SnsUser us=(SnsUser) JSONObject.toBean(json, SnsUser.class);
//		System.out.println(us.getPrivilege());
		
		System.out.println("/webAuth/53310/login".indexOf("/webAuth/"));
	}
	
}
