package cn.lonsun.core.base.controller;

import cn.lonsun.core.base.util.CSRFTokenManager;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.AjaxObj;
import cn.lonsun.core.util.ResultVO;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.core.util.ThreadUtil.LocalParamsKey;
import cn.lonsun.core.util.TipsMode;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

/**
 * spring控制器基类
 *
 * @author xujh dzl
 * @version V1.0
 * @date 2014年10月8日 下午4:38:18
 */
public abstract class BaseController {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static class callback {
        private String function;
        private Object value;

        public callback(String function, Object value) {
            super();
            this.function = function;
            this.value = value;
        }

        public String getFunction() {
            return function;
        }

        public void setFunction(String function) {
            this.function = function;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    @ModelAttribute
    public void modelMap(HttpServletRequest request, ModelMap modelMap) {
        modelMap.put("sessionId", request.getSession().getId());
        modelMap.put("person", JSON.toJSONString(request.getSession().getAttribute("person")));
    }


    /**
     * 下载classPath路径下的文件
     * @param response
     * @param filePath 相对classPath路径 ps: /importtmpl/easyerr.xlsx
     * @param displayFileName 显示的文件名， is null show filePath
     * @author zhongjun
     */
    protected void downloadClassPathFile(HttpServletResponse response, String filePath, String displayFileName){
        java.io.BufferedInputStream bis = null;
        java.io.BufferedOutputStream bos = null;
        if(StringUtils.isEmpty(displayFileName)){
            displayFileName = filePath;
        }
        try {
            //设置这个类型让浏览器自动识别文件类型
            response.setContentType("multipart/form-data;");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(displayFileName, "UTF-8"));
            if(StringUtils.isEmpty(filePath)){
                response.getWriter().write(JSONObject.toJSONString(ajaxErr("文件不存在")));
                return;
            }
            bos = new BufferedOutputStream(response.getOutputStream());
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
            bis = new BufferedInputStream(is);
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("文件下载失败,错误信息{}", e.getMessage());
        } finally {
            if (bis != null){
                try {
                    bis.close();
                } catch (Exception e) {
                    logger.error("文件下载流关闭失败,错误信息{}", e.getMessage());
                }
            }
            if (bos != null){
                try {
                    bos.close();
                } catch (Exception e) {
                    logger.error("文件下载流关闭失败,错误信息{}", e.getMessage());
                }
            }
        }
    }

    /**
     * 获取返回值-new
     *
     * @return
     */
    public Object getObject() {
        ResultVO result = new ResultVO();
        Object object = null;
        Object tempObj = null;
        Integer dataFlag = ThreadUtil.getInteger(LocalParamsKey.DataFlag);
        String callback = ThreadUtil.getString(LocalParamsKey.Callback);
        if (dataFlag != null && dataFlag != 0) {
            tempObj = null;
        } else {
            tempObj = result;
        }
        if (!StringUtils.isEmpty(callback)) {
            object = JSON.toJSON(new callback(callback, tempObj));
        } else {
            object = tempObj;
        }
        return object;
    }

    /**
     * 获取返回值-new
     *
     * @param value
     * @return
     */
    public Object getObject(Object value) {
        ResultVO result = new ResultVO();
        if (value != null) {
            result.setData(value);
        }
        Object object = null;
        Object tempObj = null;
        Integer dataFlag = ThreadUtil.getInteger(LocalParamsKey.DataFlag);
        String callback = ThreadUtil.getString(LocalParamsKey.Callback);
        if (dataFlag != null && dataFlag != 0) {
            tempObj = value;
        } else {
            tempObj = result;
        }
        if (!StringUtils.isEmpty(callback)) {
            object = JSON.toJSON(new callback(callback, tempObj));
        } else {
            object = tempObj;
        }
        return object;
    }

    /**
     * 用于检查jsr303参数校验结果的方法， 如果参数校验失败，自动抛出异常
     *
     * @param result
     * @Time 2014年8月19日 下午5:57:59
     */
    protected void checkBindResult(BindingResult result) {
        if (result != null && !result.getAllErrors().isEmpty()) {
            ObjectError error = result.getAllErrors().get(0);
            throw new BaseRunTimeException(TipsMode.Message.toString(), error.getDefaultMessage());
        }
    }

    // edit by dzl 2014.09.25

    /**
     * 简化的错误输出方法: 参数错误<br/>
     *
     * @return {"status":0,"desc":"参数错误","data":null}
     */
    protected AjaxObj ajaxParamsErr() {
        return AjaxObj.Err("参数错误");
    }

    /**
     * 简化的错误输出方法: 参数错误
     *
     * @param paramName
     *            参数名称,用于构造错误信息,如: id 则输出显示为:id参数错误
     * @return {"status":0,"desc":"id参数错误","data":null}
     */
    protected AjaxObj ajaxParamsErr(String paramName) {
        return AjaxObj.Err(paramName + "参数错误");
    }

    /**
     * 简化的错误输出方法: 记录不存在
     *
     * @return {"status":0,"desc":"记录不存在","data":null}
     */
    protected AjaxObj ajaxNoExistsErr() {
        return AjaxObj.Err("记录不存在");
    }

    /**
     * 输出标准的错误信息对象JSON字符串<br/>
     * 如: {"status":0,"desc":"错误信息","data":null}
     *
     * @param errMsg
     *            错误信息
     * @return {"status":0,"desc":"错误信息","data":null}
     */
    protected AjaxObj ajaxErr(String errMsg) {
        return AjaxObj.Err(errMsg);
    }

    /**
     * 输出标准的错误信息对象JSON字符串<br/>
     *
     * @param errMsg
     *            错误信息
     * @param errCode
     *            错误代码
     * @return {"status":0,"desc":"错误信息","data":错误代码}
     */
    protected AjaxObj ajaxErr(String errMsg, int errCode) {
        return new AjaxObj(0, errMsg, errCode);
    }

    /**
     * 输出标准的成功信息对象JSON字符串<br/>
     * 如: {"status":1,"desc":"操作成功","data":null}
     *
     * @return {"status":1,"desc":"操作成功","data":null}
     */
    protected AjaxObj ajaxOk() {
        return AjaxObj.Ok();
    }

    /**
     * 将data数据对象输出标准的成功信息对象JSON字符串<br/>
     *
     * @param data
     *            要输出的数据对象
     * @return {"status":1,"desc":"操作成功","data":data}
     */
    protected AjaxObj ajaxOk(Object data) {
        return AjaxObj.Ok(data);
    }

    /**
     * 验证ajax提交中的headers中的__RequestVerificationToken值与session中的token是否不致
     * <p>
     * var headers = {};
     * </p>
     * <p>
     * headers['__RequestVerificationToken'] = $("#CSRFToken").val();
     * </p>
     * <p>
     * $.ajax({ type: "POST",headers: headers,cache: false,...});
     * </p>
     *
     * @param request
     * @return
     */
    protected boolean isValidCsrfHeaderToken(HttpServletRequest request) {
        String token1 = request.getHeader("__RequestVerificationToken");
        String token2 =
                request.getSession().getAttribute(CSRFTokenManager.CSRF_TOKEN4SESSION_ATTRNAME) == null ? null : request.getSession()
                        .getAttribute(CSRFTokenManager.CSRF_TOKEN4SESSION_ATTRNAME).toString();
        if (token1 == null || token2 == null || !token1.equals(token2)) {
            return false;
        }
        return true;
    }
}