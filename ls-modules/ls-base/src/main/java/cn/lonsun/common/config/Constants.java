package cn.lonsun.common.config;

import javax.servlet.ServletContext;

public class Constants {
    /**web-app物理路径*/
    public static String WEBAPP_PHYSICALPATH = System.getProperty("dir");
    /**web-app上下文名称*/
    public static String WEBAPP_CONTEXT="/";
    @SuppressWarnings("unused")
    private Constants(){}
    
   
    public Constants(ServletContext servletContext){
        WEBAPP_PHYSICALPATH = servletContext.getRealPath("");
    }
}
