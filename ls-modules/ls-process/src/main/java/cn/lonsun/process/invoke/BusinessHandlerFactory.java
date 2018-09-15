
package cn.lonsun.process.invoke;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.core.util.TipsMode;

/**
 *
 * 业务处理工厂类
 *@date 2015-1-9 10:14  <br/>
 *@author zhusy  <br/>
 *@version v1.0  <br/>
 */
public class BusinessHandlerFactory {

    public static IBusinessHandler getBusinessHandlerInstance(String moduleCode){
        if(AppUtil.isEmpty(moduleCode)){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"流程引擎编码为空");
        }
        return SpringContextHolder.getBean("exFormHandler");
    }

}
