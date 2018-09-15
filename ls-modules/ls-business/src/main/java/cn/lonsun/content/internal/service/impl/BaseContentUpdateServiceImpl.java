package cn.lonsun.content.internal.service.impl;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.dao.IBaseContentUpdateDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.BaseContentUpdateEO;
import cn.lonsun.content.internal.service.IBaseContentUpdateService;
import cn.lonsun.content.vo.BaseContentUpdateQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuk on 2017/6/30.
 */
@Service
public class BaseContentUpdateServiceImpl extends BaseService<BaseContentUpdateEO> implements IBaseContentUpdateService {
    @Resource
    private TaskExecutor taskExecutor;
    @Resource
    private IBaseContentUpdateDao baseContentUpdateDao;

    private void processResultList(List<?> dataList) {
        if (null != dataList && !dataList.isEmpty()) {
            for (Object o : dataList) {
                BaseContentUpdateEO update = (BaseContentUpdateEO) o;
                update.setColumnName(ColumnUtil.getColumnName(update.getColumnId(), update.getSiteId()));
                update.setWarningTypeName(Enum.valueOf(BaseContentUpdateEO.WarningType.class, update.getWarningType()).getTypeName());
            }
        }
    }

    @Override
    public Pagination getPagination(BaseContentUpdateQueryVO queryVO) {
        Pagination p = baseContentUpdateDao.getPagination(queryVO);
        this.processResultList(p.getData());
        return p;
    }

    @Override
    public int getCountByColumnId(BaseContentUpdateQueryVO queryVO) {
        return baseContentUpdateDao.getCountByColumnId(queryVO);
    }


    @Override
    public void export(BaseContentUpdateQueryVO queryVO, HttpServletResponse response) {
        List<BaseContentUpdateEO> list = baseContentUpdateDao.getList(queryVO);
        this.processResultList(list);
        String[] titles = new String[]{"栏目名称", "警示类型", "最后更新日期", "警示消息"};
        List<String[]> rowList = new ArrayList<String[]>();
        if (null != list && !list.isEmpty()) {
            for (BaseContentUpdateEO update : list) {
                String[] row = new String[5];
                row[0] = update.getColumnName();
                row[1] = update.getWarningTypeName();
                row[2] = DateFormatUtils.format(update.getLastPublishDate(), "yyyy-MM-dd HH:mm:ss");
                row[3] = update.getMessage();
                rowList.add(row);
            }
        }

        // 导出
        String name = "新闻栏目更新预警";

        if(queryVO.getSiteId()!=null){
            IndicatorEO siteInfo = CacheHandler.getEntity(IndicatorEO.class,queryVO.getSiteId());
            name = siteInfo.getName() + name;
        }
        try {
            CSVUtils.download(name, titles, rowList, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessageByCurrentUser(final Long userId) {
        final Long siteId = LoginPersonUtil.getSiteId();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                HibernateSessionUtil.execute(new HibernateHandler<String>() {
                    @Override
                    public String execute() throws Throwable {
                        BaseContentUpdateQueryVO queryVO = new BaseContentUpdateQueryVO();
                        queryVO.setUserId(userId);
                        List<BaseContentUpdateEO> list = baseContentUpdateDao.getList(queryVO);
                        if (null != list && !list.isEmpty()) {
                            for (BaseContentUpdateEO eo : list) {
                                MessageSystemEO message = new MessageSystemEO();
                                message.setSiteId(siteId);
                                message.setColumnId(eo.getColumnId());
                                message.setMessageType(MessageSystemEO.TIP);
                                message.setModeCode(BaseContentEO.TypeCode.articleNews.toString());
                                message.setRecUserIds(userId.toString());
                                message.setResourceId(eo.getId());
                                message.setTitle(eo.getMessage());
                                message.setContent(eo.getMessage());
                                message.setMessageStatus(MessageSystemEO.MessageStatus.warning.toString());
                                message.setData(eo);//数据存入消息中
                                MessageSender.sendMessage(message);
                            }
                        }
                        return null;
                    }

                    @Override
                    public String complete(String result, Throwable exception) {
                        if (null != exception) {
                            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目更新预警发送消息失败！");
                        }
                        return result;
                    }
                });
            }
        });
    }
}
