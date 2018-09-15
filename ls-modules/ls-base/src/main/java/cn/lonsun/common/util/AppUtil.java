package cn.lonsun.common.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.*;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * 工具类<br/>
 * 通用方法，使用频率非常高的方法，如数值类型转换,对象值拷贝
 *
 * @Author taodp
 * @Time 2014年8月16日 下午2:49:20
 */
public class AppUtil {

    /**
     * *
     * 获取随机数 日期 + 随机数 （主要用文件名称）
     *
     * @return
     */
    public static String getRandom() {
        Calendar calendar = new GregorianCalendar();
        StringBuilder buffer = new StringBuilder();
        buffer.append(calendar.get(Calendar.YEAR));
        int month = calendar.get(Calendar.MONTH) + 1;
        int date = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        int millsec = calendar.get(Calendar.MILLISECOND);
        if (month < 10) {
            buffer.append(0);
        }
        buffer.append(month);
        if (date < 10) {
            buffer.append(0);
        }
        buffer.append(date);
        if (hour < 10) {
            buffer.append(0);
        }
        buffer.append(hour);
        if (min < 10) {
            buffer.append(0);
        }
        buffer.append(min);
        if (sec < 10) {
            buffer.append(0);
        }
        buffer.append(sec);
        if (millsec < 100) {
            if (millsec < 10) {
                buffer.append(0);
            }
            buffer.append(0);
        }
        buffer.append(millsec);
        String random = String.valueOf(Math.random());
        random = random.substring(random.length() - 8, random.length());
        buffer.append(random);
        return buffer.toString();

    }

    /**
     * *
     * 是否为空判断
     *
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
     *
     * @param sCheck
     * @return
     */
    public static final boolean isInt(String sCheck) {
        if (isEmpty(sCheck)) {
            return false;
        }
        if (sCheck.substring(0, 1).equals("-")) {
            sCheck = sCheck.substring(1);
        }
        if (!(isNumeric(sCheck))) {
            return false;
        }
        return (Long.parseLong(sCheck) <= 2147483647L);
    }

    /**
     * 判断是否是整数
     *
     * @param sCheck
     * @return
     */
    public static final boolean isNumeric(String sCheck) {
        if (sCheck == null) {
            return false;
        }
        if (sCheck.length() == 0) {
            return false;
        }
        String numStr = "0123456789";
        for (int i = 0; i < sCheck.length(); ++i) {
            if (numStr.indexOf(sCheck.charAt(i)) == -1) {
                return false;
            }
        }

        return true;
    }

    /**
     * 将对象转为整型对象
     *
     * @param obj
     * @return
     */
    public static Integer getInteger(Object obj) {
        if (null == obj) {
            return null;
        } else if ((obj instanceof String)) {
            String data = (obj + "").trim();
            if ("".equals(data)) {
                return null;
            } else if (isInt(data)) {
                return Integer.valueOf(data);
            } else {
                return null;
            }
        } else if ((obj instanceof BigDecimal)) {
            return (((BigDecimal) obj).intValue());
        } else if (isInt(obj + "")) {
            return Integer.valueOf(obj + "");
        } else {
            return null;
        }
    }

    /**
     * 将对象转为整型值
     */
    public static int getint(Object obj) {
        Integer result = getInteger(obj);
        return null == result ? 0 : result.intValue();
    }

    /**
     * *
     * 将对象转为短整型
     *
     * @param obj
     * @return
     */
    public static Short getShort(Object obj) {
        if (null == obj) {
            return null;
        } else if ((obj instanceof String)) {
            String data = (obj + "").trim();
            if ("".equals(data)) {
                return null;
            } else if (isInt(data)) {
                return Short.valueOf(data);
            } else {
                return null;
            }
        } else if ((obj instanceof BigDecimal)) {
            return (((BigDecimal) obj).shortValueExact());
        } else if (isInt(obj + "")) {
            return Short.valueOf(obj + "");
        } else {
            return null;
        }
    }

    /**
     * *
     * 将对象转为长整型
     *
     * @param obj
     * @return
     */
    public static Long getLong(Object obj) {
        if (null == obj) {
            return null;
        } else if ((obj instanceof String)) {
            String data = (obj + "").trim();
            if ("".equals(data)) {
                return null;
            } else if (isInt(data)) {
                return Long.valueOf(data);
            } else {
                return null;
            }
        } else if ((obj instanceof BigDecimal)) {
            return new Long(((BigDecimal) obj).longValue());
        } else if (isInt(obj + "")) {
            return Long.valueOf(obj + "");
        } else if (obj instanceof Long) {
            return (Long) (obj);
        } else {
            return null;
        }
    }

    /**
     * 将对象转为整型值
     */
    public static long getlong(Object obj) {
        Long result = getLong(obj);
        return null == result ? 0 : result.longValue();
    }

    /**
     * *
     * 获取长整型
     *
     * @param obj
     * @return
     */
    public static Double getDouble(Object obj) {
        if (null == obj) {
            return null;
        } else if ((obj instanceof String)) {
            String data = (obj + "").trim();
            if ("".equals(data)) {
                return null;
            } else if (isInt(data)) {
                return Double.valueOf(data);
            } else {
                return null;
            }
        } else if ((obj instanceof BigDecimal)) {
            return new Double(((BigDecimal) obj).longValue());
        } else if (isInt(obj + "")) {
            return Double.valueOf(obj + "");
        } else {
            return null;
        }
    }

    /**
     * *
     *
     * @param obj
     * @return
     */
    public static Float getFloat(Object obj) {
        if (null == obj) {
            return null;
        } else if ((obj instanceof String)) {
            String data = (obj + "").trim();
            if ("".equals(data)) {
                return null;
            } else if (isFloat(data)) {
                return Float.valueOf(data);
            } else {
                return null;
            }
        } else if ((obj instanceof BigDecimal)) {
            return new Float(((BigDecimal) obj).longValue());
        } else if (isFloat(obj + "")) {
            return Float.valueOf(obj + "");
        } else {
            return null;
        }
    }

    /**
     * 判断是否是float数字
     *
     * @param sCheck
     * @return
     */
    public static final boolean isFloat(String sCheck) {
        if (isEmpty(sCheck)) {
            return false;
        }
        if (sCheck.indexOf(".") != -1) {
            int dotPos = sCheck.indexOf(".");
            sCheck = sCheck.substring(0, dotPos) + sCheck.substring(dotPos + 1);
        }
        return isNumeric(sCheck);
    }
    /*
     * 将对象转为指定的字符串
     */

    public static String getValue(Object obj) {
        if (null == obj) {
            return "";
        } else if (obj instanceof String) {
            return (String) obj;
        } else {
            return obj + "";
        }
    }

    /**
     * *
     * 属性拷贝，空值不拷贝
     *
     * @param dest
     * @param orig
     */
    public static void copyPropertiesWithoutEmpty(Object dest, Object orig) {
        final BeanWrapper src = new BeanWrapperImpl(orig);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for(java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (ObjectUtils.isEmpty(srcValue)) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        org.springframework.beans.BeanUtils.copyProperties(orig, dest, emptyNames.toArray(result));
    }

    /**
     * *
     * 属性拷贝
     *
     * @param dest
     * @param orig
     */
    public static void copyProperties(Object dest, Object orig) {
        org.springframework.beans.BeanUtils.copyProperties(orig, dest);
    }

    /**
     * *
     *
     * 属性拷贝
     *
     * @author
     * @param dest
     * @param orig
     * @param ignoreProperties
     */
    public static void copyProperties(Object dest, Object orig, String[] ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(orig, dest, ignoreProperties);
    }

    /**
     * 根据分隔符将字符串转换Long数组
     *
     * @param str 目标字符串
     * @param delimiter	分割符
     * @return
     */
    public static Long[] getLongs(String str, String delimiter) {
        if (null == str || null == delimiter || "".equals(str) || "".equals(delimiter)) {
            return null;
        }
        String strs[] = str.split(delimiter);
        List<Long> tempList = new ArrayList<Long>();
        Long temp = null;
        for (int i = 0; i < strs.length; i++) {
        	temp = getLong(strs[i]);
            if(null!=temp) {
                tempList.add(temp);
            }
        }
        Long[] result = new Long[tempList.size()];
        tempList.toArray(result);
        return result;
    }
    /**
     * 根据分隔符将字符串转换Long数组
     * @param str 目标字符串
     * @param delimiter	分割符
     * @return
     */
    public static String[] getStrings(String str,String delimiter){
    	if(null == str || null == delimiter || "".equals(str) || "".equals(delimiter)) return null;
    	return str.split(delimiter);
    }
   

    /**
     * 获取UUID
     *
     * @Time 2014年8月19日 下午7:32:59
     * @return
     */
    public static String getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * 传入Date类型和需要转换格式返回对应字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String formatTimeToString(Date date, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        if (date != null) {
            return f.format(date);
        }
        return null;
    }

    /**
     * 字符串转换时间的方法，失败后返回null
     *
     * @Time 2014年9月16日 下午3:41:13
     * @param str
     * @param pattern
     * @return
     */
    public static Date formatStringToTime(String str, String pattern) {
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
     *
     * @Time 2014年9月5日 上午11:17:10
     * @return
     */
    public static int getCurrentYear() {
        return getint(Calendar.getInstance().getTime().toString().substring(Calendar.getInstance().getTime().toString().length() - 4));
    }

    /**
     * 获取当前时间所在的年份
     *
     * @Time 2014年9月13日 下午3:20:02
     * @return
     */
    public static int getYearByDate(Date date) {
        return getint(date.toString().substring(date.toString().length() - 4));
    }

    /**
     * 获取当前时间所在年度的周数
     *
     * @Time 2014年9月13日 下午3:08:05
     * @return
     */
    public static int getCurrentWeekOfYear() {
        return getint(Calendar.getInstance().get(Calendar.WEEK_OF_YEAR));
    }

    /**
     * 获取指定时间所在年度的周数
     *
     * @Time 2014年9月13日 下午3:08:30
     * @param date
     * @return
     */
    public static int getWeekOfYearByDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int weekOfYear = getint(c.get(Calendar.WEEK_OF_YEAR));
        //中国化日期修改，星期天作为一周的开始
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            weekOfYear--;
        }
        return weekOfYear;
    }

    /**
     * 获取不重复的ID串的集合
     * @param str id串
     * @param delimiter 分隔符
     * @return Long型的Set集合
     */
    public static Set<Long> getLongSet(String str,String delimiter){
        if (null == str || null == delimiter || "".equals(str) || "".equals(delimiter)) {
            return null;
        }
        String strs[] = str.split(delimiter);
        Set<Long> resultSet = new HashSet<Long>(strs.length);
        Long temp = null;
        for (int i = 0; i < strs.length; i++) {
            temp = getLong(strs[i]);
            if(null != temp){
                resultSet.add(temp);
            }
        }
        return resultSet;
    }

    public static Object transMapTO(Map<String,Object> map,Class<?> valueObjectClass){
        return setFieldValue(map, valueObjectClass);
    }

    public static void transMapTO(Map<String,Object> map,Object obj){
        setFieldValue(map, obj);
    }

    /**
     * 返回由Map的key对属性，value对应值组成的对应
     * @return
     */
    public static Object setFieldValue(Map<String,Object> data,Class<?> setCls){
        Object setObj = null;
        try {
            setObj = setCls.newInstance();
            Class<?>[] setType = null;
            Method[] setMethods = setCls.getMethods();
            int setLen = "set".length();
            String name = null;
            for (Method setMethod : setMethods) {
                name = setMethod.getName();
                if(!name.startsWith("set") || name.length()<=setLen) continue;
                name = name.substring(setLen);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);

                setType = setMethod.getParameterTypes();
                if(null == setType || setType.length!=1) continue;
                if(!getBaseData(setType[0]))continue;

                if (data.containsKey(name)) {
                    setType = setMethod.getParameterTypes();
                    if(null !=setType && setType.length==1){
                        Object objValue = setTypeConversion(setType[0],data.get(name));
                        setMethod.invoke(setObj, objValue);
                    }

                }


            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return setObj;

    }
    
    /**
     * 为对象target设置值，target、values以及unIngoreFields是否按约定传输，工具方法中不再进行验证
     *
     * @param target 待设值的对象
     * @param values key对应target的属性名，value对应属性的值
     * @param assignmentFields 此字段为空，那么map中的所有值都设值进去，如果不为空，那么只设值assignmentFields中的属性值
     * @param dateFormat 匹配的日期类型格式
     */
    public static void setFieldsValue(Object target,Map<String,Object> values,List<String> assignmentFields,String... dateFormat){
    	Class<?> clazz = target.getClass();
    	Field[] fields = clazz.getDeclaredFields();
    	if(fields!=null&&fields.length>0){
    		//是否只设值unIngoreFields中的属性
    		boolean hasUnIngreFields = false;
    		if(assignmentFields!=null&&assignmentFields.size()>0){
    			hasUnIngreFields = true;
			}
    		//待更新属性值容器中的key集合
    		Set<String> keys = values.keySet();
    		for(Field field:fields){
    			String fieldName =field.getName();
    			//判断是否属于指定的更新字段，如果是，那么验证是否在待更新范围内，如果不在，那么跳出此次循环
    			//如果传值容器中不包含此属性的-即keys中不包含fieldName，那么也直接跳出循环
    			if((hasUnIngreFields&&!assignmentFields.contains(fieldName))||!keys.contains(fieldName)){
    				continue;
    			}
    			Object value = values.get(fieldName);
        		try {
        			field.setAccessible(true);
        			//类型转换
        			value = setTypeConversion(field.getType(),value,dateFormat);
    				field.set(target, value);
    			} catch (IllegalArgumentException e) {
    				e.printStackTrace();
    			} catch (IllegalAccessException e) {
    				e.printStackTrace();
    			}
        	}
    	}
    }

    /**
     * 返回由Map的key对属性，value对应值组成的对应
     * @return
     */
    public static void setFieldValue(Map<String,Object> data,Object setObj){
        Class<?> setCls = setObj.getClass();
        try {
            Class<?>[] setType = null;
            Method[] setMethods = setCls.getMethods();
            int setLen = "set".length();
            String name = null;
            for (Method setMethod : setMethods) {
                name = setMethod.getName();
                if(!name.startsWith("set") || name.length()<=setLen) continue;
                name = name.substring(setLen);
                name = name.substring(0, 1).toLowerCase() + name.substring(1);

                setType = setMethod.getParameterTypes();
                if(null == setType || setType.length!=1) continue;
                if(!getBaseData(setType[0]))continue;

                if (data.containsKey(name)) {
                    setType = setMethod.getParameterTypes();
                    if(null !=setType && setType.length==1){
                        Object objValue = setTypeConversion(setType[0],data.get(name));
                        setMethod.invoke(setObj, objValue);
                    }

                }


            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /***
     * 判断 参数是否基本类型可基本类型包装类
     * @param setCls
     * @return
     */
    private static boolean getBaseData(Class<?> setCls){
        //基本类型
        if(setCls.getClass().isPrimitive()){
            return true;
        }else{
            String getType = setCls.getSimpleName();
            return ("String".equals(getType) ||
                    "Integer".equals(getType) ||
                    "Long".equals(getType) ||
                    "Double".equals(getType) ||
                    "BigDecimal".equals(getType) ||
                    "Float".equals(getType) ||
                    "Boolean".equals(getType) ||
                    "Short".equals(getType) ||
                    "Character".equals(getType) ||
                    "String[]".equals(getType) ||
                    "Integer[]".equals(getType) ||
                    "Long[]".equals(getType) ||
                    "Double[]".equals(getType) ||
                    "BigDecimal[]".equals(getType) ||
                    "Float[]".equals(getType) ||
                    "Boolean[]".equals(getType) ||
                    "Short[]".equals(getType) ||
                    "Character[]".equals(getType)
            );
        }

    }

    //强转对应的值类型
    public static Object setTypeConversion(Class<?> setCls,Object value,String... dateFormat) {
        if(isEmpty(value))return null;
        if(setCls.isArray())return value;
        Object obj = null;
        String getType = setCls.getSimpleName();
        if ("String".equals(getType)) {
            obj = getValue(value);
        }else if ("Integer".equals(getType)) {
            obj = getInteger(value);
        }else if ("Long".equals(getType)) {
            obj = getLong(value);
        }else if ("Double".equals(getType)) {
            obj = getDouble(value);
        }else if ("Float".equals(getType)) {
            obj = getFloat(value);
        }else if ("Boolean".equals(getType)) {
            if(null !=value && "1".equals(value+"")){
                obj = Boolean.TRUE;
            }else if(null !=value && "0".equals(value+"")){
                obj = Boolean.FALSE;
            }else{
                obj = Boolean.valueOf(value+"");
            }
        }else if ("Short".equals(getType)) {
            obj = getShort(value);
        }else if ("Character".equals(getType)) {
            obj = Character.valueOf((value + "").charAt(0));
        }else if("Date".equals(getType)){
            if(null !=dateFormat && dateFormat.length > 0) {
                for(String str : dateFormat){
                    try {
                        obj = new SimpleDateFormat(str).parse(value.toString());
                    } catch (Exception e) {}
                }
            }
        } else {
            obj = value;
        }

        return obj;
    }
    
    /**
     * 为对象target设置值，target、values以及unIngoreFields是否按约定传输，工具方法中不再进行验证
     *
     * @param target 待设值的对象
     * @param values key对应target的属性名，value对应属性的值
     * @param assignmentFields 此字段为空，那么map中的所有值都设值进去，如果不为空，那么只设值assignmentFields中的属性值
     */
	public static void copyPropertiesWithAssignmentFields(Object source, Object target, String... assignmentFields)
			throws BeansException {
		Assert.notNull(source, "Source must not be null");
        Assert.notNull(target, "Target must not be null");
		Class<?> actualEditable = target.getClass();
		PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
		List<String> assignmentList = (assignmentFields != null) ? Arrays.asList(assignmentFields) : null;

		for (PropertyDescriptor targetPd : targetPds) {
			Method writeMethod = targetPd.getWriteMethod();
			if (writeMethod != null && (assignmentFields == null || (assignmentList.contains(targetPd.getName())))) {
				PropertyDescriptor sourcePd = BeanUtils.getPropertyDescriptor(source.getClass(), targetPd.getName());
				if (sourcePd != null) {
					Method readMethod = sourcePd.getReadMethod();
					if (readMethod != null &&
							writeMethod.getParameterTypes()[0].isAssignableFrom(readMethod.getReturnType())) {
						try {
							if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
								readMethod.setAccessible(true);
							}
							Object value = readMethod.invoke(source);
							if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
								writeMethod.setAccessible(true);
							}
							writeMethod.invoke(target, value);
						}
						catch (Throwable ex) {
							throw new FatalBeanException(
									"Could not copy property '" + targetPd.getName() + "' from source to target", ex);
						}
					}
				}
			}
		}
	}

    /**
     * 将request参数放入Map中
     * @param request
     * @return
     */
    public static Map<String,Object> parseRequestToMap(HttpServletRequest request) {
        if(null == request) return null;
        Map<String,String[]> map = request.getParameterMap();
        Map<String,Object> resultMap = new HashMap<String, Object>(map.size());
        String[] strArray = null;
        for(String key : map.keySet()){
            strArray = map.get(key);
            if(null != strArray && strArray.length > 0 ){
                resultMap.put(key,strArray[0]);
            }
        }
        return resultMap;
    }


    //转换小写
    public static String lowerCase(String str){
        if(!isEmpty(str)){
            return str.toLowerCase();
        }
        return str;
    }

    /**
     * 将List转换成Map
     * @param list
     *        需要转换的集合
     * @param key
     *        转换成Map时的Key
     * @return
     */
    public static Map<?,?> parseListToMap(List<?> list,String key){
        Map<Object,Object> map = new HashMap<Object, Object>();
        if(null == list) return map;
        if(isEmpty(key)) return map;
        Object keyValue = null;
        for(Object obj : list){
            try {
                key = key.substring(0,1).toUpperCase()+key.substring(1,key.length());
                keyValue = obj.getClass().getMethod("get"+key).invoke(obj);
                map.put(keyValue,obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }

}
