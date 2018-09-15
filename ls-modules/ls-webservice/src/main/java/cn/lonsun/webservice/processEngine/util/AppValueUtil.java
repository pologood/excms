package cn.lonsun.webservice.processEngine.util;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * 工具类<br/>
 * 通用方法，使用频率非常高的方法，如数值类型转换,对象值拷贝
 * @Author taodp
 * @Time 2014年8月16日 下午2:49:20
 */
public class AppValueUtil {
    /***
     * 获取随机数
     * 日期 + 随机数 （主要用文件名称）
     * @return
     */
    public static String getRandom() {
        Calendar calendar = new GregorianCalendar();
        StringBuffer buffer = new StringBuffer();
        buffer.append(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DAY_OF_MONTH);
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int min=calendar.get(Calendar.MINUTE);
                int sec=calendar.get(Calendar.SECOND);
                int millsec=calendar.get(Calendar.MILLISECOND);
        if (month < 10) {
            buffer.append(0);
        }
        buffer.append(month);
        if (date < 10) {
            buffer.append(0);
        }
        buffer.append(date);
                if(hour<10){
                        buffer.append(0);
                }
                buffer.append(hour);
                if(min<10){
                        buffer.append(0);
                }
                buffer.append(min);
                if(sec<10){
                        buffer.append(0);
                }
                buffer.append(sec);
                if(millsec<100){
                        if(millsec<10){
                                buffer.append(0);
                        }
                        buffer.append(0);
                }
                buffer.append(millsec);
        String random = String.valueOf(Math.random());
        random = random.substring(random.length()-8, random.length());
        buffer.append(random);
        return buffer.toString();

    }
    /***
     * 是否为空判断 
     * @param o
     * @return
     */
    public static final boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }
        String s = null;
        if (!(o instanceof String)) {
            s = o.toString();
        } else {
            s = (String) o;
        }

        if (s == null || s.trim().length() == 0 || "null".equals(s)) {
            return true;
        }
        return false;
    }
    /**
     * 是否是int整数
     * @param sCheck
     * @return
     */
    public static final boolean isInt(String sCheck) {
        if (isEmpty(sCheck))
            return false;
        if (sCheck.substring(0, 1).equals("-"))
            sCheck = sCheck.substring(1);
        if (!(isNumeric(sCheck)))
            return false;
        long value;
        try{
            value = Long.parseLong(sCheck);
        }catch (NumberFormatException ex){
            return false;
        }
        return value <= 2147483647L;
    }
    /**
     * 判断是否是整数
     * @param sCheck
     * @return
     */
    public static final boolean isNumeric(String sCheck) {
        if (sCheck == null)
            return false;
        if (sCheck.length() == 0)
            return false;
        String numStr = "0123456789";
        for (int i = 0; i < sCheck.length(); ++i)
            if (numStr.indexOf(sCheck.charAt(i)) == -1)
                return false;
        
        return true;
    }
    

    
    /**
     * 将对象转为整型对象
     * @param obj
     * @return
     */
    public static Integer getInteger(Object obj){       
        if(null == obj){
            return null;
        }else if((obj instanceof String)){
            String data = (obj+"").trim();
            if("".equals(data)){
                return null;
            }else if(isInt(data)){
                return Integer.valueOf(data);
            }else{
                return null;
            }
        }else if((obj instanceof BigDecimal)){
            return (((BigDecimal)obj).intValue());
        }else if(isInt(obj+"")){
            return Integer.valueOf(obj+"");
        }else{
            return null;
        }
    }
    /**将对象转为整型值 */
    public static int getint(Object obj){
        Integer result = getInteger(obj);
        return null==result?0:result.intValue();
    }
    
    
    /***
     * 将对象转为短整型
     * @param obj
     * @return
     */
    public static Short getShort(Object obj){
        if(null == obj){
            return null;
        }else if((obj instanceof String)){
            String data = (obj+"").trim();
            if("".equals(data)){
                return null;
            }else if(isInt(data)){
                return Short.valueOf(data);
            }else{
                return null;
            }
        }else if((obj instanceof BigDecimal)){          
            return (((BigDecimal)obj).shortValueExact());
        }else if(isInt(obj+"")){
            return Short.valueOf(obj+"");
        }else{
            return null;
        }
    }
    /***
     * 将对象转为长整型
     * @param obj
     * @return
     */
    public static Long getLong(Object obj){
        if(null == obj){
            return null;
        }else if((obj instanceof String)){
            String data = (obj+"").trim();
            if("".equals(data)){
                return null;
            }else if(isInt(data)){
                return Long.valueOf(data);
            }else{
                return null;
            }
        }else if((obj instanceof BigDecimal)){
            return new Long(((BigDecimal)obj).longValue());
        }else if(isInt(obj+"")){
            return Long.valueOf(obj+"");
        }else if(obj instanceof Long){
            return (Long)(obj);
        }else{
            return null;
        }
    }
    
    /**将对象转为整型值 */
    public static long getlong(Object obj){
        Long result = getLong(obj);
        return null==result?0:result.longValue();
    }
    /***
     * 获取长整型
     * @param obj
     * @return
     */
    public static Double getDouble(Object obj){
        if(null == obj){
            return null;
        }else if((obj instanceof String)){
            String data = (obj+"").trim();
            if("".equals(data)){
                return null;
            }else if(isInt(data)){
                return Double.valueOf(data);
            }else{
                return null;
            }
        }else if((obj instanceof BigDecimal)){
            return new Double(((BigDecimal)obj).longValue());
        }else if(isInt(obj+"")){
            return Double.valueOf(obj+"");
        }else{
            return null;
        }
    }
    /***
     * 
     * @param obj
     * @return
     */
    public static Float getFloat(Object obj){
        if(null == obj){
            return null;
        }else if((obj instanceof String)){
            String data = (obj+"").trim();
            if("".equals(data)){
                return null;
            }else if(isFloat(data)){
                return Float.valueOf(data);
            }else{
                return null;
            }
        }else if((obj instanceof BigDecimal)){
            return new Float(((BigDecimal)obj).longValue());
        }else if(isFloat(obj+"")){
            return Float.valueOf(obj+"");
        }else{
            return null;
        }
    }
    /**
     * 判断是否是float数字
     * @param sCheck
     * @return
     */
    public static final boolean isFloat(String sCheck) {
        if (isEmpty(sCheck))
            return false;
        if (sCheck.indexOf(".") != -1) {
            int dotPos = sCheck.indexOf(".");
            sCheck = sCheck.substring(0, dotPos) + sCheck.substring(dotPos + 1);
        }
        return isNumeric(sCheck);
    }
    /*
     * 将对象转为指定的字符串
     */
    public static String getValue(Object obj){
        if(null == obj){
            return "";
        }else if(obj instanceof String){
            return (String)obj;
        }else{
            return obj+"";
        }
    }
    /***
     * 属性拷贝
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest,Object orig){
        org.springframework.beans.BeanUtils.copyProperties(orig, dest);
    }
    /***
     * 
     * 属性拷贝
     * @author
     * @param dest
     * @param orig
     * @param ignoreProperties
     */
    public static void copyProperties(Object dest,Object orig,String[] ignoreProperties){
        org.springframework.beans.BeanUtils.copyProperties(orig, dest,ignoreProperties);
    }
    
    /**
     * 根据分隔符将字符串转换Long数组
     * @param str 目标字符串
     * @param delimiter	分割符
     * @return
     */
    public static Long[] getLongs(String str,String delimiter){
    	if(null == str || null == delimiter || "".equals(str) || "".equals(delimiter)) return null;
    	String strs[] = str.split(delimiter);
    	Long[] result = new Long[strs.length];
    	for(int i=0;i<strs.length;i++){
    	    result[i] = getLong(strs[i]);
    	}
    	return result;
    }
   
    /**
     * 获取UUID
     * @Time 2014年8月19日 下午7:32:59
     * @return
     */
    public static String getUuid(){
    	UUID uuid = UUID.randomUUID();
    	return uuid.toString();
    }
    
    
    /**
	 * 传入Date类型和需要转换格式返回对应字符串
	 * @param date
	 * @param format
	 * @return
	 */
	public static String formatTimeToString(Date date,String format){
		SimpleDateFormat f = new SimpleDateFormat(format);
		if(date!=null){
			return f.format(date);
		}
		return null;
	}

	/**
     * 字符串转换时间的方法，失败后返回null
     * @Time 2014年9月16日 下午3:41:13
     * @param str
     * @param pattern
     * @return
     */
    public static Date formatStringToTime(String str,String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date result = null;
        try {
            result = format.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return result;
    }
	
	/**
	 * 获取当前年份方法
	 * @Time 2014年9月5日 上午11:17:10
	 * @return
	 */
	public static int getCurrentYear(){
	    return getint(Calendar.getInstance().getTime().toString().substring(Calendar.getInstance().getTime().toString().length()-4));
	}
	
	/**
	 * 获取当前时间所在的年份
	 * @Time 2014年9月13日 下午3:20:02
	 * @return
	 */
	public static int getYearByDate(Date date){
	    return getint(date.toString().substring(date.toString().length()-4));
	}
	
	/**
	 * 获取当前时间所在年度的周数
	 * @Time 2014年9月13日 下午3:08:05
	 * @return
	 */
	public static int getCurrentWeekOfYear(){
	    return getint(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
	} 
	
	/**
	 * 获取指定时间所在年度的周数
	 * @Time 2014年9月13日 下午3:08:30
	 * @param date
	 * @return
	 */
	public static int getWeekOfYearByDate(Date date){
	    Calendar c = Calendar.getInstance();
	    c.setTime(date);
	    int weekOfYear = getint(c.get(Calendar.WEEK_OF_YEAR));
	    //中国化日期修改，星期天作为一周的开始
	    if(c.get(Calendar.DAY_OF_WEEK)==1){
	        weekOfYear--;
	    }
	    return weekOfYear;
	}
	/***
	 * 读取文本文件
	 *
	 * @param realFile
	 * @return
	 */
	public static String readText(String realFile){
	    String result = "";
	    File file = new File(realFile);
	    if(file.exists()){
		try {
		    BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
		    StringBuilder sb=new StringBuilder();
		    String temp=br.readLine();
		    while(null !=temp){
		       sb.append(temp+" ");
		       temp=br.readLine();
		    }
		    br.close();
		    result = sb.toString();
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		    
		}
	    }
	    return result;
	    
	}
	/***
	 * 格式化sql 的in条件
	 *
	 * @param ids
	 * @param field
	 * @return
	 */
	public static String getInSql(String ids,String field){
		if(isEmpty(ids)){return field + " not in (-1)";}
		int maxLen = 900;
		StringBuffer sb = new StringBuffer();
		String[] idArray = ids.split(",");
		List<String> agentList = new ArrayList<String>();
		for(int k=0;k<idArray.length;k++){
			if("".equals(idArray[k])) continue;
			agentList.add(idArray[k]);
		}
		for(int i=0;i<agentList.size();i++){
		   if(i==agentList.size()-1)
		    sb.append(agentList.get(i)).append(")");
		   else if(i%maxLen==0&&i>0)
		    sb.append(agentList.get(i))
		    .append(") or " + field + " in (");
		   else
		    sb.append(agentList.get(i)).append(",");
		}
		
		if(agentList.size()>0){
			return field + " in (-1,"+sb;
		}else{
			return field + " in (-1)";
		}
	}



    /**
     * 将not in条件折分，以解决超过1000个id值报错的问题
     *
     * @param ids
     * @param field
     * @return
     */
    public static String getNotInSql(String ids, String field) {
        if (isEmpty(ids)) {
            return field + " not in (-1)";
        }
        int maxLen = 900;
        StringBuffer sb = new StringBuffer();
        String[] idArray = ids.split(",");
        List agentList = new ArrayList();
        for (int k = 0; k < idArray.length; k++) {
            if ("".equals(idArray)) continue;
            agentList.add(idArray[k]);
        }
        for (int i = 0; i < agentList.size(); i++) {
            if (i == agentList.size() - 1)
                sb.append(agentList.get(i)).append(")");
            else if (i % maxLen == 0 && i > 0)
                sb.append(agentList.get(i))
                        .append(") or " + field + " not in (");
            else
                sb.append(agentList.get(i)).append(",");
        }

        return field + " not in (-1," + sb;
    }
	/***
	 * 
	 * 下载
	 * @param data
	 * @param request
	 * @param response
	 * @param fileName
	 * @throws java.io.IOException
	 */
	public static void down(byte[] data,HttpServletRequest request,HttpServletResponse response,String fileName)throws IOException {
	    response.setContentType("application/x-download"); 
	    response.reset();
	    response.addHeader("Content-Disposition", "attachment;filename="+new String(fileName.getBytes("gbk"),"iso-8859-1"));  //转码之后下载的文件不会出现中文乱码
	    response.addHeader("Content-Length", "" + data.length);
	    OutputStream toClient = new BufferedOutputStream(response.getOutputStream());	     
	    toClient.write(data); 
	    toClient.flush(); 
	    toClient.close();	    
	}
    /**
     * 拷贝map数据
     * @param data
     * @return
     */
    public static Map<String,Object> copyIndex0ByMap(Map<String,Object> data) {
        if(null == data) return null;
        Map<String,Object> ret = new ConcurrentHashMap<String, Object>();
        Iterator<Map.Entry<String,Object>> it = data.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,Object> entry = it.next();
            Object value = entry.getValue();
            if(null == value) continue;
            if(value.getClass().isArray()){
                ret.put(entry.getKey(),((Object[])value)[0]);
            }else{
                ret.put(entry.getKey(),value);
            }
        }
        return ret;
    }
    public static String getFileName(HttpServletRequest request,String fileName) throws UnsupportedEncodingException {
        String rest = "filename=\"" + fileName + "\"";;
        String userAgent = request.getHeader("USER-AGENT");
        String newFileName = URLEncoder.encode(fileName, "UTF-8");
        if(null !=userAgent){
            userAgent = userAgent.toLowerCase();
            if (userAgent.indexOf("msie") != -1){
                rest ="filename=\"" + newFileName + "\"";
            }else if (userAgent.indexOf("opera") != -1){
                rest = "filename*=UTF-8''" + newFileName;
            }else if (userAgent.indexOf("safari") != -1 ){
                rest = "filename=\"" + new String(fileName.getBytes("UTF-8"),"ISO8859-1") + "\"";
            }else if (userAgent.indexOf("applewebkit") != -1 ){
                newFileName = MimeUtility.encodeText(fileName, "UTF8", "B");
                rest = "filename=\"" + newFileName + "\"";
            }else if (userAgent.indexOf("mozilla") != -1){
                rest =  "filename*=UTF-8''" + newFileName;
            }
        }
        return rest;
    }

}
