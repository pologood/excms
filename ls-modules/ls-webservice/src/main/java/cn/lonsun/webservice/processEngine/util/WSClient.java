/*
 * 2014-12-14 <br/>
 * 
 */
package cn.lonsun.webservice.processEngine.util;

import cn.lonsun.webservice.core.WebServiceCaller;

import java.util.List;

public class WSClient {
    /**
     * 返回对象
     * @param code
     * @param params
     * @param clazz
     * @return
     */
    public static Object getObject(String code,final Object[] params,Class clazz){
        Object[] p = params;
         if(null != p && p.length>0){
             for(int i=0;i<params.length;i++){
                 p[i] = params[i];
                 if(isOthObject(p[i])){
                     p[i] = JSONConvertUtil.toJsonString(p[i]);
                 }

             }
         }

        return WebServiceCaller.getSimpleObject(code,p,clazz);
    }

    public static Object[] getObject(final Object[] params){
        Object[] p = params;
        if(null != p && p.length>0){
            for(int i=0;i<params.length;i++){
                p[i] = params[i];
                if(isOthObject(p[i])){
                    p[i] = JSONConvertUtil.toJsonString(p[i]);
                }
            }
        }
        return p;
    }
    /***
     *  fa
     * @param code
     * @param params
     * @param clazz
     * @return
     */
    public static Object getList(String code,final Object[] params,Class clazz){
        Object[] p = params;
        if(null != p && p.length>0){
            for(int i=0;i<params.length;i++){
                p[i] = params[i];
                if(isOthObject(p[i])){
                    p[i] = JSONConvertUtil.toJsonString(p[i]);
                }
            }
        }
        return WebServiceCaller.getList(code,p,clazz);
    }

    public static boolean isOthObject(Object object){
        if(null == object) return false;
        String clazz = object.getClass().getSimpleName();
        return (clazz.equals("Integer") ||
                clazz.equals("Integer[]") ||
                clazz.equals("Long") ||
                clazz.equals("Long[]") ||
                clazz.equals("Double") ||
                clazz.equals("Double[]") ||
                clazz.equals("Float") ||
                clazz.equals("Float[]") ||
                clazz.equals("Boolean") ||
                clazz.equals("Boolean[]")
                )?false:(!(object instanceof String));

    }

}
