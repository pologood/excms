package cn.lonsun.internal.metadata;

/**
 * 导入状态
 * @author zhongjun
 */
public enum ImportState {
    notImport("未导入"),
    start("开始导入"),
    queryOldData("查询老数据"),
    saveNew("保存到新库"),
    success("导入成功"),
    ;

    @Override
    public String toString() {
        return this.name();
    }

    ImportState(String text) {
        this.text = text;
    }

    private String text;

    public String getText() {
        return text;
    }
}
