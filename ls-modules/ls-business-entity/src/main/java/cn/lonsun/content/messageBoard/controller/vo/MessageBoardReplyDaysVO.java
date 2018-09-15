package cn.lonsun.content.messageBoard.controller.vo;

import cn.lonsun.core.base.entity.AMockEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 查询回复天数
 */
public class MessageBoardReplyDaysVO {

    private Integer replyDays;

    public Integer getReplyDays() {
        return replyDays;
    }

    public void setReplyDays(Integer replyDays) {
        this.replyDays = replyDays;
    }
}
