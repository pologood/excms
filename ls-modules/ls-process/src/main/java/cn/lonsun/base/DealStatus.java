package cn.lonsun.base;

/**
 * 办理状态
 * Created by zhusy on 2016-8-3.
 */
public enum DealStatus {

    Dealing(0), //办理中
    Terminate(1), //终止
    Completed(2);//办结

    private Integer value;

    private DealStatus(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
