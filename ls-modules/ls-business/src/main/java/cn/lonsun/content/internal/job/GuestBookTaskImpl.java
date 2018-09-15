package cn.lonsun.content.internal.job;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.GuestBookEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IGuestBookService;
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

public class GuestBookTaskImpl extends ISchedulerService {
    private static final IScheduleJobService scheduleJobService = SpringContextHolder.getBean(IScheduleJobService.class);
    private static final IGuestBookService guestBookService = SpringContextHolder.getBean(IGuestBookService.class);
    private static final IBaseContentService contentService = SpringContextHolder.getBean(IBaseContentService.class);


    @Override
    public void execute(String json) {
        Long id = Long.parseLong(json);
        GuestBookEO guestBookEO = guestBookService.getEntity(GuestBookEO.class, id);
        if(guestBookEO!=null){
            BaseContentEO contentEO= contentService.getEntity(BaseContentEO.class,guestBookEO.getBaseContentId());
            if(contentEO!=null&&contentEO.getColumnId()!=null){
                try {
                    ColumnTypeConfigVO setting = ModelConfigUtil.getCongfigVO(contentEO.getColumnId(),contentEO.getSiteId());
                    if(setting!=null){
                        SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
                        if (setting.getIsAssess()!=null&&setting.getIsAssess()==1&&setting.getAssessDay() != null) {
                            if ("handled,replyed".contains(guestBookEO.getDealStatus())) {
                                if(StringUtils.isEmpty(guestBookEO.getCommentCode())){
                                    Date startDate = guestBookEO.getReplyDate();
                                    String startTimeStr = simple.format(startDate);
                                    Date startTime = simple.parse(startTimeStr);
                                    int day = setting.getAssessDay();
//                                  Date startTime1 = simple.parse(startTimeStr);
//                                        int addDay=day;
//                                        while(addDay>0){
//                                            startTime1.setDate(startTime1.getDate() + 1);
//                                            if (startTime1.getDay() == 6 || startTime1.getDay() == 0) {
//                                                day++;
//                                            }
//                                            addDay--;
//                                        }
                                        Date endTime=new Date();
                                        endTime.setDate(startTime.getDate() + day);
                                        Date endTime1=new Date();
                                        if(endTime1.after(endTime)){
                                            guestBookEO.setCommentCode("satisfactory");
                                            guestBookService.updateEntity(guestBookEO);
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
