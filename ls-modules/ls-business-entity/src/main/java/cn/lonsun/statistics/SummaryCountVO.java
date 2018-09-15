package cn.lonsun.statistics;

/**
 * 统计数量vo
 * Created by huangxx on 2018/6/4.
 */
public class SummaryCountVO {

    private String queryType;

    private Long todayCreateNums;//今日新增数量

    private Long todayUpdateNums;//今日更新数量

    private Long todayPublishNums;//今日发布数量

    private Long todoNums;//未办数量

    private Long doneNums;//已办数量

    private String url;//跳转链接

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public Long getTodayCreateNums() {
        return todayCreateNums;
    }

    public void setTodayCreateNums(Long todayCreateNums) {
        this.todayCreateNums = todayCreateNums;
    }

    public Long getTodayUpdateNums() {
        return todayUpdateNums;
    }

    public void setTodayUpdateNums(Long todayUpdateNums) {
        this.todayUpdateNums = todayUpdateNums;
    }

    public Long getTodayPublishNums() {
        return todayPublishNums;
    }

    public void setTodayPublishNums(Long todayPublishNums) {
        this.todayPublishNums = todayPublishNums;
    }

    public Long getTodoNums() {
        return todoNums;
    }

    public void setTodoNums(Long todoNums) {
        this.todoNums = todoNums;
    }

    public Long getDoneNums() {
        return doneNums;
    }

    public void setDoneNums(Long doneNums) {
        this.doneNums = doneNums;
    }
}
