package cn.lonsun.sms.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.sms.internal.service.ISendSmsService;
import cn.lonsun.sms.vo.SmsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhangchao on 2016/9/19.
 */
@Controller
@RequestMapping(value = "sendSms", produces = { "application/json;charset=UTF-8" })
public class SendSmsController extends BaseController {

    @Autowired
    private ISendSmsService sendSmsService;

    @RequestMapping("list")
    public String index(ModelMap map) {
//        Boolean isSend = SmsHttpClient.sendSmsUtil("15156698935","龙讯科技测试数据","543567");
        return "/sms/sms_list";
    }

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(SmsQueryVO query) {
        Pagination page = sendSmsService.getPage(query);
        return getObject(page);
    }
}
