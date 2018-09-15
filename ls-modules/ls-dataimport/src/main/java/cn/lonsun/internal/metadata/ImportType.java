package cn.lonsun.internal.metadata;

/**
 * 导入类型,
 * @author zhongjun
 */
public enum ImportType {
    all("全量导入"),
    add("增量导入"),
    /**验重操作耗时较长*/
    update("不重复导入"),
    /**错误数据导入*/
    appoint("导入指定数据")
    ;

    @Override
    public String toString() {
        return this.name();
    }

    ImportType(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }

}
