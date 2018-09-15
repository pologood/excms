package cn.lonsun.lottery.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * Created by lonsun on 2017-1-11.
 */
public class QuestionsQueryVO extends PageQueryVO {
     private String  searchKey;
    private String  answerKey;
    private String date;
    private String name;
    private String phone;


    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getAnswerKey() {
        return answerKey;
    }

    public void setAnswerKey(String answerKey) {
        this.answerKey = answerKey;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
