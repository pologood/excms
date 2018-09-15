package cn.lonsun.test.controller;

import java.lang.reflect.InvocationTargetException;
import java.util.Random;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.controller.BaseController;

/**
 * @author DooCal
 * @ClassName: test
 * @Description:
 * @date 2015/12/8 8:49
 */
@Controller
@RequestMapping(value = "test")
public class Test extends BaseController {

    @Autowired
    private IBaseContentService baseContentService;

    @RequestMapping("edit")
    public String article_mgr_edit(Long pageIndex, ModelMap map) {
        if (pageIndex == null) pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        return "/content/articlenews/article_mgr_edit";
    }

    @RequestMapping("list")
    public String article_mgr_list(Long pageIndex, ModelMap map) {
        if (pageIndex == null) pageIndex = 0L;
        map.put("pageIndex", pageIndex);
        return "/content/articlenews/article_mgr_list";
    }

    @RequestMapping("wx_list")
    public String wx_list() {
        return "/wechat/wx_list";
    }

    @RequestMapping("wx_form")
    public String wx_form() {
        return "/wechat/wx_form";
    }

    @RequestMapping("wx_article")
    public String wx_article() {
        return "/wechat/wx_article";
    }

    /**
     * @return
     * @Description 保存增加或修改的新闻
     * @author Hewbing
     * @date 2015年9月11日 下午4:10:30
     */
    @RequestMapping("save")
    @ResponseBody
    public Object saveArticleNews(Long s) {
        //content=WordsSplitHolder.wordsRplc(articleText,content, Type.SENSITIVE.toString());
        Long[] cids = new Long[]{54039L, 54044L, 54049L, 54054L, 54059L, 54064L};
        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, 174500L);
        String content = "<p style=\"text-indent:2em;\">\n" +
            "\t摘要1月20日下午，合肥市政府联合多部门召开应对低温雨雪和冰冻天气的紧急工作会议。会议要求对事故易发路段，合肥将加强巡逻管控力度，科学组织车辆分流、除雪除冰防滑等交通管制和安全应急措施。\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t合肥市政府联合多部门1月20日下午召开应对低温雨雪和冰冻天气的紧急工作会议。\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t会议要求对事故易发路段，合肥将加强巡逻管控力度，科学组织车辆分流、除雪除冰防滑等交通管制和安全应急措施。\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t公共安全方面，将重点加强高层建筑、在建工程、地下工程、石油化工、外来务工人员集中居住的宿舍工棚等领域及“家庭式”旅馆、老结构居民住宅等场所的消防安全管理。\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t<a href=\"http://travel.ifeng.com/\" target=\"_blank\">旅游</a>和学校安全必须加强，“遇到恶劣天气，及时采取关闭景点等措施，严防事故发生。”\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t此外，合肥市还将组织对所属区域农贸市场、大跨度轻质车间、仓库屋顶、农业大棚等安全检查。\n" +
            "</p>\n" +
            "<p style=\"text-indent:2em;\">\n" +
            "\t<br />\n" +
            "</p>";
        Long t1 = System.currentTimeMillis();
        for (Long i = s; i < 500000; i++) {
            BaseContentEO _eo = new BaseContentEO();
            Random rand = new Random();
            try {
                BeanUtils.copyProperties(_eo, contentEO);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            _eo.setColumnId(cids[rand.nextInt(5)]);
            _eo.setIsPublish(1);
            _eo.setId(null);
            _eo.setTitle(_eo.getTitle() + String.valueOf(i));
            //Long id = baseContentService.saveArticleNews(_eo, content, null);
        }
        Long t2 = System.currentTimeMillis();
        System.err.println("耗时：" + (t2 - t1) + "ms");
        boolean rel = false;
//		if(contentEO.getIsPublish()==1){
//			//处理保存时发布,正式部署时启用
//			rel= MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()),1);
//		}else{
//			rel = MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()),2);
//		}
        return getObject(rel);
    }

}
