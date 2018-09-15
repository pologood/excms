package cn.lonsun.base;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;

/**
 * Created by zhusy on 2016-7-30.
 */
public enum  ProcessBusinessType{

    ArticleNews(EngineCode.EX,"/articleNews/articleNewsEdit","articleNewsFormDataService"),
    PictureNews(EngineCode.EX,"/pictureNews/picNewsEdit","pictureNewsFormDataService"),
    VideoNews(EngineCode.EX,"/videoNews/editVideo","videoNewsFormDataService"),
    GuestBook(EngineCode.EX,"/guestBook/editGuestBook","guestBookFormDataService"),
    OnlineDeclaration(EngineCode.EX,"/onlineDeclaration/editDeclaration","onlineDeclarationFormDataService");

    private EngineCode engineCode;

    private String formUrl;

    private String serviceBeanName;

    private ProcessBusinessType(EngineCode engineCode,String formUrl,String serviceBeanName) {
        this.engineCode = engineCode;
        this.formUrl = formUrl;
        this.serviceBeanName = serviceBeanName;
    }

    public EngineCode getEngineCode() {
        return engineCode;
    }

    public String getFormUrl() {
        return formUrl;
    }

    public String getServiceBeanName() {
        return serviceBeanName;
    }

}
