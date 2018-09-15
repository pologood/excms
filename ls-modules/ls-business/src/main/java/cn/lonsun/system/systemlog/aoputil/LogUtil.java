package cn.lonsun.system.systemlog.aoputil;

import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.system.systemlog.internal.service.ICmsLogService;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.aspectj.lang.JoinPoint;

import java.util.Date;

/**
 * @author Hewbing
 * @ClassName: LogUtil
 * @Description: 系统操作日志管理
 * @date 2015年10月8日 下午2:05:26
 */
public class LogUtil {
    /**
     * @param joinpoint
     * @param returnObj
     * @Description 系统操作日志
     * @author Hewbing
     * @date 2015年10月8日 下午2:06:06
     */
    public void recLog(JoinPoint joinpoint, Object returnObj) {
        ICmsLogService cmsLogService = SpringContextHolder.getBean("cmsLogService");
        String logArgs = "";
        String orp = "";
        String method = joinpoint.getSignature().getName();
        if (method.startsWith("save")) {
            logArgs += "操作类型：保存操作";
            orp = CmsLogEO.Operation.Add.toString();
        } else if (method.startsWith("update")) {
            logArgs += "操作类型：修改操作";
            orp = CmsLogEO.Operation.Update.toString();
        } else if (method.startsWith("delete")) {
            logArgs += "操作类型：删除操作";
            orp = CmsLogEO.Operation.Delete.toString();
        }
        logArgs += "，参数：";
        Object[] obj = joinpoint.getArgs();
        //	for(int i=0;i<obj.length;i++){
        JsonConfig jsonConfig = new JsonConfig();
        jsonConfig.registerJsonValueProcessor(Date.class, new JsonDateValueProcessor());
        //logArgs+=Jacksons.json().fromObjectToJson(obj[i])+"，";
        logArgs += JSONArray.fromObject(obj, jsonConfig) + "，";
        //	}
        logArgs += "操作返回参数：" + (returnObj == null ? "无返回值" : returnObj);
        //logArgs=logArgs.replaceAll("\\s*","");
        logArgs = logArgs.replaceAll(",", "，");
        System.out.println(logArgs);
        cmsLogService.recLog(logArgs, "EO", orp);
    }

    public void catchException(JoinPoint joinpoint, Object throwEx) {
        System.err.println("错误信息：" + throwEx);
    }
}
