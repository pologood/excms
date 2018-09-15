package cn.lonsun.staticcenter.exproject.lixin;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.staticcenter.generate.BsdtLinkListVO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;


public class AesCBCDecrypt {
	public static String key = "E9DA036F58B724C1";

	public static void main(String[] args) throws Exception {
		System.out.println(AesCBCDecrypt.decrypt("OEPC3k+++4jn/XJbGmUDbofk2ULspB44O487nAz2gjQ=", "1410161603AABBCD"));
		System.out.println("340204198409152015");
	}
	
	public static String decrypt(String data, String key) throws Exception{
		try{
			String iv = "0123456789abcdef";

			byte[] encrypted1 = new Base64().decode(data.getBytes());
			 
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
			             
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
			  
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original,"UTF-8");
			return originalString.trim();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static List<BsdtLinkListVO> getList(String json,String type,Integer limit){
		JSONObject jsonObject  = JSONObject.fromObject(json);
		JSONArray jsonArray = jsonObject.getJSONArray("data");

		for (int i = 0; i < jsonArray.size(); i++) {
			if(jsonArray.getJSONObject(i).get("type").equals(type)){
				jsonObject = jsonArray.getJSONObject(i);
			}
		}
		jsonArray = jsonObject.getJSONArray("items");
		if(!AppUtil.isEmpty(type)&&type.equals("bmfw")){
			jsonObject = jsonArray.getJSONObject(0);
		}else{
			for (int i = 0; i < jsonArray.size(); i++) {
				if(jsonArray.getJSONObject(i).get("type").equals("ztflsx")){//只需要按主题分类数据
					jsonObject = jsonArray.getJSONObject(i);
				}
			}
		}
		jsonArray = jsonObject.getJSONArray("items");
		int length = jsonArray.size()>limit?limit:jsonArray.size();
		List<BsdtLinkListVO> list = new ArrayList<BsdtLinkListVO>();
		for (int i = 0; i < length; i++) {
			BsdtLinkListVO vo = new BsdtLinkListVO();
			String dm = jsonArray.getJSONObject(i).get("dm")==null?"":jsonArray.getJSONObject(i).get("dm").toString();
			String jc = jsonArray.getJSONObject(i).get("jc")==null?"":jsonArray.getJSONObject(i).get("jc").toString();
			String mc = jsonArray.getJSONObject(i).get("mc")==null?"":jsonArray.getJSONObject(i).get("mc").toString();
			String lxjp = jsonArray.getJSONObject(i).get("lxjp")==null?"":jsonArray.getJSONObject(i).get("lxjp").toString();
			String pyjp = jsonArray.getJSONObject(i).get("pyjp")==null?"":jsonArray.getJSONObject(i).get("pyjp").toString();
			String sm = jsonArray.getJSONObject(i).get("sm")==null?"":jsonArray.getJSONObject(i).get("sm").toString();
			String sourceId = jsonArray.getJSONObject(i).get("sourceId")==null?"":jsonArray.getJSONObject(i).get("sourceId").toString();
			vo.setDm(dm);
			vo.setJc(jc);
			vo.setMc(mc);
			vo.setLxjp(lxjp);
			vo.setPyjp(pyjp);
			vo.setSm(sm);
			vo.setSourceId(sourceId);
			if(!AppUtil.isEmpty(type)&&type.equals("bmfw")){
				vo.setLink("http://bsdt.bozhou.gov.cn/portal/index.do?sspt=341623&mainTab="+type+"&subTab=gsbmsx&item="+dm+"&lxjp="+lxjp+"#sub_tab");
			}else{
				vo.setLink("http://bsdt.bozhou.gov.cn/portal/index.do?sspt=341623&subTab=ztflsx&item="+dm+"&mainTab="+type+"&lxjp="+lxjp+"#sub_tab");
			}
			list.add(vo);
		}
		return list;
	}
}