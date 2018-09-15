package cn.lonsun.staticcenter.generate.tag.impl.guestbook;

import cn.lonsun.statistics.GuestListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 留言满意度排行<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-8<br/>
 */
@Component
public class GuestBookRankListBeanService  extends AbstractBeanService {
    @Autowired
    private IGuestBookService guestBookService;


    @Override
    public Object getObject(JSONObject paramObj) {
        Context context= ContextHolder.getContext();
        Long siteId=context.getSiteId();
        String columnIds = paramObj.getString("columnIds");
        Integer isPublish=paramObj.getInteger("isPublish");
        List<GuestListVO> list= guestBookService.replyOKRank(siteId,columnIds,isPublish);
        return list;
    }
}
