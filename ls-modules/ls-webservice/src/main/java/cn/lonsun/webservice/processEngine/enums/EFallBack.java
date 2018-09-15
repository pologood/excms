package cn.lonsun.webservice.processEngine.enums;

/**
 * 回退类型
 * notAllow（不允许）, beforeStep（上一步）, allHistoryStep（所有历史节点）
 *
 * @author Lee
 * @date 2014-9-11
 */
public enum EFallBack {
    /**不允许回退*/
    notAllow,
    /**向前回退一步*/
    beforeStep,
    /**向前回退任何一步*/
    allHistoryStep
}
