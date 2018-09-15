package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.statistics.GuestListVO;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *该年度个单位回复信件的总量排行 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-7-13<br/>
 */
@Component
public class GuestBookReplyListBeanService extends AbstractBeanService {
    @Autowired
    private IGuestBookService guestBookService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context= ContextHolder.getContext();
        Long siteId=context.getSiteId();
        String columnIds = paramObj.getString("columnIds");
        String organIds = paramObj.getString("organIds");
        Integer num=paramObj.getInteger("num");
        List<GuestListVO> list= guestBookService.replyOrderByOrgan(columnIds,siteId,organIds,num);
        return list;
    }
}
