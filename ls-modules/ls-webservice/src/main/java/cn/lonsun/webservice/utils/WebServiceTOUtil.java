package cn.lonsun.webservice.utils;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.webservice.to.WebServiceTO;
import cn.lonsun.webservice.to.WebServiceTO.ErrorCode;

/**
 * WebService请求异常后填充错误信息
 * 
 * @author xujh
 * @date 2014年11月6日 下午2:35:22
 * @version V1.0
 */
public class WebServiceTOUtil {

    /**
     * 通用返回异常信息处理
     * 
     * @param vo
     * @param e
     */
    public static void setErrorInfo(WebServiceTO vo, Exception e) {
        // 设置请求状态为失败
        vo.setStatus(Integer.valueOf(0));
        // 验证是否是参数错误导致的异常，如果是，那么设置异常错误码为001
        if (e instanceof IllegalArgumentException) {
            vo.setErrorCode(ErrorCode.ArgumentsError.getValue());
            vo.setDescription("系统繁忙，请稍后再试");
        } else if (e instanceof BusinessException) {
            vo.setErrorCode(ErrorCode.SystemError.getValue());
            vo.setDescription(((BusinessException) e).getTipsMessage());
        } else if (e instanceof BaseRunTimeException) {
            vo.setErrorCode(ErrorCode.SystemError.getValue());
            vo.setDescription(((BaseRunTimeException) e).getTipsMessage());
        } else {
            vo.setErrorCode(ErrorCode.SystemError.getValue());
            vo.setDescription("系统繁忙，请稍后再试");
        }
    }
}