package cn.lonsun.webservice.processEngine.enums;

/**
 * Created by lonsun on 2014/12/16.
 */

/***
 * 任务状态
 */
public enum TaskStatus {
    //暂停
    pause,
    //启动
    start,
    //待办
    waitDeal,
    //待阅
    waitRead,
    //已办
    doneDeal,
    //已阅
    doneRead,
    //结束
    end
}
