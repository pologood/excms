package cn.lonsun.ucenter.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.PoiExcelUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Type;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.cache.InternalLdapCache;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.vo.Node4SaveOrUpdateVO;
import cn.lonsun.system.role.internal.service.IRoleAsgService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 组织/单位管理控制器
 *
 * @author xujh
 * @version V1.0
 * @Description:
 * @date 2014年9月23日 下午10:02:29
 */
@Controller
@RequestMapping(value = "/organ", produces = {"application/json;charset=UTF-8"})
public class OrganController extends BaseController {

    @Autowired
    private IOrganService organService;
    @Autowired
    private IPersonService personService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private IRoleAsgService roleAsgService;


    /**
     * 获取站点下所有单位
     *
     * @return
     */
    @RequestMapping("getSiteOrgans")
    @ResponseBody
    public Object getSiteOrgans() {
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            return getObject();
        }
        return getObject(organService.getOrgansBySiteId(siteId, true));
    }

    /**
     * 单位迁移
     *
     * @param m
     * @return
     */
    @RequestMapping("moveUnit")
    public String moveUnit(Model m) {

        return "/system/ucenter/unit_move";
    }

    /**
     * 保存迁移单位信息
     *
     * @param organIds   需迁移的组织id
     * @param srcOrganId 迁移至组织id
     * @return
     */
    @RequestMapping("saveMoveOrgans")
    @ResponseBody
    public Object saveMoveOrgans(Long[] organIds, Long srcOrganId) {
        if (organIds == null || srcOrganId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空");
        }
        if (organIds != null && organIds.length > 0) {
            for (Long organId : organIds) {
                try {
                    //需迁移的组织
                    OrganEO organ = organService.getEntity(OrganEO.class, organId);
                    //迁移至组织
                    OrganEO srcOrgan = organService.getEntity(OrganEO.class, srcOrganId);
                    //不能将父节点移动至子节点
                    System.out.println(srcOrgan.getDn());
                    System.out.println(organ.getDn());
                    System.out.println(srcOrgan.getDn().indexOf(organ.getDn()));
                    if (organ != null && srcOrgan != null && srcOrgan.getDn().indexOf(organ.getDn()) == -1) {
                        Long srcOrganPid = organ.getParentId();
                        organService.updateOrgansAndPerson(srcOrgan, organ);
                        //更新字段属于
                        if (srcOrganPid != null) {
                            organService.updateHasChildren4Organ(srcOrganPid);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //update
            if (srcOrganId != null) {
                organService.updateHasChildren4Organ(srcOrganId);
            }
            //		//异步执行
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    organService.initSimpleOrgansCache();
                    personService.initSimplePersonsCache();
                }
            });
        }
        return getObject();
    }

    @RequestMapping("unitExport")
    public String unitExport(Model m) {

        return "/system/ucenter/unit_export";
    }

    @RequestMapping("unitMemberExport")
    public String unitMemberExport(Model m) {

        return "/system/ucenter/unit_member_export";
    }

    /**
     * 下载模版
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("downUnitxls")
    public void downUnitXls(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String organName = request.getParameter("name");
        String[] headers = new String[]{"组织名称", "类型(单位/部门)", "单位简称", "编码", "开启信息公开（是/否）", "排序号", "联系电话1", "联系电话2",
                "联系地址1", "联系地址2", "单位网址", "下级科室名称"};
        String name = "组织导入模版" + (StringUtils.isEmpty(organName) ? "" : "(" + organName + ")");
        String title = "组织导入";
        PoiExcelUtil.exportExcel(title, name, "xls", headers, null, response);
    }

    @RequestMapping("downMemberxls")
    public void downMemberxls(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String organName = request.getParameter("name");
        String[] headers = new String[]{"用户名", "密码", "姓名", "性别", "电子邮箱", "手机号码",
                "证件号码", "地址", "验证问题", "验证答案"};
        String name = "会员导入模板";
        String title = "会员导入";
        PoiExcelUtil.exportExcel(title, name, "xls", headers, null, response);
    }


    /**
     * 组织管理首页
     *
     * @return
     */
    @RequestMapping("unitPage")
    public String unitPage(Model m) {
        String isRemoveNode = "1";
        if (LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin()) {
            isRemoveNode = "0";
        }
        m.addAttribute("isRemoveNode", isRemoveNode);
        return "/system/ucenter/unit";
    }

    @RequestMapping("getOrgansBySiteId")
    @ResponseBody
    public Object getOrgansBySiteId(Long siteId) {
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点id不能为空");
        }
        return getObject(organService.getOrgansByType(siteId, null));
    }

    @RequestMapping("getOrgansByTypeAndName")
    @ResponseBody
    public Object getOrgansByTypeAndName(String name, String type) {
        if (type == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位类型不能为空");
        }
        return getObject(organService.getOrgansByTypeAndName(name, type));
    }

    /**
     * 组织管理首页
     *
     * @return
     */
    @RequestMapping("editPage")
    public String editPage(Long id, Long pid, String nType, Model m) {
        m.addAttribute("id", id);
        m.addAttribute("pid", pid);
        m.addAttribute("nType", nType);
        return "/system/ucenter/unit_edit";
    }

    /**
     * 根据parentId和parentDn获取子组织、单位集合，但不包括外部单位
     *
     * @param parentId
     * @param parentDn
     * @param scope    允许的值有：1,2,3,4
     * @return
     */
    @RequestMapping("getInternalSubOrgans")
    @ResponseBody
    public Object getInternalSubOrgans(Long parentId, String parentDn, @RequestParam(defaultValue = "0") int scope) {
        List<OrganNodeVO> organs = new ArrayList<OrganNodeVO>();
        if (LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin() || RoleAuthUtil.isCurUserColumnAdmin()) {//超管或者系统管理员
            String[] types = null;
            String virtualOrgan = Type.VirtualNode.toString();
            String organ = Type.Organ.toString();
            String organUnit = Type.OrganUnit.toString();
            String virtual = Type.Virtual.toString();
            //标记此请求的叶子节点类型，以便设置isParent属性值,默认为0：叶子节点为部门或虚拟部门
            int flag = 0;
            switch (scope) {
                case 0:
                    break;
                case 1:
                    types = new String[]{virtualOrgan, organ, organUnit, virtual};
                    break;
                case 2:
                    types = new String[]{virtualOrgan, organ, organUnit};
                    break;
                case 3:
                    types = new String[]{virtualOrgan, organ};
                    //3:叶子节点为单位或虚拟节点
                    flag = 3;
                    break;
                case 4:
                    types = new String[]{virtualOrgan};
                    break;
                default:
                    types = null;
                    break;
            }
            organs = organService.getSubOrgans(parentId, parentDn, types, true, flag);
        } else if (LoginPersonUtil.isSiteAdmin()) {//站点管理员
            if (parentId == null) {
                SiteMgrEO SiteMgrs = CacheHandler.getEntity(SiteMgrEO.class, LoginPersonUtil.getSiteId());
                if (SiteMgrs != null && !StringUtils.isEmpty(SiteMgrs.getUnitIds())) {
                    //如果SiteMgrs.getUnitIds()是多个需要在处理
                    List<OrganNodeVO> oNodes = organService.getOrganNodeVOs(new Long[]{Long.parseLong(SiteMgrs.getUnitIds())}, 0);
                    if (oNodes != null && oNodes.size() > 0) {
                        organs.addAll(oNodes);
                    }
                }
            } else {
                organs = organService.getSubOrgans(parentId, 0);
            }
        } else {//普通角色
            if (parentId == null) {
                Long organId = LoginPersonUtil.getUnitId();
                if (organId != null) {
                    //如果SiteMgrs.getUnitIds()是多个需要在处理
                    List<OrganNodeVO> oNodes = organService.getOrganNodeVOs(new Long[]{organId}, 0);
                    if (oNodes != null && oNodes.size() > 0) {
                        organs.addAll(oNodes);
                    }
                }
            } else {
                organs = organService.getSubOrgans(parentId, 0);
            }
        }
        return getObject(organs);
    }

    /**
     * 根据parentId和parentDn获取子组织、单位集合，但不包括外部单位
     *
     * @param parentId
     * @param parentDn
     * @return
     */
    @RequestMapping("getInternalSubOrgansAndPersons")
    @ResponseBody
    public Object getInternalSubOrgansAndPersons(Long parentId, String parentDn) {
        List<Object> list = new ArrayList<Object>();
        if (LoginPersonUtil.isRoot() || LoginPersonUtil.isSuperAdmin() || RoleAuthUtil.isCurUserColumnAdmin()) {//超管或者系统管理员
            if (parentId == null && RoleAuthUtil.isCurUserColumnAdmin())
                parentId = LoginPersonUtil.getUnitId();
            List<OrganNodeVO> oNodes = organService.getSubOrgans(parentId, parentDn, null, true, 1);
            if (oNodes != null && oNodes.size() > 0) {
                list.addAll(oNodes);
            }
            List<PersonNodeVO> pNodes = personService.getSubPersons(parentId, parentDn);
            if (pNodes != null && pNodes.size() > 0) {
                list.addAll(pNodes);
            }
        } else if (LoginPersonUtil.isSiteAdmin()) {//站点管理员
            List<OrganNodeVO> oNodes = null;
            if (parentId == null) {
                SiteMgrEO SiteMgrs = CacheHandler.getEntity(SiteMgrEO.class, LoginPersonUtil.getSiteId());
                if (SiteMgrs != null && !StringUtils.isEmpty(SiteMgrs.getUnitIds())) {
                    //如果SiteMgrs.getUnitIds()是多个需要在处理
                    oNodes = organService.getOrganNodeVOs(new Long[]{Long.parseLong(SiteMgrs.getUnitIds())}, 0);
                }
            } else {
                oNodes = organService.getSubOrgans(parentId, null, null, true, 1);

            }
            if (oNodes != null && oNodes.size() > 0) {
                list.addAll(oNodes);
            }
            if (parentId != null) {
                List<PersonEO> persons = this.personService.getPersons(parentId);
                if (persons != null && persons.size() > 0) {
                    for (PersonEO person : persons) {
                        PersonNodeVO np = getPersonNode(person);
                        list.add(np);
                    }
                }
            }
        } else {//普通角色
            List<OrganNodeVO> oNodes = null;
            if (parentId == null) {
                Long organId = LoginPersonUtil.getUnitId();
                if (organId != null) {
                    //如果SiteMgrs.getUnitIds()是多个需要在处理
                    oNodes = organService.getOrganNodeVOs(new Long[]{organId}, 0);
                }
            } else {
                oNodes = organService.getSubOrgans(parentId, null, null, true, 1);
            }
            if (oNodes != null && oNodes.size() > 0) {
                list.addAll(oNodes);
            }
            if (parentId != null) {
                List<PersonEO> persons = this.personService.getPersons(parentId);
                if (persons != null && persons.size() > 0) {
                    for (PersonEO person : persons) {
                        PersonNodeVO np = getPersonNode(person);
                        list.add(np);
                    }
                }
            }
        }
        return getObject(list);
    }

    private PersonNodeVO getPersonNode(PersonEO person) {
        PersonNodeVO node = new PersonNodeVO();
        BeanUtils.copyProperties(person, node);
        node.setId(person.getPersonId());
        node.setPid(person.getOrganId());
        node.setNodeType(TreeNodeVO.Type.Person.toString());
        node.setIsPluralistic(person.getIsPluralistic());
        node.setIcon(TreeNodeVO.Icon.Male.getValue());
        return node;
    }

    /**
     * 组织迁移首页
     *
     * @return
     */
    @RequestMapping("unitMovePage")
    public String unitMovePage() {
        return "/app/mgr/unitMove";
    }

    /**
     * 选择单位部门列表
     *
     * @return
     */
    @RequestMapping("addUnit")
    public String addUnit() {
        return "/app/mgr/addUnit";
    }

    /**
     * 选择单位部门列表
     *
     * @return
     */
    @RequestMapping("selectUnitMove")
    public String selectUnitMove() {
        return "/app/mgr/selectUnitMove";
    }

    /**
     * 保存迁移单位信息
     *
     * @param clickOrganId  需迁移的组织id
     * @param selectOrganId 迁移至组织id
     * @return
     */
    @RequestMapping("saveMoveOrgan")
    @ResponseBody
    public Object saveMoveOrgan(Long clickOrganId, Long selectOrganId) {
        if (clickOrganId == null || selectOrganId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "参数不能为空");
        }
        if (clickOrganId.equals(selectOrganId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "不可以移动至本单位下");
        }
        List<Long> listId = new ArrayList<Long>();
        this.getAllbyPid(clickOrganId, listId);
        if (listId.contains(selectOrganId)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "迁移单位/部门不可以是选择单位的子节点");
        }
        //需迁移的组织
        OrganEO clickOrgan = organService.getEntity(OrganEO.class, clickOrganId);
        //迁移至组织
        OrganEO selectOrgan = organService.getEntity(OrganEO.class, selectOrganId);
        if (selectOrgan != null && clickOrgan != null) {
            Long clickOrganPid = clickOrgan.getParentId();
            organService.updateOrgansAndPerson(selectOrgan, clickOrgan);
            //更新字段属于
            if (clickOrganPid != null) {
                organService.updateHasChildren4Organ(clickOrganPid);
            }
            if (selectOrganId != null) {
                organService.updateHasChildren4Organ(selectOrganId);
            }
            //		//异步执行
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    organService.initSimpleOrgansCache();
                    personService.initSimplePersonsCache();

                }
            });
//			organService.initOrganNamesWithDn();
//			// 创建成果后更新缓存
//			InternalLdapCache.getInstance().update(clickOrgan.getParentId());
//			// 创建成果后更新缓存
//			InternalLdapCache.getInstance().update(selectOrgan.getParentId());
        }
        return getObject();
    }

    private void getAllbyPid(Long clickOrganId, List<Long> listId) {
        List<OrganEO> list = organService.getOrgans(clickOrganId);
        if (list != null && list.size() > 0) {
            for (OrganEO o : list) {
                listId.add(o.getOrganId());
                getAllbyPid(o.getOrganId(), listId);
            }
        }
    }

    /**
     * 单位管理员组织管理首页
     *
     * @param request
     * @param indicatorId
     * @return
     */
    @RequestMapping("unitsPage")
    public String unitsPage(HttpServletRequest request, Long indicatorId) {
        request.setAttribute("indicatorId", indicatorId);
        return "/app/mgr/unitmanager/unit";
    }

    /**
     * 组织管理首页
     *
     * @return
     */
    @RequestMapping("selectUnitPage")
    public String selectUnitPage() {
        return "/app/common/selectSingleUnit";
    }

    /**
     * 组织管理首页
     *
     * @return
     */
    @RequestMapping("editPage4Unit")
    public String editPage4Unit() {
        return "/app/mgr/unitmanager/unit_edit";
    }

    /**
     * 列表页面
     *
     * @return
     */
    @RequestMapping("listPage")
    public ModelAndView listPage() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/app/mgr/developer/indicator_organ_list");
        return mav;
    }

    @RequestMapping
    @ResponseBody
    public Object getOrgan4Code(Long organId) {
        return getObject(organService.getEntity(OrganEO.class, organId));
    }

    /**
     * 编辑页面
     *
     * @param organId
     * @return
     */
    @RequestMapping("editCodePage")
    public ModelAndView editCodePage(Long organId) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("organId", organId);
        mav.setViewName("/app/mgr/developer/indicator_organ_edit");
        return mav;
    }

    /**
     * 获取有单位编码的单位分页列表
     *
     * @return
     */
    @RequestMapping("getPagination4Code")
    @ResponseBody
    public Object getPagination4Code(PageQueryVO query) {
        if (query.getPageIndex() == null || query.getPageIndex() < 0) {
            query.setPageIndex(Long.valueOf(0));
        }
        if (query.getPageSize() == null || query.getPageSize() < 0) {
            query.setPageSize(Integer.valueOf(15));
        }
        return getObject(organService.getPagination4Code(query));
    }

    /**
     * 更新单位编码
     *
     * @param unitId
     * @param code
     * @return
     */
    @RequestMapping("updateCode")
    @ResponseBody
    public Object updateCode(Long unitId, String code) {
        if (StringUtils.isEmpty(code) || code.trim().length() <= 0 || code.trim().length() > 16) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入4-16位的单位编码");
        }
        //验证编码是否重复
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("code", code);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<OrganEO> organs = organService.getEntities(OrganEO.class, map);
        if (organs != null && organs.size() > 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位编码已存在，请输入新的编码");
        }
        organService.updateCode(unitId, code);
        return getObject();
    }

    /**
     * 删除单位或部门编码
     *
     * @param organIds
     * @return
     */
    @RequestMapping("deleteCodes")
    @ResponseBody
    public Object deleteCodes(@RequestParam(value = "organIds[]") Long[] organIds) {
        if (organIds == null || organIds.length <= 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请至少选择一条记录");
        }
        organService.deleteCodes(organIds);
        return getObject();
    }

    /**
     * 异步验证组织名称是否可用
     *
     * @param parentId
     * @param name
     * @return
     */
    @RequestMapping("isNameUsable")
    public Object isNameUsable(Long parentId, String name) {
        if (parentId == null || StringUtils.isEmpty(name)) {
            throw new NullPointerException();
        }
        return getObject(organService.isNameUsable(parentId, null, name) ? 1 : 0);
    }

    /**
     * 删除Organ
     *
     * @param organId
     * @return
     */
    @RequestMapping("deleteOrgan")
    @ResponseBody
    public Object deleteOrgan(Long organId) {
        try {
            OrganEO organEO = organService.getEntity(OrganEO.class, organId);
            if (organEO != null && Integer.valueOf(1).equals(organEO.getIsPublic())) {
                return ajaxErr("该单位已绑定信息公开，不能删除");
            }

            Long parentId = organService.delete(organId);
            //更新父节点
            if (parentId != null) {
                organService.updateHasChildren4Organ(parentId);
            }
            //			//异步执行
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    organService.initSimpleOrgansCache();
                }
            });
            //			// 创建成果后更新缓存
            //			InternalLdapCache.getInstance().update(parentId);
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
        }
        return getObject();
    }

    /**
     * 保存组织/单位
     *
     * @param node
     * @return
     */
    @RequestMapping("saveOrgan")
    @ResponseBody
    public Object saveOrgan(OrganNodeVO node) {
        checkOrgan(node);
        OrganEO organ = new OrganEO();
        BeanUtils.copyProperties(node, organ);
        organ.setIsExternal(node.getIsExternal());
        organ.setType(node.getNodeType());
        organ.setIsFictitious(node.getFictitious());
        // 设置父节点ID
        organ.setParentId(node.getPid());
        if (node.getParentId() != null) {
            organ.setParentId(node.getPid());
        }
        organ.setOfficeAddress(node.getOfficeAddress());
        organ.setOfficePhone(node.getOfficePhone());
        organ.setServeAddress(node.getServeAddress());
        organ.setServePhone(node.getServePhone());
        organ.setOrganUrl(node.getOrganUrl());
        organ.setCode(node.getCode());
        organ.setIsPublic(node.getIsPublic());
        organ.setHeadPerson(node.getHeadPerson());
        organ.setPositions(node.getPositions());
        organService.save(organ);
        //更新父节点
        if (organ.getParentId() != null) {
            organService.updateHasChildren4Organ(organ.getParentId());
        }
//		异步执行
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                organService.initSimpleOrgansCache();
            }
        });
        // 创建成果后更新缓存
        //		InternalLdapCache.getInstance().update(organ.getParentId());
        Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(organ.getOrganId(), organ.getParentId(), organ.getName(), false);
        n.setSortNum(organ.getSortNum());
        n.setNodeType(node.getNodeType());

        return getObject(n);
    }

    /**
     * 获取组织
     *
     * @param organId
     * @return
     */
    @RequestMapping("getOrgan")
    @ResponseBody
    public Object getOrgan(Long organId, Long parentId) {
        OrganNodeVO node = new OrganNodeVO();
        // 当organId为null时，表示前端发起请求返回空对象
        if (organId != null) {
            OrganEO organ = organService.getEntity(OrganEO.class, organId);
            if (organ.getParentId() != null) {
                OrganEO parent = organService.getEntity(OrganEO.class, organ.getParentId());
                node.setParentNodeType(parent.getType());
                node.setIsParentFictitious(parent.getIsFictitious());
            }
            addProperties(node, organ);
            node.setNodeType(organ.getType());
            node.setIsExternal(organ.getIsExternal());
            node.setFictitious(organ.getIsFictitious());
            node.setHasOrgans(organ.getHasOrgans() == 1 ? true : false);
            node.setIsParent(node.getHasOrgans());
        } else {
            // 排序号默认取最大排序号+2
            Long sortNum = organService.getMaxSortNum(parentId);
            if (sortNum == null) {
                sortNum = 2L;
            } else {
                sortNum = sortNum + 2;
            }
            node.setSortNum(sortNum);
            if (parentId == null || parentId <= 0) {
                //默认为单位容器-虚拟单位
                node.setNodeType(TreeNodeVO.Type.Organ.toString());
                node.setFictitious(Integer.valueOf(1));
            } else {
                // 设置是否是外部单位-只要父单位时外部单位，那么子单位一定是外部单位
                Boolean isExternal = Boolean.FALSE;
                OrganEO parent = organService.getEntity(OrganEO.class, parentId);
                //是否县区单位与父节点保持同步
                if (parent.getIsExternal() != null) {
                    isExternal = parent.getIsExternal();
                }
                node.setIsExternal(isExternal);
                //父节点类型
                String parentNodeType = parent.getType();
                node.setParentNodeType(parent.getType());
                node.setIsParentFictitious(parent.getIsFictitious());
                if (TreeNodeVO.Type.Organ.toString().equals(parentNodeType)) {
                    //如果父节点是单位容器/单位，那么设置默认的为Organ
                    node.setNodeType(TreeNodeVO.Type.Organ.toString());
                } else {
                    node.setNodeType(TreeNodeVO.Type.OrganUnit.toString());
                    node.setFictitious(parent.getIsFictitious());
                }
            }
        }
        return getObject(node);
    }

    /**
     * 更新Organ
     *
     * @param node
     * @return
     */
    @RequestMapping("updateOrgan")
    @ResponseBody
    public Object updateOrgan(OrganNodeVO node) {
        checkOrgan(node);
        OrganEO organ = new OrganEO();
        organ.setName(node.getName());
        organ.setSimpleName(node.getSimpleName());
        organ.setSortNum(node.getSortNum());
        organ.setIsExternal(node.getIsExternal());
        organ.setDescription(node.getDescription());
        organ.setSiteId(node.getSiteId());
        organ.setOfficeAddress(node.getOfficeAddress());
        organ.setOfficePhone(node.getOfficePhone());
        organ.setServeAddress(node.getServeAddress());
        organ.setServePhone(node.getServePhone());
        organ.setOrganUrl(node.getOrganUrl());
        organ.setCode(node.getCode());
        organ.setIsPublic(node.getIsPublic());
        organ.setHeadPerson(node.getHeadPerson());
        organ.setPositions(node.getPositions());
        try {
            organ = organService.update(node.getOrganId(), organ);
            //			//异步执行
            taskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    organService.initSimpleOrgansCache();
                }
            });
            //			// 更新缓存
            //			InternalLdapCache.getInstance().update(organ.getParentId());
        } catch (BusinessException e) {
            e.printStackTrace();
            throw new BaseRunTimeException(TipsMode.Key.toString(), e.getKey());
        }
        Node4SaveOrUpdateVO n = new Node4SaveOrUpdateVO(organ.getOrganId(),
                organ.getParentId(), organ.getName(), true);
        n.setIsParent(node.getIsParent());
        n.setSortNum(organ.getSortNum());
        return getObject(n);
    }

    /**
     * 组织架构输入验证
     *
     * @param node
     */
    private void checkOrgan(OrganNodeVO node) {
        //排序号
        Long sortNum = node.getSortNum();
        if (sortNum == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入排序号");
        } else {
            if (sortNum.longValue() < 0) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能小于0");
            }
            if (sortNum > 999999) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "排序号不能大于999999");
            }
        }
        //名称
        String name = node.getName();
        if (StringUtils.isEmpty(name)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入名称");
        } else {
            name = name.trim();
            if (!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(name)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "名称仅支持中文、英文数字和部分中文标点符号的组合");
            }
            if (name.length() > 50) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "名称最多支持50个字符");
            }
            node.setName(name);
        }
        //简称
        String simpleName = node.getSimpleName();
        if (!StringUtils.isEmpty(simpleName)) {
            simpleName = simpleName.trim();
            if (!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(simpleName)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "简称仅支持中文、英文和数字的组合");
            }
            if (simpleName.length() > 50) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "简称最多支持50个字符");
            }
            node.setSimpleName(simpleName);
        }
        //描述
        String description = node.getDescription();
        if (!StringUtils.isEmpty(description)) {
            description = description.trim();
            if (description.length() > 300) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "简介最多支持300个字符");
            }
        }
    }

    /**
     * 从organ向node中注入属性名称相同的值
     *
     * @param node
     * @param organ
     */
    private void addProperties(OrganNodeVO node, OrganEO organ) {
        BeanUtils.copyProperties(organ, node);
        node.setId(organ.getOrganId());
        node.setPid(organ.getParentId());
        if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
            node.setNodeType(TreeNodeVO.Type.Organ.toString());
        } else {
            if (organ.getIsFictitious() == 1) {
                // 虚拟单位
                node.setNodeType("Virtual");
            } else {
                node.setNodeType(TreeNodeVO.Type.OrganUnit.toString());
            }
        }
    }
}
