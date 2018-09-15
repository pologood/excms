package cn.lonsun.special.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.vo.SpecialQueryVO;

/**
 * Created by doocal on 2016-10-15.
 */
public interface ISpecialService extends IMockService<SpecialEO> {

    /**
     * 专题 ： 分析模板公共部分,写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    ISpecialService parseCommonTemplate();

    /**
     * 创建专题虚拟模型模板
     * 创建专题虚拟栏目模板和文章页模板，只在第一次添加专题的时候写入
     * 虚拟栏目也要有模板，不然生成静态报错
     */
    ISpecialService parseVirtualModelTemplate();

    /**
     * 分析模型
     * <p>
     * 根据配置的 NewsTplCode,NavCode 到 tplMap 找对应的项，并将自己的 ModelCode 做为 map 的key
     * 用于下一下添加栏目的要找的模型对象
     */
    ISpecialService parseModel();

    /**
     * 子站 ： 分析模板公共部分,写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    ISpecialService parseSiteCommonTemplate();

    /**
     * 专题 : 分析模板,提取内容写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    ISpecialService parseTemplate();

    /**
     * 专题 : 分析模板,提取内容写入模板表
     * <p>
     * 创建模板项并同时写入内容，根据setting.xml配置的TemplateCode 做为模板的唯一键（此值和模型的NavCode、NewsTplCode关联）
     * 用于下一步添加模型时候找到添加的模板的对象
     */
    ISpecialService parseSiteTemplate();

    /**
     * 专题：分析栏目
     */
    ISpecialService parseColumn();

    /**
     * 子站：分析栏目
     * 目前栏目深度只能创建 1 级
     */
    ISpecialService parseSiteColumn();

    /**
     * 分析标签，绑定到栏目
     */
    ISpecialService parseLabelBindColumn();

    /**
     * 替换模板标签ID为真实栏目的ID
     */
    String replaceLabelId(String content);

    /**
     * 替换模板公部部分对应的模板块ID
     *
     * @param content
     */
    String replaceCommonTplID(String content);

    /**
     * 保存专题
     *
     * @param special
     */
    void saveSpecial(SpecialEO special);

    /**
     * 保存专题子站
     *
     * @param siteMgrEO
     */
    SiteConfigEO saveSiteSpecial(SiteMgrEO siteMgrEO);

    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    Pagination getPagination(SpecialQueryVO queryVO);

    /**
     * 改变专题状态
     */
    void changeSpecial(Long id, Long specialStatus);

    /**
     * 删除专题
     */
    void deleteSpecial(Long id);

    /**
     * 查找是否存在相关主题 ID
     *
     * @param id
     * @return
     */
    SpecialEO getThemeById(Long id);

    /**
     * 根据ID查找专题
     *
     * @param id
     * @return
     */
    SpecialEO getById(Long id);
}
