package cn.lonsun.site.words;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.site.words.internal.entity.ContentCheckEO;
import cn.lonsun.site.words.internal.entity.vo.WordsPageVO;
import cn.lonsun.site.words.internal.service.IContentCheckService;
import cn.lonsun.monitor.words.internal.util.Type;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.LoginPersonUtil;
import net.sf.json.JSONArray;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author gu.fei
 * @version 2015-12-31 11:12
 */
@Controller
@RequestMapping("/content/check")
public class ContentCheckController extends BaseController {

    @Autowired
    private IContentCheckService contentCheckService;

    @Autowired
    private TaskExecutor taskExecutor;

    @ResponseBody
    @RequestMapping("/getPageEO")
    public Object getPageEO(WordsPageVO vo) {
        Pagination page = contentCheckService.getPageEO(vo);
        return page;
    }

    @ResponseBody
    @RequestMapping("/checkContent")
    public Object checkContent(final String checkType) {
        final Long siteId = LoginPersonUtil.getSiteId();
        final Long userId = LoginPersonUtil.getUserId();
        final String link = checkType.equals(Type.SENSITIVE.toString())?"/words/sensitive/checkAll":"/words/easyerr/checkAll";
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setTitle("词汇检索!");
                eo.setLink(link);
                eo.setModeCode("words");
                eo.setRecUserIds(userId + "");
                // 绑定session至当前线程中
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                try {
                    contentCheckService.checkContent(checkType, siteId);
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                    eo.setContent(checkType.equals(Type.SENSITIVE.toString())?"完成敏感词检测!":"完成易错词检测!");
                } catch (Exception e) {
                    e.printStackTrace();
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                    eo.setContent(checkType.equals(Type.SENSITIVE.toString()) ? "敏感词检测失败!" : "易错词检测失败!");
                }
                // 关闭session
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
                MessageSender.sendMessage(eo);
            }
        });
        return ResponseData.success("检测中!");
    }

    @ResponseBody
    @RequestMapping("/replace")
    public Object replace(String eos) {
        try {
            JSONArray jsonArray = JSONArray.fromObject(eos);
            List<ContentCheckEO> checkEOs = (List<ContentCheckEO>) JSONArray.toCollection(jsonArray, ContentCheckEO.class);
            contentCheckService.replace(checkEOs);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseData.fail("替换失败");
        }
        return ResponseData.success("替换成功!");
    }
}
