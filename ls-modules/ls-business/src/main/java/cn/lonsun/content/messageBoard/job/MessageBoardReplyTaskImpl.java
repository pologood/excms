package cn.lonsun.content.messageBoard.job;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardReplyEO;
import cn.lonsun.content.messageBoard.service.IMessageBoardReplyService;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.IScheduleJobService;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.site.contentModel.vo.ColumnTypeConfigVO;
import cn.lonsun.util.ModelConfigUtil;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-23<br/>
 */

public class MessageBoardReplyTaskImpl extends ISchedulerService {
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);
    private static final IMessageBoardReplyService replyService = SpringContextHolder.getBean(IMessageBoardReplyService.class);
    private static final IMessageBoardService messageBoardService = SpringContextHolder.getBean(IMessageBoardService.class);
    private static final IBaseContentService contentService = SpringContextHolder.getBean(IBaseContentService.class);


    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        MessageBoardReplyEO replyEO = replyService.getEntity(MessageBoardReplyEO.class, id);
        if(replyEO!=null){
            MessageBoardEO messageBoardEO= messageBoardService.getEntity(MessageBoardEO.class,replyEO.getMessageBoardId());
            if(messageBoardEO!=null) {
                BaseContentEO contentEO = contentService.getEntity(BaseContentEO.class, messageBoardEO.getBaseContentId());
            if(contentEO!=null&&contentEO.getColumnId()!=null) {
                try {
                    ColumnTypeConfigVO setting = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(), contentEO.getSiteId());
                    if (setting != null) {
                        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                        if (setting.getIsAssess() != null && setting.getIsAssess() == 1 && setting.getAssessDay() != null) {
                            if ("handled,replyed".contains(replyEO.getDealStatus())) {
                                if (StringUtils.isEmpty(replyEO.getCommentCode())) {
                                    Date startDate = replyEO.getCreateDate();
                                    String startTimeStr = simple.format(startDate);
                                    Date startTime = simple.parse(startTimeStr);
                                    int day = setting.getAssessDay();
                                    Date endTime = new Date();
                                    endTime.setDate(startTime.getDate() + day);
                                    Date endTime1 = new Date();
                                    if (endTime1.after(endTime)) {
                                        replyEO.setCommentCode("satisfactory");
                                        replyService.updateEntity(replyEO);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            }
        }

    }
}
