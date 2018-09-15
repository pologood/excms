package cn.lonsun.staticcenter.xfyun.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.staticcenter.xfyun.util.XFYYApiUtil;
import cn.lonsun.staticcenter.xfyun.vo.ParamVO;
import cn.lonsun.staticcenter.xfyun.vo.ResultVO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 讯飞语音api
 * Created by huangxx on 2018/7/3.
 */
@Controller
@RequestMapping(value = "/search/xfyy")
public class XFYYApiController extends BaseController {

    /**
     * 转换音频数据
     *
     * @param vo
     * @return
     * @throws Exception
     */
    @RequestMapping("getData")
    @ResponseBody
    public Object getData(ParamVO vo, HttpServletRequest request, HttpServletResponse response) throws Exception {

        MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
        if (AppUtil.isEmpty(multipartRequest)) {
            return ajaxErr("参数错误！");
        }

        MultipartFile file = multipartRequest.getFile("audio");

        if (AppUtil.isEmpty(file) && AppUtil.isEmpty(file.getBytes())) {
            return ajaxErr("未识别的语音内容！");
        }
        if (AppUtil.isEmpty(vo)) {
            return ajaxErr("参数错误");
        }
        if (AppUtil.isEmpty(vo.getEngineType())) {
            vo.setEngineType("sms8k");
        }
        if (AppUtil.isEmpty(vo.getAue())) {
            vo.setAue("raw");
        }
        if (vo.getAue().equals("speex") && AppUtil.isEmpty(vo.getSpeexSize())) {
            return ajaxErr("speex音频帧率，speex音频必传！");
        }

        //构建X-PARAM
        JSONObject json = new JSONObject();
        json.put("engine_type", vo.getEngineType());
        json.put("aue", vo.getAue());
        //得到转换内容
        JSONObject resultJson = XFYYApiUtil.getData(json.toString(), file.getBytes());
        if (null == resultJson) {
            return ajaxErr("语音接口调用失败");
        }
        //返回结果
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(resultJson.getString("code"));
        resultVO.setData(resultJson.getString("data"));
        resultVO.setDesc(resultJson.getString("desc"));
        resultVO.setSid(resultJson.getString("sid"));
        return getObject(resultVO);
    }

}
