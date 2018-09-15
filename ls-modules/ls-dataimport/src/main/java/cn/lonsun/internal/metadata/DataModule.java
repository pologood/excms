package cn.lonsun.internal.metadata;

/**
 * 数据分类
 * @author zhongjun
 */
public enum DataModule {
    Organ("单位部门"),
    User("系统用户"),
    Member("网站会员"),

    Column("栏目"),
    WorkGuide("办事指南"),
    VideoNews("视频新闻"),
    Survey("调查"),
    PictureNews("图片新闻"),
    MessageBoard("留言信箱"),
    InterviewInfo("在线访谈"),
    ArticleNews("文字列表"),
    CollectInfo("民意征集"),

    PublicUnit("信息公开-公开单位"),
    PublicContent("信息公开-主动公开"),
    PublicAnnualReport("信息公开-公开年报"),
    PublicInstitution("信息公开-公开制度"),
    PublicGuide("信息公开-公开指南"),
    PublicApply("信息公开-依申请公开"),
    PublicApplyCatalog("信息公开-依申请公开目录"),
    PublicCatalog("信息公开-主动公开目录"),
    ;

    DataModule(String text) {
        this.text = text;
    }

    private String text;

    @Override
    public String toString() {
        return this.name();
    }

    public String getText() {
        return text;
    }
}
