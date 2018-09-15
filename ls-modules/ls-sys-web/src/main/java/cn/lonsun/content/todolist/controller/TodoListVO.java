package cn.lonsun.content.todolist.controller;

/**
 * @author gu.fei
 * @version 2016-1-4 10:55
 */
public class TodoListVO {

    private String typeCode;

    private String typeName;

    private Long count;

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
