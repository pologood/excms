/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.lonsun.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dzl
 */
public class StrUtil {
    // List转换String，String转换List edit by dzl 2014.10.1
    private static final String SEP1 = ",";
    private static final String SEP2 = "|";
    private static final String SEP3 = "=";
    
     /** 
     * List转换String 
     * @param list  需要转换的List ,默认分隔符为逗号(,)
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list) {
         return ListToString(list,",");
    }
    /** 
     * List转换String 
     * @param list  需要转换的List 
     * @param sep  分隔符
     * @return String转换后的字符串
     */
    public static String ListToString(List<?> list,String sep) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == null || list.get(i) == "") {
                    continue;
                }
                    // 如果值是list类型则调用自己  
                if (list.get(i) instanceof List) {
                    sb.append(ListToString((List<?>) list.get(i),sep));
                    sb.append(sep);
                } else if (list.get(i) instanceof Map) {
                    sb.append(MapToString((Map<?, ?>) list.get(i)));
                    sb.append(sep);
                } else {
                    sb.append(list.get(i));
                    sb.append(sep);
                }
            }
        }
        String str = sb.toString();
        if(str.endsWith(sep))str=str.substring(0, str.length()-1);
        return str;
    }
    /**
     * Map转换String
     * @param map  :需要转换的Map
     * @return String转换后的字符串
     */
    public static String MapToString(Map<?, ?> map) {
        StringBuilder sb = new StringBuilder();
            // 遍历map  
        for (Object obj : map.keySet()) {
            if (obj == null) {
                continue;
            }
            Object key = obj;
            Object value = map.get(key);
            if (value instanceof List<?>) {
                sb.append(key.toString() + SEP1 + ListToString((List<?>) value));
                sb.append(SEP2);
            } else if (value instanceof Map<?, ?>) {
                sb.append(key.toString() + SEP1
                        + MapToString((Map<?, ?>) value));
                sb.append(SEP2);
            } else {
                sb.append(key.toString() + SEP3 + value.toString());
                sb.append(SEP2);
            }
        }
        return "M" + sb.toString();
    }
    /**
     * String转换Map 
     * @return Map<String, Object>
     */
    public static Map<String, Object> StringToMap(String mapText) {
        if (mapText == null || mapText.equals("")) {
            return null;
        }
        mapText = mapText.substring(1);
        mapText = mapText;
        Map<String, Object> map = new HashMap<String, Object>();
        String[] text = mapText.split("\\" + SEP2); // 转换为数组  
        for (String str : text) {
            String[] keyText = str.split(SEP3); // 转换key与value的数组  
            if (keyText.length < 1) {
                continue;
            }
            String key = keyText[0]; // key  
            String value = keyText[1]; // value  
            if (value.charAt(0) == 'M') {
                Map<?, ?> map1 = StringToMap(value);
                map.put(key, map1);
            } else if (value.charAt(0) == 'L') {
                List<?> list = StringToList(value);
                map.put(key, list);
            } else {
                map.put(key, value);
            }
        }
        return map;
    }
    /** 
     * String转换List
     * @param listText 需要转换的文本
     * @return List<?>     
     */
    public static List<?> StringToList(String listText){
        return StringToList(listText,",");
    }
    
    public static List<Long> StringToListLong(String listText){
        List<String> list = (List<String>)StringToList(listText);
        List<Long> listLong = new ArrayList<Long>();
        if(list.isEmpty())return listLong;
        for(String el:list){
            listLong.add(Long.valueOf(el));
        }
        return listLong;
    }

    public static List<Integer> StringToListInteger(String listText) {
        List<String> list = (List<String>) StringToList(listText);
        List<Integer> listInteger = new ArrayList<Integer>();
        if(list.isEmpty())return listInteger;
        for (String el : list) {
            listInteger.add(Integer.valueOf(el));
        }
        return listInteger;
    }
    /** 
     * String转换List
     * @param listText 需要转换的文本
     * @param sep  分隔符
     * @return List<?>     
     */
    public static List<?> StringToList(String listText,String sep) {
        if (listText == null || listText.equals("")) {
            return null;
        }
        //listText = listText.substring(1);
        //listText = listText;
        List<Object> list = new ArrayList<Object>();
        String[] text = listText.split(sep);
        for (String str : text) {
            if (str.charAt(0) == 'M') {
                Map<?, ?> map = StringToMap(str);
                list.add(map);
            } else if (str.charAt(0) == 'L') {
                List<?> lists = StringToList(str,sep);
                list.add(lists);
            } else {
                list.add(str);
            }
        }
        return list;
    }

    private static String htmlEncode(char c) {
        switch (c) {
            case '&':
                return "&amp;";
            case '<':
                return "&lt;";
            case '>':
                return "&gt;";
            case '"':
                return "&quot;";
            case ' ':
                return "&nbsp;";
            default:
                return c + "";
        }
    }

    /**
    对传入的字符串str进行Html encode转换
    */
    public static String htmlEncode(String str) {
        if (str == null || str.trim().equals("")) return str;
        StringBuilder encodeStrBuilder = new StringBuilder();
        for (int i = 0, len = str.length(); i < len; i++) {
            encodeStrBuilder.append(htmlEncode(str.charAt(i)));
        }
        return encodeStrBuilder.toString();
    }
}
