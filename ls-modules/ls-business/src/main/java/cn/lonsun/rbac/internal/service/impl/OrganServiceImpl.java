package cn.lonsun.rbac.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.enums.RoleCodes;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.common.vo.TreeNodeVO;
import cn.lonsun.common.vo.TreeNodeVO.Icon;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.BusinessException;
import cn.lonsun.core.util.FileUtils;
import cn.lonsun.core.util.JSONHelper;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.service.ILdapOrganService;
import cn.lonsun.ldap.internal.util.Constants;
import cn.lonsun.ldap.internal.util.LDAPUtil;
import cn.lonsun.ldap.internal.util.LdapOpenUtil;
import cn.lonsun.ldap.vo.OrganNodeVO;
import cn.lonsun.log.internal.service.ILogService;
import cn.lonsun.rbac.internal.dao.IOrganDao;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.rbac.internal.util.OrganDnContainer;
import cn.lonsun.rbac.internal.vo.DeleteDnVO;
import cn.lonsun.rbac.internal.vo.OrganVO;
import cn.lonsun.rbac.servlet.InitServlet;
import cn.lonsun.rbac.utils.PinYinUtil;
import cn.lonsun.rbac.utils.TimeCutUtil;

import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import java.util.*;

@Service("organService")
public class OrganServiceImpl extends MockService<OrganEO> implements
        IOrganService {


    /**
     * 组织相关异常枚举
     *
     * @author xujh
     *
     */
    private enum ExceptionKey {
        OrganizationStorageError("OrganEO.OrganizationStorageError"), HasChildrenException(
                "HasChildrenException"), HasOrgansException(
                "HasOrgansException"), HasPersonsException(
                "HasPersonsException"), HasRoleException("HasRoleException");

        private String value;

        private ExceptionKey(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

    }
    private ILdapOrganService ldapOrganService;
    @Autowired
    private IOrganDao organDao;
    @Autowired
    private IOrganPersonService organPersonService;
    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private IPersonService personService;
    @Autowired
    private IPlatformService platformService;
    @Autowired
    private ILogService logService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRoleAssignmentService roleAssignmentService;
    @Autowired
    private IIndicatorPermissionService indicatorPermissionService;



    @Override
    public void updateIsPublic(Long... ids){
        organDao.updateIsPublic(ids);
    }

    /**
     * 获取当前用户负责管理的单位(单位管理员)
     *
     * @param userId
     * @return
     */
    public List<OrganEO> getUnits4UnitManager(Long userId){
        //获取单位管理员角色
        RoleEO role = roleService.getRoleByCode(RoleCodes.unit_admin.toString());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleId", role.getRoleId());
        params.put("userId", userId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
        List<OrganEO> organs = null;
        if(ras!=null&&ras.size()>0){
            Long[] unitIds = new Long[ras.size()];
            for(int i=0;i<ras.size();i++){
                RoleAssignmentEO ra = ras.get(i);
                unitIds[i] = ra.getUnitId();
            }
            organs = getOrgansByOrganIds(unitIds);
        }
        return organs;
    }

    /**
     * 获取当前用户负责管理的单位(单位管理角色)
     *
     * @param userId
     * @param indicatorId
     * @return
     */
    public List<OrganEO> getFLUnits4UnitManager(Long userId,Long indicatorId){
        //获取用户(userId)拥有管理菜单(indicatorId)的角色ID集合
        List<Long> roleIds = roleAssignmentService.getRoleIds(userId, indicatorId);
        //获取单位管理员角色
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("roleId", roleIds);
        params.put("userId", userId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<RoleAssignmentEO> ras = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
        List<OrganEO> organs = null;
        if(ras!=null&&ras.size()>0){
            Long[] unitIds = new Long[ras.size()];
            for(int i=0;i<ras.size();i++){
                RoleAssignmentEO ra = ras.get(i);
                unitIds[i] = ra.getUnitId();
            }
            organs = getOrgansByOrganIds(unitIds);
        }
        return organs;
    }


    /**
     * 更新hasVirtualNode,hasOrgans,hasOrganUnits或hasFictitiousUnits
     * 由于事务的问题，请在Controller中调用
     * @param organId
     */
    @Override
    public void updateHasChildren4Organ(Long organId){
        if(organId==null||organId<=0){
            return;
        }
        OrganEO organ = getEntity(OrganEO.class, organId);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", organId);
        params.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> children = getEntities(OrganEO.class, params);
        //获取父单位/部门的信息
        Integer hasVirtualNode = Integer.valueOf(0);
        Integer hasOrgans = Integer.valueOf(0);
        Integer hasOrganUnits = Integer.valueOf(0);
        Integer hasFictitiousUnits = Integer.valueOf(0);
        if(children!=null&&children.size()>0){
            for(OrganEO child:children){
                String type = child.getType();
                //数据库中存储的类型只有Organ和OrganUnit
                if(type.equals(TreeNodeVO.Type.Organ.toString())){
                    //单位
                    if(child.getIsFictitious()!=null&&child.getIsFictitious().intValue()==0){
                        hasOrgans = 1;
                    }else{//单位容器
                        hasVirtualNode = 1;
                    }
                }else{
                    //部门/处室
                    if(child.getIsFictitious()!=null&&child.getIsFictitious().intValue()==0){
                        hasOrganUnits = 1;
                    }else{//虚拟部门/处室
                        hasFictitiousUnits = 1;
                    }
                }
            }
        }
        organ.setHasVirtualNodes(hasVirtualNode);
        organ.setHasOrgans(hasOrgans);
        organ.setHasOrganUnits(hasOrganUnits);
        organ.setHasFictitiousUnits(hasFictitiousUnits);
        updateEntity(organ);
    }

    /**
     * 更新hasVirtualNode,hasOrgans,hasOrganUnits或hasFictitiousUnits
     * 由于事务的问题，请在Controller中调用
     * @param organ
     */
    @Override
    public void updateHasChildren4Organ(OrganEO organ){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", organ.getOrganId());
        params.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> children = getEntities(OrganEO.class, params);
        //获取父单位/部门的信息
        Integer hasVirtualNode = Integer.valueOf(0);
        Integer hasOrgans = Integer.valueOf(0);
        Integer hasOrganUnits = Integer.valueOf(0);
        Integer hasFictitiousUnits = Integer.valueOf(0);
        if(children!=null&&children.size()>0){
            for(OrganEO child:children){
                String type = child.getType();
                //数据库中存储的类型只有Organ和OrganUnit
                if(type.equals(TreeNodeVO.Type.Organ.toString())){
                    //单位
                    if(child.getIsFictitious()!=null&&child.getIsFictitious().intValue()==0){
                        hasOrgans = 1;
                    }else{//单位容器
                        hasVirtualNode = 1;
                    }
                }else{
                    //部门/处室
                    if(child.getIsFictitious()!=null&&child.getIsFictitious().intValue()==0){
                        hasOrganUnits = 1;
                    }else{//虚拟部门/处室
                        hasFictitiousUnits = 1;
                    }
                }
            }
        }
        organ.setHasVirtualNodes(hasVirtualNode);
        organ.setHasOrgans(hasOrgans);
        organ.setHasOrganUnits(hasOrganUnits);
        organ.setHasFictitiousUnits(hasFictitiousUnits);
        updateEntity(organ);
    }

    @Override
    public List<Object> getOrgans(Long pageIndex,int pageSize){
        return organDao.getOrgans(pageIndex, pageSize);
    }

    public void updateSortNum4RemoveDate(){
        organDao.test();
    }

    @Override
    public List<OrganEO> getOrgans(Long[] organIds,Boolean isContainsExternal){
        return organDao.getOrgans(organIds, isContainsExternal);
    }

    @Override
    public List<OrganEO> getSubOrgans(Long[] parentIds,Boolean isContainsExternal){
        return organDao.getSubOrgans(parentIds, isContainsExternal);
    }

    @Override
    public List<OrganNodeVO> getSubOrgans(Long parentId,String parentDn,String[] types, boolean isOnlyInternal,int flag){
        List<OrganNodeVO> nodes = null;
        String platformCode = null;
        //如果只获取本平台数据，那么先获取平台编码
        //		if(isOnlyInternal){
        //			PlatformEO platform = platformService.getCurrentPlatform();
        //			platformCode = platform.getCode();
        //		}
        //如果为空，那么默认获取跟节点
        //		if(StringUtils.isEmpty(parentDn)){
        //			parentDn = Constants.ROOT_DN;
        //		}
        //从LDAP中获取所有的子单位、部门以及虚拟部门数据
        //		List<OrganEO> ldapOrgans = ldapOrganService.getOrgansByPlatformCode(parentDn,types, platformCode);
        //		if(ldapOrgans!=null&&ldapOrgans.size()>0){
        //从DB中获取数据
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("parentId", parentId);
//		params.put("recordStatus", RecordStatus.Normal.toString());
        //			if(platformCode!=null){
        //				params.put("platformCode", platformCode);
        //			}
        List<OrganEO> dbOrgans = organDao.getOrgans(parentId);
        //DB与LDAP中的数据对比
        if(dbOrgans!=null&&dbOrgans.size()>0){
            nodes = new ArrayList<OrganNodeVO>(dbOrgans.size());
            //				Map<String,OrganEO> dbMap = new HashMap<String, OrganEO>(dbOrgans.size());
            //				for(OrganEO dbOrgan:dbOrgans){
            //					if(dbOrgan!=null&&!StringUtils.isEmpty(dbOrgan.getDn())){
            //						dbMap.put(dbOrgan.getDn(), dbOrgan);
            //					}
            //				}
            for(OrganEO dbOrgan:dbOrgans){
                //					if(dbOrgan!=null){
                //						OrganEO dbOrgan = dbMap.get(ldapOrgan.getDn());
                //LDAP与DB都存在的才显示
                //						if(dbOrgan!=null){
                OrganNodeVO node = getOrganNode(dbOrgan, flag);
                node.setCount(OrganEO.Type.Organ.toString().equals(node.getType())?getOrganChildCount(node.getOrganId()):null);
                nodes.add(node);

                //						}
                //					}
            }
        }
        //		}
        return nodes;
    }


    @Override
    public List<OrganNodeVO> getOrganNodeVOs(Long[] organIds, int flag) {
        List<OrganNodeVO> nodes = new ArrayList<OrganNodeVO>();
        List<OrganEO> dbOrgans = organDao.getOrgans(organIds,null);
        if(dbOrgans!=null&&dbOrgans.size()>0){
            for(OrganEO dbOrgan:dbOrgans){
                OrganNodeVO node = getOrganNode(dbOrgan, flag);
                node.setCount(OrganEO.Type.Organ.toString().equals(node.getType())?getOrganChildCount(node.getOrganId()):null);
                nodes.add(node);
            }
        }
        return nodes;
    }

    /**
     * 获取子单位数
     * @param organId
     * @return
     */
    private Integer getOrganChildCount(Long organId) {
        Integer count = 0;
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordStatus", RecordStatus.Normal.toString());
        params.put("parentId",organId);
        params.put("type",OrganEO.Type.Organ.toString());
        List<OrganEO> organs = getEntities(OrganEO.class,params);
        if(organs != null && organs.size() > 0 ){
            count = organs.size();
        }
        return count;
    }


    private OrganNodeVO getOrganNode(OrganEO dbOrgan,int flag){
        OrganNodeVO node = new OrganNodeVO();
        org.springframework.beans.BeanUtils.copyProperties(dbOrgan, node);
        //名称、简称以及描述需要从LDAP中读取
        node.setName(dbOrgan.getName());
        node.setSimpleName(dbOrgan.getSimpleName());
        node.setDescription(dbOrgan.getDescription());
        node.setHasOrgans(dbOrgan.getHasOrgans() == 1 ? true : false);
        node.setId(dbOrgan.getOrganId());
        node.setPid(dbOrgan.getParentId());
        node.setIsExternal(dbOrgan.getIsExternal());
        if (Integer.valueOf(dbOrgan.getHasOrgans()) == 1) {
            node.setIsParent(true);
        }
        node.setNodeType(dbOrgan.getType());
        node.setFictitious(dbOrgan.getIsFictitious());
        //		if (dbOrgan.getType().equals(TreeNodeVO.Type.Organ.toString())) {
        //			if(Integer.valueOf(dbOrgan.getIsFictitious()) == 1){
        //				node.setIcon(Icon.VirtualNode.getValue());
        //			}else{
        //				node.setIcon(Icon.Organ.getValue());
        //			}
        //		} else {
        //			if (Integer.valueOf(dbOrgan.getIsFictitious()) == 1) {
        //				node.setIcon(Icon.Virtual.getValue());
        //			} else {
        //				node.setIcon(Icon.OrganUnit.getValue());
        //			}
        //		}
        if (dbOrgan.getHasOrgans() == 1) {
            node.setHasOrgans(true);
        }
        if (dbOrgan.getHasPersons() == 1) {
            node.setHasPersons(true);
        }
        Integer hasVirtualNodes = dbOrgan.getHasVirtualNodes();
        Integer hasOrgans = dbOrgan.getHasOrgans();
        Integer hasOrganUnits = dbOrgan.getHasOrganUnits();
        Integer hasFictitiousUnits = dbOrgan.getHasFictitiousUnits();
        switch (flag) {
            case 0:
                if ((hasVirtualNodes!=null&&hasVirtualNodes==1)||hasOrgans == 1 || hasOrganUnits == 1|| hasFictitiousUnits == 1) {
                    node.setIsParent(true);
                }
                break;
            case 1:
                if (hasOrgans == 1 || hasOrganUnits == 1 || hasFictitiousUnits == 1|| dbOrgan.getHasPersons() == 1) {
                    node.setIsParent(true);
                }
                break;
            case 2:
                // 当组织或单位下不存在人员且不存在子组织和单位时，设置当前节点的isParent为false
                if (hasOrgans == 1 || dbOrgan.getHasRoles() == 1) {
                    node.setIsParent(true);
                }
                break;
            case 3:
                // 当只获取虚拟节点和单位时，flag=3
                if (hasOrgans == 1 || (hasVirtualNodes!=null&&hasVirtualNodes==1)) {
                    node.setIsParent(true);
                }
                break;
            default:
                break;
        }
        return node;
    }

    /**
     * 由于LDAP中的部分数据有可能与数据库不一致(外平台的且尚未同步的)，此时，我们需要以LDAP中的为准
     *
     * @param dbOrgan
     * @param ldapOrgan
     * @param flag
     * @return
     */
    private OrganNodeVO getOrganNode(OrganEO dbOrgan,OrganEO ldapOrgan,int flag){
        OrganNodeVO node = new OrganNodeVO();
        org.springframework.beans.BeanUtils.copyProperties(dbOrgan, node);
        //名称、简称以及描述需要从LDAP中读取
        node.setName(ldapOrgan.getName());
        node.setSimpleName(ldapOrgan.getSimpleName());
        node.setDescription(ldapOrgan.getDescription());
        node.setHasOrgans(dbOrgan.getHasOrgans() == 1 ? true : false);
        node.setId(dbOrgan.getOrganId());
        node.setPid(dbOrgan.getParentId());
        node.setIsExternal(dbOrgan.getIsExternal());
        if (Integer.valueOf(dbOrgan.getHasOrgans()) == 1) {
            node.setIsParent(true);
        }
        node.setNodeType(ldapOrgan.getType());
        node.setFictitious(ldapOrgan.getIsFictitious());
        //		if (dbOrgan.getType().equals(TreeNodeVO.Type.Organ.toString())) {
        //			if(Integer.valueOf(dbOrgan.getIsFictitious()) == 1){
        //				node.setIcon(Icon.VirtualNode.getValue());
        //			}else{
        //				node.setIcon(Icon.Organ.getValue());
        //			}
        //		} else {
        //			if (Integer.valueOf(dbOrgan.getIsFictitious()) == 1) {
        //				node.setIcon(Icon.Virtual.getValue());
        //			} else {
        //				node.setIcon(Icon.OrganUnit.getValue());
        //			}
        //		}
        if (dbOrgan.getHasOrgans() == 1) {
            node.setHasOrgans(true);
        }
        if (dbOrgan.getHasPersons() == 1) {
            node.setHasPersons(true);
        }
        Integer hasVirtualNodes = dbOrgan.getHasVirtualNodes();
        Integer hasOrgans = dbOrgan.getHasOrgans();
        Integer hasOrganUnits = dbOrgan.getHasOrganUnits();
        Integer hasFictitiousUnits = dbOrgan.getHasFictitiousUnits();
        switch (flag) {
            case 0:
                if ((hasVirtualNodes!=null&&hasVirtualNodes==1)||hasOrgans == 1 || hasOrganUnits == 1|| hasFictitiousUnits == 1) {
                    node.setIsParent(true);
                }
                break;
            case 1:
                if (hasOrgans == 1 || hasOrganUnits == 1 || hasFictitiousUnits == 1|| dbOrgan.getHasPersons() == 1) {
                    node.setIsParent(true);
                }
                break;
            case 2:
                // 当组织或单位下不存在人员且不存在子组织和单位时，设置当前节点的isParent为false
                if (hasOrgans == 1 || dbOrgan.getHasRoles() == 1) {
                    node.setIsParent(true);
                }
                break;
            case 3:
                // 当只获取虚拟节点和单位时，flag=3
                if (hasOrgans == 1 || (hasVirtualNodes!=null&&hasVirtualNodes==1)) {
                    node.setIsParent(true);
                }
                break;
            default:
                break;
        }
        return node;
    }

    public List<OrganEO> getInternalLdapOrgans(Long parentId) {
        String parentDn = null;
        if (parentId == null) {
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO parent = getEntity(OrganEO.class, parentId);
            parentDn = parent.getDn();
        }
        List<OrganEO> organs = getLdapOrgansWithOutCache(parentDn, parentId);
        return organs;
    }

    /**
     * 获取organId在organIds的组织
     *
     * @param organIds
     * @return
     */
    public List<OrganEO> getOrgansByOrganIds(Long[] organIds){
        List<OrganEO> organs = null;
        if(organIds!=null&&organIds.length>0){
            organs = organDao.getOrgansByOrganIds(organIds);
        }
        return organs;
    }

    /**
     * 根据单位ID集合删除单位记录
     *
     * @param organIds
     */
    @Override
    public void deleteByOrganIds(List<Long> organIds){
        if(organIds==null||organIds.size()<=0){
            return;
        }
        organDao.deleteByOrganIds(organIds);
    }

    /**
     * 获取所有的子孙单位、部门和虚拟部门主键集合
     *
     * @param organId
     * @return
     */
    public List<Long> getDescendantOrganIds(Long organId){
        return organDao.getDescendantOrganIds(organId);
    }

    @Override
    public void updateCode(Long organId,String code){
        organDao.updateCode(organId, code);
    }

    @Override
    public void deleteCodes(Long[] organIds){
        organDao.deleteCodes(organIds);
    }

    /**
     * 根据Dn初始化组织架构路径（从最后一级单位开始）
     */
    public void initOrganNamesWithDn(){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> organs = getEntities(OrganEO.class, params);
        if(organs!=null){
            for(OrganEO organ:organs){
                String simpleDn = LDAPUtil.getSimpleDn(organ.getDn());
                String name = organ.getName();
                OrganDnContainer.put(simpleDn, name);
            }
        }
    }

    /**
     * 组织架构全路径处理
     *
     * @param organs
     */
    private void addParentFullName(List<?> organs){
        //存储dn(第一级subDn)与组织名称的键值对
        Map<String,String> tempMap = new HashMap<String, String>(organs.size());
        for(Object organ:organs){
            Object[] values = (Object[])organ;
            String name = values[1].toString();
            String dn = values[4].toString();
            if(!StringUtils.isEmpty(name)&&!StringUtils.isEmpty(dn)){
                String topDn = null;
                String[] subDns = dn.split("-");
                topDn = subDns[0];
                tempMap.put(topDn, name);
            }
        }
        for(Object organ:organs){
            Object[] values = (Object[])organ;
            String name = values[1].toString();
            String dn = values[4].toString().replaceAll(",","-");
            String[] subDns = dn.split("-");
            if(subDns!=null&&subDns.length>0){
                //父组织架构全路径名称
                String fullName = "";
                for(String subDn:subDns){
                    String subName = tempMap.get(subDn);
                    if(!StringUtils.isEmpty(subName)){
                        if(StringUtils.isEmpty(fullName)){
                            fullName = subName;
                        }else{
                            fullName = subName.concat(">").concat(fullName);
                        }
                    }
                }
                fullName = fullName.concat(">").concat(name);
                values[10] = fullName;
            }
        }
    }
    @Override
    public Pagination getPagination4Code(PageQueryVO query){
        return organDao.getPagination4Code(query);
    }
    @Override
    public OrganEO getUnitByOrganDn(String organDn) {
        // 部门的dn以"ou="开头
        if (!organDn.startsWith("ou=")) {
            throw new BaseRunTimeException();
        }
        // 单位的dn以"o="开头
        String parentDn = LDAPUtil.getParentDn(organDn);
        while (!parentDn.startsWith("o=")) {
            parentDn = LDAPUtil.getParentDn(parentDn);
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("dn", parentDn);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getEntity(OrganEO.class, params);
    }

    @Override
    public List<String> getNames(Long[] organIds, int scope,
                                 String blurryNameOrPY) {
        String[] simpleDns = null;
        if (organIds != null && organIds.length > 0) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("organId", organIds);
            List<OrganEO> organs = getEntities(OrganEO.class, params);
            if (organs != null && organs.size() > 0) {
                int size = organs.size();
                simpleDns = new String[size];
                for (int i = 0; i < size; i++) {
                    OrganEO organ = organs.get(i);
                    simpleDns[i] = organ.getDn().split(",")[0];
                }
            }
        }
        return organDao.getNames(simpleDns, scope, blurryNameOrPY);
    }

    @Override
    public List<OrganEO> getOrganUnits(Long[] unitIds) {
        return organDao.getOrganUnits(unitIds);
    }

    @Override
    public boolean isNameUsable(Long parentId, String type, String name) {
        String parentDn = null;
        if (parentId == null) {
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO organ = getEntity(OrganEO.class, parentId);
            parentDn = organ.getDn();
        }
        return !ldapOrganService.isNameExisted(parentDn, type, name);
    }

    @Override
    public boolean isSortNumUsable(Long parentId, String type, Long sortNum) {
        String parentDn = null;
        if (parentId == null) {
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO parent = getEntity(OrganEO.class, parentId);
            parentDn = parent.getDn();
        }
        return !ldapOrganService.isSortNumExisted(parentDn, type,
                sortNum.toString());
    }

    @Override
    public boolean hasOrganSuns(Long parentId) {
        OrganEO organ = getEntity(OrganEO.class, parentId);
        return ldapOrganService.hasSuns(organ.getDn());
    }


    @Override
    public void save(OrganEO organ) {
        // 1.名称是否可用
        if (isNameExistedDB(organ.getParentId(), organ.getType(), organ.getName(),0)) {
            String key = null;
            if (TreeNodeVO.Type.Organ.toString().equals(organ.getType())) {
                key = "OrganEO.OrganizationNameRepeated";
            } else {
                key = "OrganEO.OrganizationUnitNameRepeated";
            }
            throw new BaseRunTimeException(TipsMode.Key.toString(), key);
        } else {
            //如果拼音未转换，那么处理拼音
            if(StringUtils.isEmpty(organ.getFullPy())){
                // 通过姓名获取姓名的简拼和全拼并设置到person中
                String simplePy = PinYinUtil.cn2FirstSpell(organ.getName());
                organ.setSimplePy(simplePy);
                String fullPy = PinYinUtil.cn2Spell(organ.getName());
                organ.setFullPy(fullPy);
            }
        }
        try {
            // 保存组织
            String parentDn = Constants.ROOT_DN;
            if (organ.getParentId() != null) {
                OrganEO parent = getEntity(OrganEO.class, organ.getParentId());
                parentDn = parent.getDn();
            }
            //构造DN
            String simpleDn = LDAPUtil.getSimpleDn();
            String dn = null;
            if (TreeNodeVO.Type.Organ.toString().equals(organ.getType())||TreeNodeVO.Type.VirtualNode.toString().equals(organ.getType())) {
                dn = "o=".concat(simpleDn).concat(",").concat(parentDn);
            }else{
                dn = "ou=".concat(simpleDn).concat(",").concat(parentDn);
            }
            organ.setDn(dn);
            //设置平台编码
            //			PlatformEO platform = platformService.getCurrentPlatform();
            //			if(platform != null){
            //				organ.setPlatformCode(platform.getCode());
            //			}
            organ.setIsExternalOrgan(Boolean.FALSE);
            saveEntity(organ);
            //保存组织架构路径相关信息
            OrganDnContainer.put(simpleDn, organ.getName());
            //是否保存ldap
            if(LdapOpenUtil.isOpen){
                ldapOrganService.save(simpleDn, organ);
            }
            //			logService.saveLog("【系统管理】新增单位/部门，名称："+organ.getName(), "OrganEO", "Add");
            //记录日志
            SysLog.log("【系统管理】新增单位/部门，名称："+organ.getName(),"OrganEO", CmsLogEO.Operation.Add.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException();
        }
        //		// 新增组织，通知其他业务执行对应的操作，例如rtx新增组织架构等
        //		Object obj = organ;
        //		OrganSubject.getInstance().saveNotify(obj);
    }

    @Override
    public Long delete(Long id) throws BusinessException {
        // 是否有子节点
        OrganEO organ = getEntity(OrganEO.class, id);
        //是否保存ldap
        if(LdapOpenUtil.isOpen) {
            boolean hasSuns = ldapOrganService.hasSuns(organ.getDn());
            if (hasSuns) {
                String key = ExceptionKey.HasOrgansException.getValue();
                if (organ.getHasOrgans() == 0 && organ.getHasPersons() == 1) {
                    key = ExceptionKey.HasPersonsException.getValue();
                } else {
                    if (organ.getHasRoles() == 1) {
                        key = ExceptionKey.HasRoleException.getValue();
                    }
                }
                throw new BusinessException(TipsMode.Key.toString(), key);
            }
            ldapOrganService.delete(organ.getDn());
        }
        Long parentId = organ.getParentId();
        // 删除
        delete(organ);
        //保存组织架构路径相关信息
        String simpleDn = LDAPUtil.getSimpleDn(organ.getDn());
        OrganDnContainer.remove(simpleDn);
        //		logService.saveLog("【系统管理】删除单位/部门，名称："+organ.getName(), "OrganEO", "Delete");
        SysLog.log("【系统管理】删除单位/部门，名称："+organ.getName(), "OrganEO", CmsLogEO.Operation.Delete.toString());
        //		// 删除组织，通知其他业务执行对应的操作，例如rtx新增组织架构等
        //		Object obj = organ;
        //		OrganSubject.getInstance().deleteNotify(obj);
        return parentId;
    }

    @Override
    public OrganEO update(Long organId, OrganEO organ) throws BusinessException {
        // 获取源组织/单位
        OrganEO src = getEntity(OrganEO.class, organId);
        String srcName = src.getName();
        // 验证姓名是否可用
        boolean isNameChanged = false;
        if (!organ.getName().equals(src.getName())) {
            isNameChanged = true;
            src.setName(organ.getName());
            // 通过姓名获取姓名的简拼和全拼并设置到person中
            String simplePy = PinYinUtil.cn2FirstSpell(organ.getName());
            src.setSimplePy(simplePy);
            String fullPy = PinYinUtil.cn2Spell(organ.getName());
            src.setFullPy(fullPy);
            //名称是否存在
            if (isNameExistedDB(src.getParentId(), src.getType(),src.getName(),1)) {
                String key = null;
                if (TreeNodeVO.Type.Organ.toString().equals(organ.getType())) {
                    key = "OrganEO.OrganizationNameRepeated";
                } else {
                    key = "OrganEO.OrganizationUnitNameRepeated";
                }
                throw new BaseRunTimeException(TipsMode.Key.toString(), key);
            }
        }
        src.setSimpleName(organ.getSimpleName());
        src.setSortNum(organ.getSortNum());
        src.setDescription(organ.getDescription());
        src.setSiteId(organ.getSiteId());
        src.setOfficeAddress(organ.getOfficeAddress());
        src.setOfficePhone(organ.getOfficePhone());
        src.setServeAddress(organ.getServeAddress());
        src.setServePhone(organ.getServePhone());
        src.setOrganUrl(organ.getOrganUrl());
        src.setCode(organ.getCode());
        src.setIsPublic(organ.getIsPublic());
        src.setHeadPerson(organ.getHeadPerson());
        src.setPositions(organ.getPositions());
        // 如果单位或部门在外部单位和非外部单位进行切换，那么所有的子单位以及单位下的人员全部要进行变更
        Boolean isExternal = organ.getIsExternal()==null?Boolean.FALSE:organ.getIsExternal();
        Boolean srcIsExternal = src.getIsExternal();
        // 只有内部单位和外部单位切换才需要考虑
        if (isExternal != srcIsExternal) {
            // 内部切换到外部
            if (isExternal) {
                organDao.updateIsExternal(src.getDn(), isExternal);
            }
            src.setIsExternal(organ.getIsExternal());
        }
        // 更新
        updateEntity(src);
        // 更新person记录中的组织名称
        if (isNameChanged) {
            organPersonService.updateOrganName(organId, organ.getName(),src.getType());
        }
        //是否保存ldap
        if(LdapOpenUtil.isOpen){
            ldapOrganService.update(src);
        }
        //保存组织架构路径相关信息
        String simpleDn = LDAPUtil.getSimpleDn(src.getDn());
        OrganDnContainer.put(simpleDn, src.getName());
        //		logService.saveLog("【系统管理】更新单位/部门，名称："+organ.getName(), "OrganEO", "Update");
        SysLog.log("【系统管理】更新单位/部门，名称："+organ.getName(), "OrganEO", CmsLogEO.Operation.Update.toString());
        // 更新组织，通知其他业务执行对应的操作，例如rtx新增组织架构等
        //		Object obj = src;
        //		Object[] objs = new Object[] { src, srcName};
        //		OrganSubject.getInstance().updateNotify(objs);
        return src;
    }

    @Override
    public List<OrganEO> getOrgans(Long parentId) {
        return organDao.getOrgans(parentId);
    }

    @Override
    public List<OrganEO> getOrgans(Long parentId, String type) {
        return organDao.getOrgans(parentId, type);
    }

    @Override
    public List<OrganEO> getOrgansByTypeAndName(String name, String type) {
        return organDao.getOrgansByTypeAndName(name, type);
    }

    @Override
    public OrganEO getLdapOrgan(Long organId) {
        OrganEO o = getEntity(OrganEO.class, organId);
        OrganEO organ = ldapOrganService.getOrgan(o.getDn());
        if (organ != null) {
            copyProtities(o, organ);
        }
        return organ;
    }

    @Override
    public List<OrganEO> getLdapOrgansWithCache(Long parentId) {
        String parentDn = null;
        if (parentId == null) {
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO parent = getEntity(OrganEO.class, parentId);
            parentDn = parent.getDn();
        }
        List<OrganEO> organs = null;
        // 先从缓存中获取
        //		if (isCacheOn && OrganCache.getInstance().containsKey(parentDn)
        //				&& !OrganCache.getInstance().isExpired(parentDn)) {
        //			Object object = OrganCache.getInstance().getValue(parentDn);
        //			organs = object == null ? null : (List<OrganEO>) object;
        //		} else {
        organs = getLdapOrgansWithOutCache(parentDn, parentId);
        //			OrganCache.getInstance().put(parentDn, organs);
        //		}
        return organs;
    }

    @Override
    public List<OrganEO> getLdapOrgansWithOutCache(String parentDn,Long parentId) {
        List<OrganEO> organs = new ArrayList<OrganEO>();
        List<OrganEO> ldapOrgans = ldapOrganService.getOrgans(parentDn);
        List<OrganEO> dbOrgans = getOrgans(parentId);
        Map<String, OrganEO> map = new HashMap<String, OrganEO>(dbOrgans.size());
        if (dbOrgans != null && dbOrgans.size() > 0) {
            for (OrganEO organ : dbOrgans) {
                // 通过dn来判断是否是对应记录
                map.put(organ.getDn(), organ);
            }
            if (ldapOrgans != null && ldapOrgans.size() > 0) {
                for (OrganEO organ : ldapOrgans) {
                    OrganEO o = map.get(organ.getDn());
                    if (o != null) {
                        copyProtities(o, organ);
                        organs.add(organ);
                    }
                }
            }
        }
        return organs;
    }

    @Override
    public List<OrganEO> getConnectedOrgans(Long[] parentIds,
                                            List<String> types, String name) {
        // 获取根节点对应的dn集合
        List<String> rootDns = new ArrayList<String>();
        if (parentIds == null || parentIds.length <= 0) {
            rootDns.add(Constants.ROOT_DN);
        } else {
            // 获取organIds对应的所有组织
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("organId", parentIds);
            params.put("recordStatus",
                    AMockEntity.RecordStatus.Normal.toString());
            List<OrganEO> parents = getEntities(OrganEO.class, params);
            for (OrganEO parent : parents) {
                rootDns.add(parent.getDn());
            }
        }
        List<OrganEO> organs = organDao
                .getConnectedOrgans(rootDns, types, name);
        if (organs != null && organs.size() > 0) {
            List<String> dns = new ArrayList<String>();
            for (OrganEO organ : organs) {
                String organDn = organ.getDn();
                // 如果当前单位dn为rootDns中的一个，那么直接跳出
                if (rootDns.contains(organDn)) {
                    continue;
                }
                // 获取所有非重复的组织/单位dn集合，每个单位的人员DN只执行一次逻辑
                String dn = LDAPUtil.getParentDn(organDn);
                while (!dns.contains(dn)) {
                    dns.add(dn);
                    // 到顶级单位就跳出循环
                    if (rootDns.contains(dn)) {
                        break;
                    }
                    dn = LDAPUtil.getParentDn(dn);
                }
            }
            List<OrganEO> os = getOrgansByDns(dns);
            if (os != null && os.size() > 0) {
                for (OrganEO organ : os) {
                    organs.add(organ);
                }
            }
        }
        return organs;
    }

    @Override
    public List<OrganEO> getOrgans(Long[] parentIds, List<String> types,
                                   int scope, String blurryName) {
        // 获取根节点对应的dn集合
        List<String> rootDns = new ArrayList<String>();
        if (parentIds == null || parentIds.length <= 0) {
            rootDns.add(Constants.ROOT_DN);
        } else {
            // 获取organIds对应的所有组织
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("organId", parentIds);
            params.put("recordStatus",
                    AMockEntity.RecordStatus.Normal.toString());
            List<OrganEO> parents = getEntities(OrganEO.class, params);
            for (OrganEO parent : parents) {
                rootDns.add(parent.getDn());
            }
        }
        // 从LDAP中读取组织/单位集合
        List<OrganEO> ldapOrgans = ldapOrganService.getOrgans(rootDns, types,
                scope, blurryName);
        if (ldapOrgans != null && ldapOrgans.size() > 0) {
            int size = ldapOrgans.size();
            List<String> organDns = new ArrayList<String>(size);
            for (OrganEO organ : ldapOrgans) {
                if (organ != null && !StringUtils.isEmpty(organ.getDn())) {
                    organDns.add(organ.getDn());
                }
            }
            Map<String, Object> oparams = new HashMap<String, Object>();
            oparams.put("dn", organDns);
            oparams.put("recordStatus",
                    AMockEntity.RecordStatus.Normal.toString());
            List<OrganEO> dbOrgans = getEntities(OrganEO.class, oparams);
            if (dbOrgans != null && dbOrgans.size() > 0) {
                Map<String, OrganEO> map = new HashMap<String, OrganEO>(
                        dbOrgans.size());
                for (OrganEO organ : dbOrgans) {
                    // 通过dn来判断是否是对应记录
                    map.put(organ.getDn(), organ);
                }
                if (dbOrgans != null && dbOrgans.size() > 0) {
                    for (OrganEO organ : ldapOrgans) {
                        OrganEO o = map.get(organ.getDn());
                        copyProtities(o, organ);
                    }
                }
            }
        }
        return ldapOrgans;
    }

    @Override
    public List<OrganEO> getSubVirtualNodesAndUnits(Long parentId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("parentId", parentId);
        params.put("type", OrganEO.Type.Organ.toString());
        params.put("isExternalOrgan", Boolean.FALSE);
        params.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> units = getEntities(OrganEO.class, params);
        if(units!=null&&units.size()>0){
            Collections.sort(units, new Comparator<OrganEO>() {
                @Override
                public int compare(OrganEO o1, OrganEO o2) {
                    int flag = 0;
                    if (o1.getSortNum() == null) {
                        flag = -1;
                    } else if (o2.getSortNum() == null) {
                        flag = 1;
                    } else {
                        flag = o1.getSortNum().compareTo(o2.getSortNum());
                    }
                    return flag;
                }
            });
        }
        return units;
    }

    @Override
    public List<OrganEO> getLdapOrgans(Long parentId, String type) {
        String parentDn = null;
        if (parentId == null) {
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO parent = getEntity(OrganEO.class, parentId);
            parentDn = parent.getDn();
        }
        List<OrganEO> ldapOrgans = ldapOrganService.getOrgans(parentDn, type);
        List<OrganEO> dbOrgans = getOrgans(parentId, type);
        Map<String, OrganEO> map = new HashMap<String, OrganEO>(dbOrgans.size());
        for (OrganEO organ : dbOrgans) {
            // 通过dn来判断是否是对应记录
            map.put(organ.getDn(), organ);
        }
        for (OrganEO organ : ldapOrgans) {
            OrganEO o = map.get(organ.getDn());
            copyProtities(o, organ);
        }
        return ldapOrgans;
    }


    @Override
    public void updateHasPersons(Long organId, int hasPersons) {
        OrganEO organ = getEntity(OrganEO.class, organId);
        if (organ.getHasPersons().intValue() != hasPersons) {
            organ.setHasPersons(hasPersons);
            updateEntity(organ);
        }
    }

    @Override
    public Long getMaxSortNum(Long parentId) {
        return organDao.getMaxSortNum(parentId);
    }

    @Override
    public List<OrganEO> getAncestors(Long parentId) {
        List<OrganEO> organs = null;
        if (parentId != null) {
            organs = new ArrayList<OrganEO>();
            getOrgans(parentId, organs);
        }
        return organs;
    }

    private void getOrgans(Long parentId, List<OrganEO> organs) {
        if (parentId != null) {
            OrganEO organ = getEntity(OrganEO.class, parentId);
            organs.add(organ);
            if (organ.getParentId() != null) {
                getOrgans(organ.getParentId(), organs);
            }
        }
    }

    @Override
    public void updateHasPersons(Long organId) {
        if (organId == null) {
            throw new NullPointerException();
        }
        Long count = personService.getSubPersonCount(organId);
        Integer hasPersons = Integer.valueOf(0);
        if(count>0){
            hasPersons = Integer.valueOf(1);
        }
        organDao.updateHasPersons(organId, hasPersons);
    }

    @Override
    public Long getSunOrgansCount(Long organId) {
        return organDao.getSunOrgansCount(organId);
    }

    @Override
    public Long getSunPersonsCount(Long organId) {
        return organDao.getSunPersonsCount(organId);
    }

    @Override
    public OrganEO getOrganByDn(String dn) {
        return organDao.getOrganByDn(dn);
    }

    @Override
    public List<OrganEO> getSubOrgans(Long[] organIds, List<String> types,
                                      boolean isContainsFictitious, String fuzzyName) {
//		// 获取所有父节点对应的DN，通过这些DN到LDAP中获取子组织
//		List<String> dns = new ArrayList<String>();
//		if (organIds == null || organIds.length <= 0) {// 获取顶级Organs
//			dns.add(Constants.ROOT_DN);
//		} else {
//			Map<String, Object> params = new HashMap<String, Object>();
//			params.put("organId", organIds);
//			params.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
//			List<OrganEO> parents = getEntities(OrganEO.class, params);
//			if (parents != null && parents.size() > 0) {
//				for (OrganEO organ : parents) {
//					dns.add(organ.getDn());
//				}
//			}
//		}
        List<OrganEO> organs = new ArrayList<OrganEO>();
//		// 从ldap上获取数据
//		for (String dn : dns) {
//			List<OrganEO> ldapOrgans = ldapOrganService.getOrgans(dn, types,isContainsFictitious);
//			if (ldapOrgans != null && ldapOrgans.size() > 0) {
//				organs.addAll(ldapOrgans);
//			}
//		}
        // 本地数据库获取数据，对从ldap上获取的集合进行补充
//		if (organs != null && organs.size() > 0) {
        List<OrganEO> dbOrgans = organDao.getSubOrgans(organIds, types,isContainsFictitious, fuzzyName);
//		if (dbOrgans != null && dbOrgans.size() > 0) {
//			Map<String, OrganEO> map = new HashMap<String, OrganEO>(dbOrgans.size());
//			for (OrganEO o : dbOrgans) {
//				map.put(o.getDn(), o);
//			}
//			Iterator<OrganEO> iterator = organs.iterator();
//			while (iterator.hasNext()) {
//				OrganEO organ = iterator.next();
//				String dn = organ.getDn();
//				OrganEO o = map.get(dn);
//				if (o == null) {
//					// LDAP中有，但数据库中没有，那么不显示
//					iterator.remove();
//				} else {
//					// 通过数据库中的属性补充LDAP中缺少的属性值
//					copyProtities(o, organ);
//					organ.setIsExternalOrgan(o.getIsExternalOrgan());
//				}
//			}
//		}
//		}
        return dbOrgans;
    }

    /**
     * 获取子单位节点
     * @param types  返回的节点类型
     * @param isContainsExternal 是否包含外单位
     * @return
     */
    @Override
    public List<OrganEO> getSubNorgans(Long parentId,String[] types,Boolean isContainsExternal){
        // 获取所有父节点对应的DN，通过这些DN到LDAP中获取子组织
        String parentDn = null;
        if (parentId == null) {// 获取顶级Organs
            parentDn = Constants.ROOT_DN;
        } else {
            OrganEO parent = getEntity(OrganEO.class, parentId);
            parentDn = parent.getDn();
        }
        String platformCode = null;
        if(!isContainsExternal){
            //获取本平台编码
            platformCode = platformService.getCurrentPlatform().getCode();
        }
        // 从ldap上获取数据
        List<OrganEO> organs = ldapOrganService.getOrgansByPlatformCode(parentDn, types, platformCode);
        // 本地数据库获取数据，对从ldap上获取的集合进行补充
        if (organs != null && organs.size() > 0) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("parentId", parentId);
            params.put("recordStatus", RecordStatus.Normal.toString());
            if(!StringUtils.isEmpty(platformCode)){
                params.put("platformCode",platformCode);
            }
            List<OrganEO> dbOrgans = getEntities(OrganEO.class, params);
            if (dbOrgans != null && dbOrgans.size() > 0) {
                Map<String, OrganEO> map = new HashMap<String, OrganEO>(dbOrgans.size());
                for (OrganEO o : dbOrgans) {
                    map.put(o.getDn(), o);
                }
                Iterator<OrganEO> iterator = organs.iterator();
                while (iterator.hasNext()) {
                    OrganEO organ = iterator.next();
                    String dn = organ.getDn();
                    OrganEO o = map.get(dn);
                    if (o == null) {
                        // LDAP中有，但数据库中没有，那么不显示
                        iterator.remove();
                    } else {
                        // 通过数据库中的属性补充LDAP中缺少的属性值
                        copyProtities(o, organ);
                        organ.setIsExternalOrgan(o.getIsExternalOrgan());
                    }
                }
            }
        }
        return organs;
    }

    public static void main(String[] args) {
        String dn = "ou=sljfls,ou=sfkshdfk,o=sdfskdjf,o=skfdhksdhm,dc=sdjfl,dc=cn";

        System.out.println("o="+dn);
    }

    /**
     * 获取部门直属单位
     *
     * @param organIds
     * @return
     */
    @Override
    public List<OrganEO> getDirrectlyUpLevelUnits(Long[] organIds){
        if(organIds==null||organIds.length<=0){
            return null;
        }
        List<OrganEO> units = new ArrayList<OrganEO>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("organId", organIds);
        map.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> organs = getEntities(OrganEO.class, map);
        Map<String, String> dnMap = new HashMap<String, String>();
        if(organs!=null&&organs.size()>0){
            List<String> dns = new ArrayList<String>(organs.size());
            for(OrganEO organ:organs){
                String dn = organ.getDn();
                String unitDn = "o="+org.apache.commons.lang3.StringUtils.substringAfter(dn, "o=");
                dns.add(unitDn);
                dnMap.put(organ.getOrganId().toString(), unitDn);
            }
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("dn", dns);
            params.put("recordStatus", RecordStatus.Normal.toString());
            List<OrganEO> allUnits = getEntities(OrganEO.class, params);
            Map<String, OrganEO> unitMap = new HashMap<String, OrganEO>();
            if(allUnits!=null&&allUnits.size()>0){
                for(OrganEO unit:allUnits){
                    unitMap.put(unit.getDn(), unit);
                }
            }
            //返回内容排序，需要跟入参的organId保持一致
            for(Long organId:organIds){
                if(organId!=null){
                    String dn = dnMap.get(organId.toString());
                    OrganEO unit = unitMap.get(dn);
                    units.add(unit);
                }
            }
        }
        return units;
    }

    @Override
    public OrganEO getDirectlyUpLevelUnit(Long organId) {
        OrganEO organ = getEntity(OrganEO.class, organId);
        OrganEO unit = null;
        if(organ!=null){
            Long parentId = organ.getParentId();
            if (parentId != null) {
                unit = getEntity(OrganEO.class, parentId);
                // 返回的要么为空，要么节点类型为Organ的对象
                while (!unit.getType().equals(TreeNodeVO.Type.Organ.toString())) {
                    parentId = unit.getParentId();
                    if (parentId == null) {
                        break;
                    }
                    unit = getEntity(OrganEO.class, parentId);
                }
            }
        }
        return unit;
    }

    @Override
    public List<OrganEO> getOrgansByDns(List<String> dns) {
        return organDao.getOrgansByDns(dns);
    }

    @Override
    public List<Long> getOrganIdsByDn(String dn){
        return organDao.getOrganIdsByDn(dn);
    }

    @Override
    public String[] getDns(Long[] organIds) {
        // 获取跟组织/单位对应的dn
        String[] organDns = null;
        if (organIds == null || organIds.length <= 0) {
            return new String[] { Constants.ROOT_DN };
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("organId", organIds);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<OrganEO> organs = getEntities(OrganEO.class, map);
        if (organs == null || organs.size() <= 0) {
            organDns = new String[] { cn.lonsun.ldap.internal.util.Constants.ROOT_DN };
        } else {
            int size = organs.size();
            organDns = new String[size];
            for (int i = 0; i < size; i++) {
                OrganEO organ = organs.get(i);
                if (organ != null) {
                    // 将dn存放到数组中
                    organDns[i] = organ.getDn();
                }
            }
        }
        return organDns;
    }

    /**
     * 将数据库中的部分属性值拷贝到从Ldap上获取的对象中
     *
     * @param source
     * @param target
     */
    private void copyProtities(OrganEO source, OrganEO target) {
        target.setOrganId(source.getOrganId());
        target.setCreateDate(source.getCreateDate());
        target.setCreateUserId(source.getCreateUserId());
        target.setParentId(source.getParentId());
        target.setUpdateDate(source.getUpdateDate());
        target.setUpdateUserId(source.getUpdateUserId());
        target.setHasOrgans(source.getHasOrgans());
        target.setHasOrganUnits(source.getHasOrganUnits());
        target.setHasRoles(source.getHasRoles());
        target.setHasFictitiousUnits(source.getHasFictitiousUnits());
        target.setHasPersons(source.getHasPersons());
        target.setIsFictitious(source.getIsFictitious());
    }
    /**
     * 删除单位部门人员关联
     */
    public void deleteOrgans(OrganEO clickOrgan) {
        //部门
        if(clickOrgan.getType().equals(OrganEO.Type.OrganUnit.toString())){
            deleteOrganPerson(clickOrgan.getOrganId());
        }else{//单位
            deleteOrgan(clickOrgan.getOrganId());
        }
    }

    /**
     * 根据部门id删除人员
     * @param organUnitId
     */
    private void deleteOrganPerson(Long organUnitId) {
        List<PersonEO> list=personService.getPersons(organUnitId);
        if(list!=null && list.size()>0){
            for(PersonEO p:list){
                organPersonService.deleteByPersonId(p.getPersonId());
            }
        }
    }

    /**
     * 递归单位id 删除部门人员关系
     * @param organId
     */
    private void deleteOrgan(Long organId) {
        List<OrganEO> list=organDao.getOrgans(organId);
        if(list!=null && list.size()>0){
            for(OrganEO o:list){
                //保存dn
                if(o.getType().equals(OrganEO.Type.OrganUnit.toString())){
                    deleteOrganPerson(o.getOrganId());
                }else{
                    deleteOrgan(o.getOrganId());
                }
            }
        }
    }


    //递归部门
    private void saveOrgan(OrganEO organ) {
        List<OrganEO> list=organDao.getOrgans(organ.getOrganId());
        if(list!=null && list.size()>0){
            for(OrganEO o:list){
                //保存组织ldap
                saveOrganEOLdap(o);
                //判断是单位还是部门,部门则保存人员
                if(o.getType().equals(OrganEO.Type.OrganUnit.toString())){
                    savePersonLdap(o.getOrganId());
                }
                saveOrgan(o);
            }
        }
    }


    //更新人员至ldap
    private void savePersonLdap(Long organId) {
        List<PersonEO> list=personService.getPersons(organId);
        if(list!=null && list.size()>0){
            for(PersonEO p:list){
                //更新人员至ldap
                personService.savePersonLdap(p);
            }
        }
    }

    //更新单位至ldap
    private void saveOrganEOLdap(OrganEO organ) {
        try {
            // 保存组织
            String parentDn = Constants.ROOT_DN;
            if (organ.getParentId() != null) {
                OrganEO parent = getEntity(OrganEO.class, organ.getParentId());
                parentDn = parent.getDn();
            }
            //构造DN
            String simpleDn = LDAPUtil.getSimpleDn();
            String dn = null;
            if (TreeNodeVO.Type.Organ.toString().equals(organ.getType())||TreeNodeVO.Type.VirtualNode.toString().equals(organ.getType())) {
                dn = "o=".concat(simpleDn).concat(",").concat(parentDn);
            }else{
                dn = "ou=".concat(simpleDn).concat(",").concat(parentDn);
            }
            organ.setDn(dn);
            //更新组织
            updateEntity(organ);
            if(LdapOpenUtil.isOpen){
                //LDAP最后保存
                ldapOrganService.save(simpleDn, organ);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseRunTimeException();
        }
    }


    /**
     * 数据迁移
     * clickOrgan  需迁移的组织
     * selectOrgan 迁移至组织
     */
    @Override
    public void updateOrgansAndPerson(OrganEO selectOrgan, OrganEO clickOrgan) {
        Long clickOrganPid= clickOrgan.getParentId();
        String clickOrganDn=clickOrgan.getDn();
        //设置需迁移组织的父id和dn
        clickOrgan.setParentId(selectOrgan.getOrganId());
        //更新需迁移的组织信息
        this.updateEntity(clickOrgan);

        //保存至ldap和OrganPerson信息
        saveOrganEOLdap(clickOrgan);
        //如果是部门，保存人员信息
        if(clickOrgan.getType().equals(OrganEO.Type.OrganUnit.toString())){
            savePersonLdap(clickOrgan.getOrganId());
        }
        //递归保存至ldap
        saveOrgan(clickOrgan);
        //是否开启ldap
        if(LdapOpenUtil.isOpen){
            //查询节点下所有子节点
            List<DeleteDnVO> listDns = getDeleteDns(clickOrganDn);
            if(listDns!=null && listDns.size()>0){
                //dn排序
                Collections.sort(listDns,new Comparator<DeleteDnVO>() {
                    public int compare(DeleteDnVO dn1, DeleteDnVO dn2) {
                        Integer dnLength1=dn1.getDn().length();
                        Integer dnLength2=dn2.getDn().length();
                        return dnLength2.compareTo(dnLength1);
                    }
                });
                //删除dn
                for(DeleteDnVO dnObj:listDns){
                    try{
                        ldapOrganService.delete(dnObj.getDn());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
        //记录日志
        SysLog.log("【系统管理】移动单位，名称："+selectOrgan.getName()+"，移动到："+clickOrgan.getName(),"OrganEO", CmsLogEO.Operation.Update.toString());
    }

    private List<DeleteDnVO> getDeleteDns(String clickOrganDn) {
        DirContext dc = null;
        List<DeleteDnVO> listDns = new ArrayList<DeleteDnVO>();
        try {
            dc = LDAPUtil.getDirContext();
            // 查询条件
            SearchControls sc = new SearchControls();
            sc.setSearchScope(SearchControls.SUBTREE_SCOPE);
            // LDAP查询
            String filter = "objectClass=*";
            NamingEnumeration<SearchResult> ne = dc.search(clickOrganDn, filter, sc);
            // 针对查询结果构建人员信息
            while (ne.hasMore()) {
                String dn=ne.next().getNameInNamespace();
                listDns.add(new DeleteDnVO("DN",dn));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listDns;
    }

    @Override
    public List<TreeNodeVO> getAllOrgans() {
        List<OrganEO> organs = organDao.getAllOrgans(null);
        List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
        if (organs != null && organs.size() > 0) {
            for (OrganEO organ : organs) {
                nodes.add(getOrganTreeNode(null,organ));
            }
        }

        List<PersonEO> persons = personService.getAllPersons();
        if (persons != null && persons.size() > 0) {
            for (PersonEO p : persons) {
                nodes.add(getPersonTreeNode(p));
            }
        }
        return nodes;
    }

    @Override
    public List<TreeNodeVO> getAllOrgans(String name) {
        List<OrganEO> organs = organDao.getAllOrgans(null);
        List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
        if (organs != null && organs.size() > 0) {
            for (OrganEO organ : organs) {
                nodes.add(getOrganTreeNode(null,organ));
            }
        }

        List<PersonEO> persons = personService.getPersonsByLikeName(null,name);
        if (persons != null && persons.size() > 0) {
            for (PersonEO p : persons) {
                nodes.add(getPersonTreeNode(p));
            }
        }
        return nodes;
    }

    @Override
    public List<TreeNodeVO> getUnitOrganAndUser(Long unitId) {
        Long[] unitIds = {unitId};
        List<OrganEO> organs = organDao.getOrganUnits(unitIds);
        List<OrganEO> units = organDao.getOrgansByOrganIds(unitIds);
        if(units != null) {
            organs.addAll(units);
        }
        List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
        if (organs != null && organs.size() > 0) {
            for (OrganEO organ : organs) {
                nodes.add(getOrganTreeNode(null,organ));
            }
        }

        List<PersonEO> persons = personService.getPsersonsByUnitId(unitId);
        if (persons != null && persons.size() > 0) {
            for (PersonEO p : persons) {
                nodes.add(getPersonTreeNode(p));
            }
        }
        return nodes;
    }

    @Override
    public List<TreeNodeVO> getUnitsAndPersons(Long unitId) {
        OrganEO eo = getEntity(OrganEO.class, unitId);
        List<TreeNodeVO> nodes = null;
        if(eo !=null){
            nodes = new ArrayList<TreeNodeVO>();
            List<OrganEO> organs = organDao.getOrgansByDn(eo.getDn(),null);
            List<PersonEO> persons = personService.getPersonsByOrganDn(eo.getDn());
            if (organs != null && organs.size() > 0) {
                for (OrganEO organ : organs) {
                    nodes.add(getOrganTreeNode(null,organ));
                }
            }
            if (persons != null && persons.size() > 0) {
                for (PersonEO p : persons) {
                    nodes.add(getPersonTreeNode(p));
                }
            }
        }
        return nodes;
    }

    @Override
    public List<TreeNodeVO> getUnitsAndPersons(Long unitId, String name) {
        OrganEO eo = getEntity(OrganEO.class, unitId);
        List<TreeNodeVO> nodes = null;
        if(eo !=null){
            nodes = new ArrayList<TreeNodeVO>();
            List<OrganEO> organs = organDao.getOrgansByDn(eo.getDn(),null);
            List<PersonEO> persons = personService.getPersonsByOrganDn(eo.getDn(),name);
            if (organs != null && organs.size() > 0) {
                for (OrganEO organ : organs) {
                    nodes.add(getOrganTreeNode(null,organ));
                }
            }
            if (persons != null && persons.size() > 0) {
                for (PersonEO p : persons) {
                    nodes.add(getPersonTreeNode(p));
                }
            }
        }
        return nodes;
    }

    private TreeNodeVO getPersonTreeNode(PersonEO person) {
        TreeNodeVO node = new TreeNodeVO();
        String id = PersonEO.class.getSimpleName().concat(
                person.getPersonId().toString());
        node.setId(id);
        Long organId = person.getOrganId();
        if (organId != null) {
            String pid = OrganEO.class.getSimpleName().concat(
                    organId.toString());
            node.setPid(pid);
        }
        //是否外平台
        node.setIsExternalPerson(person.getIsExternalPerson());
        //平台编码
        node.setPlatformCode(person.getPlatformCode());
        node.setPersonId(person.getPersonId());
        node.setPersonName(person.getName());
        node.setUserId(person.getUserId());
        node.setName(person.getName());
        node.setOrganId(person.getOrganId());
        node.setOrganName(person.getOrganName());
        node.setUnitId(person.getUnitId());
        node.setUnitName(person.getUnitName());
        node.setMobile(person.getMobile());
        node.setDn(person.getDn());
        // 性别
        node.setIcon(Icon.Male.getValue());
        node.setType(TreeNodeVO.Type.Person.toString());
        return node;
    }
    /**
     * 构造组织、单位和虚拟单位对应的TreeNodeVO对象
     *
     * @param nodeTypes
     * @param organ
     * @return
     */
    private TreeNodeVO getOrganTreeNode(String[] nodeTypes, OrganEO organ) {
        TreeNodeVO node = new TreeNodeVO();
        String id = OrganEO.class.getSimpleName().concat(organ.getOrganId().toString());
        node.setId(id);
        node.setDn(organ.getDn());
        //是否外平台
        node.setIsExternalOrgan(organ.getIsExternalOrgan());
        //平台编码
        node.setPlatformCode(organ.getPlatformCode());
        Long parentId = organ.getParentId();
        if (parentId != null) {
            String pid = OrganEO.class.getSimpleName().concat(
                    parentId.toString());
            node.setPid(pid);
        }
        node.setName(organ.getName());
        // 节点类型
        if (organ.getType().equals(TreeNodeVO.Type.Organ.toString())) {
            if (organ.getIsFictitious() == 1) {
                node.setType(TreeNodeVO.Type.VirtualNode.toString());
            } else {
                node.setType(TreeNodeVO.Type.Organ.toString());
            }
            // 此处对应前端的单位
            node.setUnitId(organ.getOrganId());
            node.setUnitName(organ.getName());
        } else {
            if (organ.getIsFictitious() == 1) {
                node.setType(TreeNodeVO.Type.Virtual.toString());
            } else {
                node.setType(TreeNodeVO.Type.OrganUnit.toString());
            }
            // 此处对应前端的部门/处室
            node.setOrganId(organ.getOrganId());
            node.setOrganName(organ.getName());
        }
        // 是否是父节点处理
        Boolean isParent = Boolean.FALSE;
        if (nodeTypes == null) {
            isParent = Boolean.TRUE;
        } else {
            for (String nodeType : nodeTypes) {
                if (nodeType.equals(TreeNodeVO.Type.VirtualNode.toString())) {
                    if (organ.getHasVirtualNodes()!=null&&organ.getHasVirtualNodes() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Organ.toString())) {
                    if (organ.getHasOrgans() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.OrganUnit.toString())) {
                    if (organ.getHasOrganUnits() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Virtual.toString())) {
                    if (organ.getHasFictitiousUnits() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
                if (nodeType.equals(TreeNodeVO.Type.Person.toString())) {
                    if (organ.getHasPersons() == 1) {
                        isParent = Boolean.TRUE;
                        break;
                    }
                }
            }
        }
        node.setIsParent(isParent);
        return node;
    }

    @Override
    public List<OrganEO> getOrgansByUnitId(Long unitId) {
        List<OrganEO> organs = null;
        OrganEO organ = getEntity(OrganEO.class, unitId);
        if(organ != null){
            organs = new ArrayList<OrganEO>();
            List<OrganEO> organClilds  = organDao.getOrgansByDn(organ.getDn(),null);
            if(organClilds != null && organClilds.size()>0){
                organs.addAll(organClilds);
            }
        }
        return organs;
    }

    @Override
    public List<OrganEO> getOrgansBySiteId(Long siteId) {
        return organDao.getOrgansBySiteId(siteId);
    }

    @Override
    public List<OrganVO> getOrgansBySiteId(Long siteId, Boolean isRemoveTop) {
        SiteConfigEO siteConfigEO = CacheHandler.getEntity(SiteConfigEO.class, CacheGroup.CMS_INDICATORID, siteId);
        if(siteConfigEO == null && StringUtils.isEmpty(siteConfigEO.getUnitIds())){
            return null;
        }
        OrganEO unit = getEntity(OrganEO.class, Long.parseLong(siteConfigEO.getUnitIds()));
        if(unit == null){return null; }
        return organDao.getOrganVOsByDn(unit.getDn(),isRemoveTop);
    }

    @Override
    public List<OrganEO> getOrgansByType(Long siteId,String type) {
        List<OrganEO> organs = new ArrayList<OrganEO>();
        List<OrganEO> siteOrgans = getOrgansBySiteId(siteId);
        if(siteOrgans !=null && siteOrgans.size()>0){
            for(OrganEO organ:siteOrgans){
                List<OrganEO> organClilds  = organDao.getOrgansByDn(organ.getDn(),type);
                if(organClilds != null && organClilds.size()>0){
                    organs.addAll(organClilds);
                }
            }
        }

        return organs;
    }

    @Override
    public List<OrganEO> getOrgansByDn(Long unitId,String type){
        OrganEO unit = getEntity(OrganEO.class, unitId);
        List<OrganEO> organs = null;
        if(unit !=null){
            if(OrganEO.Type.Organ.toString().equals(type)){
                organs = organDao.getOrgansByDn(unit.getDn(),OrganEO.Type.Organ.toString());
            }else if(OrganEO.Type.OrganUnit.toString().equals(type)){
                organs = organDao.getOrgansByDn(unit.getDn(),OrganEO.Type.OrganUnit.toString());
            }
        }
        return organs;
    }

    @Override
    public List<OrganEO> getUnits() {
        return organDao.getAllOrgans(OrganEO.Type.Organ.toString());

    }

    @Override
    public List<TreeNodeVO> getParentOrgansById(Long id) {
        List<OrganEO> organs = organDao.getParentOrgansById(id);
        List<TreeNodeVO> nodes = new ArrayList<TreeNodeVO>();
        if (organs != null && organs.size() > 0) {
            for (OrganEO organ : organs) {
                nodes.add(getOrganTreeNode(null, organ));
            }
        }
        return nodes;
    }

    @Override
    public List<TreeNodeVO> getOrgansAndPersons(Long unitId,String name) {
        List<PersonEO> personEOs = personService.getPersonsByLikeName(unitId,name);
        Map<String,TreeNodeVO> mapnodes = new HashMap<String, TreeNodeVO>();
        if(null != personEOs) {
            for(PersonEO eo : personEOs) {
                TreeNodeVO person = getPersonTreeNode(eo);
                if(null != person) {
                    mapnodes.put(person.getId(),person);
                }
                List<TreeNodeVO> list = getParentOrgansById(eo.getOrganId());
                if(null != list) {
                    for(TreeNodeVO vo : list) {
                        mapnodes.put(vo.getId(), vo);
                    }
                }
            }
        }
        List<TreeNodeVO> listnodes = new ArrayList<TreeNodeVO>();
        Iterator it = mapnodes.keySet().iterator();
        while(it.hasNext()){
            String key = it.next().toString();
            listnodes.add(mapnodes.get(key));
        }
        return listnodes;
    }

    /**
     * 批量保存单位
     * @param organ
     */
    @Override
    public OrganEO saveXlsOrgan(OrganEO organ) {
        OrganEO o = null;
        // 1.名称是否可用
        try {
            if (!isNameExistedDB(organ.getParentId(), organ.getType(), organ.getName(),0)) {
                //如果拼音未转换，那么处理拼音
                if (StringUtils.isEmpty(organ.getFullPy())) {
                    // 通过姓名获取姓名的简拼和全拼并设置到person中
                    String simplePy = PinYinUtil.cn2FirstSpell(organ.getName());
                    organ.setSimplePy(simplePy);
                    String fullPy = PinYinUtil.cn2Spell(organ.getName());
                    organ.setFullPy(fullPy);
                }
                // 保存组织
                String parentDn = Constants.ROOT_DN;
                if (organ.getParentId() != null) {
                    OrganEO parent = getEntity(OrganEO.class, organ.getParentId());
                    parentDn = parent.getDn();
                }
                //构造DN
                String simpleDn = LDAPUtil.getSimpleDn();
                String dn = null;
                if (TreeNodeVO.Type.Organ.toString().equals(organ.getType()) || TreeNodeVO.Type.VirtualNode.toString().equals(organ.getType())) {
                    dn = "o=".concat(simpleDn).concat(",").concat(parentDn);
                } else {
                    dn = "ou=".concat(simpleDn).concat(",").concat(parentDn);
                }
                organ.setDn(dn);
                organ.setIsExternalOrgan(Boolean.FALSE);
                saveEntity(organ);
                //保存组织架构路径相关信息
                OrganDnContainer.put(simpleDn, organ.getName());
                if(LdapOpenUtil.isOpen) {
                    //LDAP最后保存
                    ldapOrganService.save(simpleDn, organ);
                }
                o = organ;
                //记录日志
                SysLog.log("【系统管理】导入单位/部门，名称："+organ.getName(),"OrganEO", CmsLogEO.Operation.Add.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }

    @Override
    public List<OrganEO> getOrganUnitsByDn(String dn) {
        return organDao.getOrganUnitsByDn(dn);
    }

    @Override
    public void initSimpleOrgansCache() {
//		String platformCode = platformService.getCurrentPlatform().getCode();
        // 所有的单位、部门以及虚拟部门
        List<?> organs1 = organDao.getPersonInfosByPlatformCode(null, null,
                null);
        if (organs1 != null && organs1.size() > 0) {
            addParentFullName(organs1);
        }
        String simpleOrgans1 = JSONHelper.toJSON(organs1);
        // 所有的单位、部门
        List<?> organs2 = organDao.getPersonInfosByPlatformCode(null,
                Integer.valueOf(0), null);
        String simpleOrgans2 = JSONHelper.toJSON(organs2);
        // 所有的单位
        List<?> organs3 = organDao.getPersonInfosByPlatformCode(
                OrganEO.Type.Organ.toString(), null, null);
        String simpleOrgans3 = JSONHelper.toJSON(organs3);
        // 更新js文件
        String js = "var ALLORGANS_ARRAY= " + simpleOrgans1;
        FileUtils.writeNew(js, InitServlet.organsJsPath);
        //更新时间戳
        TimeCutUtil.put(TimeCutUtil.CacheKey.OrganUpdateDate.toString(),new Date().getTime() + "");
    }

    @Override
    public List<OrganEO> getPublicOrgans(Long organId) {
        if(organId == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "organId不能为空");
        }
        OrganEO organ = getEntity(OrganEO.class,organId);
        if(organ == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "organId不能为空");
        }
        List<OrganEO> organList = organDao.getPublicOrgans(organ.getDn());
        if(null==organList||organList.isEmpty()){//如果没有子单位，则以本单位为准
            organList = new ArrayList<OrganEO>();
            organList.add(organ);
        }
        return organList;
    }

    //验证DB 名称是否存在
    @Override
    public Boolean isNameExistedDB(Long parentId, String type, String name,Integer count) {
        Boolean isNameExistedDB = false;
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("parentId", parentId);
        map.put("type", type);
        map.put("name", name);
        map.put("recordStatus", RecordStatus.Normal.toString());
        List<OrganEO> list = this.getEntities(OrganEO.class, map);
        if(list!=null && list.size() > count){
            isNameExistedDB = true;
        }
        return isNameExistedDB;
    }

    @Override
    public List<OrganNodeVO> getSubOrgans(Long parentId, int flag) {
        List<OrganEO> organs = this.getOrgans(parentId);
        List<OrganNodeVO> list = new ArrayList<OrganNodeVO>();
        if (organs != null && organs.size() > 0) {
            for (OrganEO organ : organs) {
                OrganNodeVO node = new OrganNodeVO();
                node.addProperties(organ, flag);
                node.setCount(OrganEO.Type.Organ.toString().equals(organ.getType())?getOrganChildCount(organ.getOrganId()):null);
                list.add(node);
            }
        }
        return list;
    }
}
