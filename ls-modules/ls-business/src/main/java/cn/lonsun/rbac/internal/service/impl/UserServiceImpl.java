package cn.lonsun.rbac.internal.service.impl;

import cn.lonsun.common.sso.util.DESedeUtil;
import cn.lonsun.common.sso.util.EncryptKey;
import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.common.vo.ReceiverVO;
import cn.lonsun.common.vo.SmsVo;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.entity.AMockEntity.RecordStatus;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.internal.exception.UidRepeatedException;
import cn.lonsun.ldap.internal.service.ILdapUserService;
import cn.lonsun.ldap.internal.util.LdapOpenUtil;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.dao.IUserDao;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.RoleAssignmentEO;
import cn.lonsun.rbac.internal.entity.RoleEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.*;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserServiceImpl extends MockService<UserEO> implements
        IUserService {
    Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 登录描述
     *
     * @author xujh
     * @version 1.0
     * 2015年2月13日
     *
     */
    public enum LoginDescription{
        UidNotExisted("用户或密码错误"),
        PassWordError("用户或密码错误"),
        CheckCodeError("验证码错误"),
        UidLocked("账号被锁定"),
        LoginSueess("登录成功");

        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        private LoginDescription(String value){
            this.value = value;
        }

    }

    @Autowired
    private IUserDao userDao;
    @Autowired
    private ILdapUserService ldapUserService;
    @Autowired
    private IPersonUserService personUserService;
    @Autowired
    private IOrganService organService;
    @Autowired
    private IRoleAssignmentService roleAssignmentService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IAccessPolicyService accessPolicyService;
    /**
     * 根据uid或手机号获取用户
     * @param argument
     * @return
     */
    @Override
    public UserEO getUserByUidOrMobile(String argument){
        if (StringUtils.isEmpty(argument)) {
            throw new NullPointerException("argument is null");
        }
        return userDao.getUserByUidOrMobile(argument);
    }

    @Override
    public UserEO save(PersonNodeVO node){
        UserEO user = new UserEO();
        user.setUid(node.getUid());
        user.setIsSupportMobile(node.getIsSupportMobile());
        user.setIsCreateMSF(node.getIsCreateMSF());
        user.setLegalIps(node.getLegalIps());
        user.setForbidIps(node.getForbidIps());
        user.setOnlyLegalIps(node.getOnlyLegalIps());
        user.setLoginType(node.getLoginType());
        user.setMobileCode(node.getMobileCode());
        user.setPersonName(node.getPersonName());
        user.setStatus(node.getStatus());
        user.setTokenNum(node.getTokenNum());
        user.setPersonDn(node.getPersonDn());
        user.setStatus(node.getStatus());
        //密码MD5加密更新
        String password = node.getPassword();
        if(!StringUtils.isEmpty(password)){
            String md5Pwd = DigestUtils.md5Hex(password);
            user.setPassword(md5Pwd);
            try {
                String key = EncryptKey.getInstance().getKey();
                //密码对称加密解密
                String desPassword = DESedeUtil.encrypt(password, key);
                user.setDesPassword(desPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        saveEntity(user);
        return user;
    }

    @Override
    public UserEO update(PersonNodeVO node){
        UserEO user = getEntity(UserEO.class, node.getUserId());
        user.setIsSupportMobile(node.getIsSupportMobile());
        user.setIsCreateMSF(node.getIsCreateMSF());
        user.setLegalIps(node.getLegalIps());
        user.setForbidIps(node.getForbidIps());
        user.setOnlyLegalIps(node.getOnlyLegalIps());
        user.setLoginType(node.getLoginType());
        user.setMobileCode(node.getMobileCode());
        user.setPersonName(node.getName());
        user.setStatus(node.getStatus());
        user.setTokenNum(node.getTokenNum());
        //密码更新
        String password = node.getPassword();
        if(!StringUtils.isEmpty(password)){
            String md5Pwd = DigestUtils.md5Hex(password);
            user.setPassword(md5Pwd);
            try {
                String key = EncryptKey.getInstance().getKey();
                //密码对称加密解密
                String desPassword = DESedeUtil.encrypt(password, key);
                user.setDesPassword(desPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateEntity(user);
        return user;
    }

    /**
     * 系统发送短信，通知密码修改信息
     *
     * @param src
     * @param target
     * @param message
     */
    public void sendMessageBySystem(PersonEO src,PersonEO target,String message){
        SmsVo smsVo = new SmsVo();
//        smsVo.setModuleCode(cn.lonsun.webservice.sms.enums.ModuleCode.systemMgr.getValue());
        smsVo.setMsgContent(message);
        ReceiverVO receiver = new ReceiverVO();
        receiver.setMobile(target.getMobile());
//        receiver.setOrganId(target.getOrganId());
//        receiver.setOrganName(target.getOrganName());
//        receiver.setPersonName(target.getName());
//        receiver.setUnitId(target.getUnitId());
//        receiver.setUnitName(target.getUnitName());
//        receiver.setUserId(target.getUserId());
        smsVo.setUsers(new ReceiverVO[]{receiver});
    }

    @Override
    public boolean isIpAccessable(UserEO user,String ip){
        boolean isAccessable = true;
        //ip限制访问-只允许该符合该条件的ip访问-优先级最高
//		String onlyLegalIps = user.getOnlyLegalIps();
//		if(!StringUtils.isEmpty(onlyLegalIps)){
//			isAccessable = isMatch(onlyLegalIps,ip);
//		}
//		//个人限制ip访问验证
//		if(isAccessable){
//			//全局验证
//			isAccessable = accessPolicyService.isIpAccessable(ip);
//			//如果不在全局设置的范围内，那么再验证额外新增的ip设置
//			if(!isAccessable){
//				//新增可访问ip比对
//				String legalIps = user.getLegalIps();
//				if(!StringUtils.isEmpty(legalIps)){
//					isAccessable = isMatch(legalIps,ip);
//				}
//			}
//		}
        return isAccessable;
    }


    /**
     * 根据uid获取有效用户
     *
     * @param uid
     * @return
     */
    @Override
    public UserEO getUser(String uid) {
        if (StringUtils.isEmpty(uid)) {
            throw new NullPointerException("uid is null");
        }
        return userDao.getUser(uid);
    }

    @Override
    public Long save(UserEO user, Long organId) {
        try {
            String password = user.getPassword();
            String md5Pwd = DigestUtils.md5Hex(password);
            user.setPassword(md5Pwd);
            try {
                String key = EncryptKey.getInstance().getKey();
                //密码对称加密解密
                String desPassword = DESedeUtil.encrypt(password, key);
                user.setDesPassword(desPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
            ldapUserService.save(user);
        } catch (UidRepeatedException e) {
            e.printStackTrace();
            // udi重复，抛出异常提示信息
            throw new BaseRunTimeException(TipsMode.Key.toString(),
                    "UserEO.UidRepeated");
        }
        return saveEntity(user);
    }

    /**
     * 根据主键删除用户</br> 先删除LDAP上的用户信息再删除本地数据库中的用户
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new NullPointerException();
        }
        UserEO user = getEntity(UserEO.class, id);
        // ldap只支持uid删除
        ldapUserService.delete(user.getUid());
        delete(UserEO.class, id);
    }

    /**
     * 更新用户密码
     *
     * @param uid
     * @param oldPassword
     * @param newPassword
     */
    @Override
    public void updatePassword(String uid,String oldPassword,String newPassword){
        // 1.验证用户是否合法
        String md5Pwd = DigestUtils.md5Hex(oldPassword);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("password", md5Pwd);
        params.put("recordStatus", RecordStatus.Normal.toString());
        UserEO user = getEntity(UserEO.class, params);
        if (user == null) {
            throw new BaseRunTimeException(TipsMode.Key.toString(),"UserEO.UidOrPwdError");
        }
        String md5NewPwd = DigestUtils.md5Hex(newPassword);
        user.setPassword(md5NewPwd);
        try {
            String key = EncryptKey.getInstance().getKey();
            //密码对称加密解密
            String desPassword = DESedeUtil.encrypt(newPassword, key);
            user.setDesPassword(desPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateEntity(user);
        ldapUserService.updatePassword(uid, null, md5NewPwd);
        //更新密码，通知其他服务
        Object obj = new Object[]{uid,newPassword};
    }

    /**
     * 通过手机号重置密码，并发送到手机
     *
     * @param mobile
     */
    public void resetPasswordByMobile(String mobile){
        //参数验证
        if(StringUtils.isEmpty(mobile)){
            throw new NullPointerException();
        }
        //手机号格式验证
        if(!RegexUtil.isMobile(mobile)){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "手机号格式错误!");
        }
        //根据手机号获取UserEO对象
        UserEO user = userDao.getUserByMobile(mobile);
        if(user==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "系统未查询到该用户，请联系管理员！");
        }
        //获取6位字符串密码
        String newPwd = cn.lonsun.core.base.util.StringUtils.getRandomString(6);
        String md5NewPwd = DigestUtils.md5Hex(newPwd);
        try {
            String key = EncryptKey.getInstance().getKey();
            //密码对称加密解密
            String desPassword = DESedeUtil.encrypt(newPwd, key);
            user.setDesPassword(desPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
        user.setPassword(md5NewPwd);
        updateEntity(user);
        ldapUserService.updatePassword(user.getUid(), null, md5NewPwd);
        //发送短信
        PersonEO target = personUserService.getPerson(user.getUserId());
        String message = "您的密码已重置成功，新的密码为："+newPwd;
        sendMessageBySystem(null,target,message);
        //更新密码，通知其他服务
        Object obj = new Object[]{user.getUid(),newPwd};
    }

    /**
     * 更新用户状态
     *
     * @param userIds
     * @param status
     * @return
     */
    public List<UserEO> updateStatus(Long[] userIds, String status) {
        // 入参验证
        if (userIds == null || userIds.length <= 0) {
            throw new NullPointerException();
        }
        if (!status.equals(UserEO.STATUS.Enabled.toString())
                && !status.equals(UserEO.STATUS.Unable.toString())) {
            throw new IllegalArgumentException();
        }
        //根据userIds获取User集合
        Map<String,Object> params = new HashMap<String,Object>(1);
        params.put("userId", userIds);
        List<UserEO> users = getEntities(UserEO.class, params);
        if (users == null || users.size() <= 0) {
            throw new BaseRunTimeException();
        }
        List<UserEO> us = new ArrayList<UserEO>(users.size());
        if (users != null && users.size() > 0) {
            for (UserEO user : users) {
                if (!user.getStatus().equals(status)) {
                    user.setStatus(status);
                    if(status.equals("Enabled")){//启用账户时，密码尝试次数置0
                        user.setRetryTimes(0);
                    }
                    us.add(user);
                }
            }
        }
        // 2.更新DB中的用户
        updateEntities(us);
        // 1.更新LDAP上的user
        if(LdapOpenUtil.isOpen){
            for (UserEO user : us) {
                ldapUserService.updateStatus(user);
            }
        }
        //记录日志
        for (UserEO u : us) {
            SysLog.log("【系统管理】"+("Enabled".equals(u.getStatus())?"启用":"禁用")+"用户，姓名：" + u.getPersonName(), "UserEO", CmsLogEO.Operation.Update.toString());
        }
        return us;
    }

    /**
     * 根据uid的部分内容进行模糊查询,最多返回maxCount条数据</br> 如果maxCount为空或小于0，那么默认赋予10</br>
     * 如果maxCount超过100，那么只返回100条数据
     *
     * @param subUid
     * @param maxCount
     * @return
     */
    @Override
    public List<UserEO> getUsersBySubUid(String subUid, Integer maxCount) {
        if (StringUtils.isEmpty(subUid)) {
            throw new NullPointerException();
        }
        if (maxCount == null || maxCount <= 0) {
            maxCount = 10;
        }
        if (maxCount > 100) {
            maxCount = 100;
        }
        return userDao.getUsersBySubUid(subUid, maxCount);
    }

    @Override
    public boolean isUidExisted(String uid) {
//		boolean isUidExisted = ldapUserService.isUidExisted(uid);
//		if (!isUidExisted) {
//			if (getUser(uid) != null) {
//				isUidExisted = true;
//			}
//		}
        Map<String,Object> params = new HashMap<String, Object>();
        params.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());
        params.put("uid",uid);
        boolean isUidExisted = false;
        List<UserEO> users = getEntities(UserEO.class,params);
        if(users != null && users.size()>0){
            isUidExisted = true;
        }
        return isUidExisted;
    }

    /**
     * 用户名是否可用</br> 1.用户名已存在;</br> 2.用户名被系统锁定,即有用户正在使用该用户名;
     *
     * @param uid
     * @return
     */
    public boolean isUidUsable(String uid) {
        return ldapUserService.isUidExisted(uid);
    }

    /**
     * 获取用户名分页
     *
     * @param index
     * @param size
     * @param organId
     * @param subUid
     *            模糊匹配
     * @param orderField
     *            排序字段
     * @param isDesc
     *            是否倒序
     * @return
     */
    public Pagination getPageByUid(Long index, Integer size, Long organId,
                                   String subUid, String orderField, boolean isDesc) {
        return userDao.getPageByUid(index, size, organId, subUid, orderField,
                isDesc);
    }

    /**
     * 获取用户姓名分页
     *
     * @param index
     * @param size
     * @param organId
     * @param name
     *            模糊匹配
     * @param orderField
     *            排序字段
     * @param isDesc
     *            是否倒序
     * @return
     */
    public Pagination getPageByName(Long index, Integer size, Long organId,
                                    String name, String orderField, boolean isDesc) {
        return userDao.getPageByName(index, size, organId, name, orderField,
                isDesc);
    }

    @Override
    public List<UserEO> getUsersByRoleId(Long roleId) {
        return userDao.getUsersByRoleId(roleId);
    }

    @Override
    public void saveUA(Long userId, List<RoleEO> roles){
        for (RoleEO role : roles) {
            RoleAssignmentEO roleAssignment = new RoleAssignmentEO();
            roleAssignment.setUserId(userId);
            roleAssignment.setRoleId(role.getRoleId());
            roleAssignmentService.saveEntity(roleAssignment);
        }
    }

    @Override
    public Pagination getPage(Long index, Integer size, Long organId,
                              String subUid, String name, String organName, String orderField,
                              boolean isDesc) {
        return userDao.getPage(index, size, organId, subUid, name, organName,orderField, isDesc);
    }

    public boolean verifyPwd(String uid, String password) {
        if(StringUtils.isEmpty(uid)||StringUtils.isEmpty(password)){
            throw new NullPointerException();
        }
        //密码加密
        String md5Pwd = DigestUtils.md5Hex(password);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("recordStatus", RecordStatus.Normal.toString());
        UserEO user = getEntity(UserEO.class, params);
        // 用户名错误
        if (user==null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(),"请输入正确的用户名");
        }
        // 密码MD5加密
        boolean isPwdOk = false;
        if (md5Pwd.equals(user.getPassword())) {
            isPwdOk = true;
        }
        return isPwdOk;
    }

    @Override
    public PersonEO login(String uid, String md5Pwd, String ip,String code) throws Exception{
        PersonEO dbPerson = null;
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("uid", uid);
            params.put("password", md5Pwd);
            params.put("recordStatus", RecordStatus.Normal.toString());
            UserEO user = userDao.getEntity(UserEO.class, params);
            if (user==null) {
                throw new BaseRunTimeException(TipsMode.Message.toString(),"用户名或密码错误");
            }
            //更新用户登陆的信息
            dbPerson = personUserService.getPerson(user.getUserId());
            user.setLastLoginDate(new Date());
            user.setLastLoginIp(ip);
            user.setLoginTimes(user.getLoginTimes() + 1);
            userDao.update(user);
            return dbPerson;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public UserEO rootLogin(String uid,String md5Pwd){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("password", md5Pwd);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        UserEO user = getEntity(UserEO.class,params);
        return user;
    }

    /**
     * 登录
     * @param uid
     * @param md5Pwd
     * @return
     */
    public UserEO login4Developer(String uid,String md5Pwd){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("uid", uid);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        UserEO user =getEntity(UserEO.class, params);
        if(user==null){
            throw new BaseRunTimeException(TipsMode.Message.toString(),"用户不存在");
        }else{
            if(!md5Pwd.equals(user.getPassword())){
                throw new BaseRunTimeException(TipsMode.Message.toString(),"密码错误");
            }
        }
        return user;
    }

    @Override
    public List<Long> getRoleIds(Long organId, Long userId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("userId", userId);
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<RoleAssignmentEO> as = roleAssignmentService.getEntities(RoleAssignmentEO.class, params);
        List<Long> roleIds = null;
        if (as != null && as.size() > 0) {
            roleIds = new ArrayList<Long>(as.size());
            for (RoleAssignmentEO a : as) {
                Long roleId = a.getRoleId();
                if (!roleIds.contains(roleId)) {
                    roleIds.add(roleId);
                }
            }
        }
        return roleIds;
    }

    @Override
    public void saveOrUpdateDeveloper(String uid, String oldPwd, String newPwd) {
        String md5Pwd = DigestUtils.md5Hex(newPwd);
        String desPassword = null;
        try {
            String key = EncryptKey.getInstance().getKey();
            //密码对称加密解密
            desPassword = DESedeUtil.encrypt(newPwd, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserEO user = getUser(uid);
        //用户为空，那么可以初始化
        if(user==null){
            user = new UserEO();
            user.setUid(uid);
            user.setPassword(md5Pwd);
            user.setDesPassword(desPassword);
            Long userId = saveEntity(user);
            List<RoleEO> roles = roleService.getRoles4DeveloperAndSuperAdmin(true);
            saveUA(userId, roles);
        }else{
            //验证密码是否正确
            if(StringUtils.isEmpty(oldPwd)){
                throw new IllegalArgumentException();
            }
            String oldMd5Pwd = DigestUtils.md5Hex(oldPwd);
            if(oldMd5Pwd.equals(user.getPassword())){
                user.setPassword(md5Pwd);
                user.setDesPassword(desPassword);
                updateEntity(user);
            }
        }
    }

    @Override
    public void saveOrUpdateSuperAdministrator(String uid, String oldPwd, String newPwd) {
        String md5Pwd = DigestUtils.md5Hex(newPwd);
        String desPassword = null;
        try {
            String key = EncryptKey.getInstance().getKey();
            //密码对称加密解密
            desPassword = DESedeUtil.encrypt(newPwd, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserEO user = getUser(uid);
        //用户为空，那么可以初始化
        if(user==null){
            user = new UserEO();
            user.setUid(uid);
            user.setPassword(md5Pwd);
            user.setDesPassword(desPassword);
            Long userId = saveEntity(user);
            List<RoleEO> roles = roleService.getRoles4DeveloperAndSuperAdmin(false);
            saveUA(userId, roles);
        }else{
            //验证密码是否正确
            if(StringUtils.isEmpty(oldPwd)){
                throw new IllegalArgumentException();
            }
            String oldMd5Pwd = DigestUtils.md5Hex(oldPwd);
            if(oldMd5Pwd.equals(user.getPassword())){
                user.setPassword(md5Pwd);
                user.setDesPassword(desPassword);
                updateEntity(user);
            }
        }
    }

    @Override
    public UserEO updatePersonalInfo(PersonNodeVO node){
        // 备份明文密码
        UserEO user = getEntity(UserEO.class, node.getUserId());
        user.setIsSupportMobile(node.getIsSupportMobile());
        user.setIsCreateMSF(node.getIsCreateMSF());
        user.setLegalIps(node.getLegalIps());
        user.setForbidIps(node.getForbidIps());
        user.setOnlyLegalIps(node.getOnlyLegalIps());
        user.setLoginType(node.getLoginType());
        user.setMobileCode(node.getMobileCode());
        user.setPersonName(node.getName());
        user.setStatus(node.getStatus());
        user.setTokenNum(node.getTokenNum());
        //密码更新
        String password = node.getPassword();
        if(!StringUtils.isEmpty(password)){
            String md5Pwd = DigestUtils.md5Hex(password);
            user.setPassword(md5Pwd);
            try {
                String key = EncryptKey.getInstance().getKey();
                //密码对称加密解密
                String desPassword = DESedeUtil.encrypt(password, key);
                user.setDesPassword(desPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        updateEntity(user);
        return user;
    }

}
