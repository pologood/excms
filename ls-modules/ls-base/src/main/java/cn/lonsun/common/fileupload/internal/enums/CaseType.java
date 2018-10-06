
package cn.lonsun.common.fileupload.internal.enums;

/**
 *文件所属实体类型
 */
public enum CaseType {

    /**
     * 发文
     */
    SendDocument,

    /**
     * 公文
     */
    Document,
    /**
     * 收文请求反馈附件
     */
    AdviceResponseEO,

    /**
     * 收文呈批标签附件
     */
    AdvicesGatherEO,
    /**
     * 收文办结反馈附件
     */
    RecSituationEO,

    /**
     * 收文办结反馈附件
     */
    bbs,
    /**
     * 内容管理
     */
    ContentEO,
    
    /**
     * 资料管理中心
     */
    dataSharing,
    
    /**
     * 会前材料
     */
    Meeting_Before,
    /**
     * 会后材料
     */
    Meeting_After,
    
    /**
     * 议题
     */
    MeetingIssue,


    /*任务分解督办*/
    TaskSplit
}
