package cn.lonsun.weibo;

import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.service.ISinaWeiboService;
import cn.lonsun.weibo.service.ITencentWeiboService;
import cn.lonsun.weibo.service.IWeiboRadioContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gu.fei
 * @version 2016-6-12 14:57
 */
@Controller
@RequestMapping(value = "/weibo/radio")
public class WeiboRadioController {

    private static final String FILE_BASE = "/weibo";

    @Autowired
    private IWeiboRadioContentService weiboRadioContentService;

    @Autowired
    private ISinaWeiboService sinaWeiboService;

    @Autowired
    private ITencentWeiboService tencentWeiboService;

    @RequestMapping(value = "/index")
    public String index() {
        return FILE_BASE + "/pend_weibo";
    }

    @ResponseBody
    @RequestMapping(value = "/getPagePendWeibo")
    public Object getPagePendWeibo(ParamDto dto) {
        dto.setType(WeiboRadioContentEO.Type.Sina.toString());
        return weiboRadioContentService.getPageEOs(dto);
    }

    @ResponseBody
    @RequestMapping(value = "/publishWeibo")
    public Object publishWeibo(Long id) {
        WeiboRadioContentEO radio = weiboRadioContentService.getEntity(WeiboRadioContentEO.class,id);
        if(null != radio) {
            if(radio.getType().equals(WeiboRadioContentEO.Type.Sina.toString())) {
                SinaWeiboContentVO sina = new SinaWeiboContentVO();
                sina.setText(radio.getContent());
                sina.setOriginalPic(radio.getPicUrl());
                try {
                    sinaWeiboService.publishWeibo(sina);
                } catch (Exception e) {
                    return ResponseData.fail("发布新浪微博失败!");
                }
            } else {
                try {
                    tencentWeiboService.publishWeibo(radio.getContent());
                } catch (Exception e) {
                    return ResponseData.fail("发布腾讯微博失败!");
                }
            }
        }

        SysLog.log("发布微博>> 发布人：" + LoginPersonUtil.getUserName() + ",单位：" + LoginPersonUtil.getUnitName(), "WeiboRadioContentEO", CmsLogEO.Operation.Update.toString());
        return ResponseData.success("发布微博成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/batchDel")
    public Object batchDel(@RequestParam(value="ids[]",required=false) Long[] ids) {
        weiboRadioContentService.batchDel(ids);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping(value = "/batchPublish")
    public Object batchPublish(@RequestParam(value="ids[]",required=false) Long[] ids) {
        try {
            weiboRadioContentService.batchPublish(ids);
        } catch (Exception e) {
            return ResponseData.fail("发布失败!");
        }
        SysLog.log("批量发布微博>> 发布人：" + LoginPersonUtil.getUserName() + ",单位：" + LoginPersonUtil.getUnitName(), "WeiboRadioContentEO", CmsLogEO.Operation.Update.toString());
        return ResponseData.success("发布成功!");
    }
}
