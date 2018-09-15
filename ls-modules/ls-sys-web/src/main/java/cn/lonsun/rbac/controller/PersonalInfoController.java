package cn.lonsun.rbac.controller;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.util.RegexUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.ldap.vo.PersonNodeVO;
import cn.lonsun.rbac.internal.entity.PersonEO;
import cn.lonsun.rbac.internal.entity.UserEO;
import cn.lonsun.rbac.internal.service.IPersonService;
import cn.lonsun.rbac.internal.service.IUserService;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.shiro.util.RSAUtils;
import cn.lonsun.util.LoginPersonUtil;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.interfaces.RSAPublicKey;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-24<br/>
 */
@RequestMapping("/personalInfo")
@Controller
public class PersonalInfoController extends BaseController {

    @Autowired
    private IPersonService personService;
    @Autowired
    private IUserService userService;

    @RequestMapping("editPersonalInfo")
    public String editPersonalInfo(Model model) {
        RSAPublicKey publicKey = RSAUtils.getDefaultPublicKey();
        model.addAttribute("modulus", new String(Hex.encodeHex(publicKey.getModulus().toByteArray())));
        model.addAttribute("exponent", new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray())));
        return "personalinfo/info_edit";
    }

    @RequestMapping("getPersonInfo")
    @ResponseBody
    public Object getPersonInfo() {
        Long personId = LoginPersonUtil.getPersonId();
        PersonEO personEO = new PersonEO();
        if (personId != null) {
            personEO = CacheHandler.getEntity(PersonEO.class, personId);
        } else {
            Long userId = LoginPersonUtil.getUserId();
            String uid = (String) LoginPersonUtil.getSession().getAttribute("uid");
            personEO.setUserId(userId);
            personEO.setUid(uid);
        }
        PersonNodeVO vo = new PersonNodeVO();
        AppUtil.copyProperties(vo, personEO);
        return getObject(vo);
    }

    @RequestMapping("updatePersonInfo")
    @ResponseBody
    public Object updatePersonInfo(PersonNodeVO node) {
        // if(node.getUid().equals("lonsun_root"))
        if(!AppUtil.isEmpty(node.getPassword())) {
            node.setPassword(RSAUtils.decryptStringByJs(node.getPassword()));
        }
        if(!AppUtil.isEmpty(node.getOriginalPassword())) {
            node.setOriginalPassword(RSAUtils.decryptStringByJs(node.getOriginalPassword()));
        }

        if (node.getUid().equals(InternalAccount.DEVELOPER_ADMIN_CODE)) {
            UserEO user = userService.getEntity(UserEO.class, node.getUserId());
            if(!user.getPassword().equals(DigestUtils.md5Hex(node.getOriginalPassword()))){
                throw new BaseRunTimeException(TipsMode.Message.toString(), "原始密码不正确");
            }
            if (!StringUtils.isEmpty(node.getPassword())) {
                user.setPassword(DigestUtils.md5Hex(node.getPassword()));
                userService.updateEntity(user);
            }
        } else {
            checkPersonAndUser(node, false);
            personService.updatePersonInfo(node);
        }

        return getObject();
    }

    public void checkPersonAndUser(PersonNodeVO node, boolean isSave) {

        // 验证原始密码
        String originalPassword = node.getOriginalPassword();
        if (!StringUtils.isEmpty(originalPassword)) {
            if (!personService.isOriginalPasswordExisted(node.getUserId(), originalPassword)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "原始密码不正确");
            }
        } else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "原始密码不能为空");
        }

        // 排序号合法性验证
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

        // 姓名
        String name = node.getName();
        if (StringUtils.isEmpty(name)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入姓名");
        } else {
            // 去除空格
            name = name.trim();
            if (!RegexUtil.isCombinationOfChineseAndCharactersAndNumbers(name)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "名称仅支持中文、英文数字和部分中文标点符号的组合");
            }
            int length = name.length();
            if (length < 2 || length > 20) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "姓名长度2-20个字符");
            }
            node.setName(name);
        }

        // 职务
        String positions = node.getPositions();
        if (!StringUtils.isEmpty(positions)) {
            positions = positions.trim();
            int length = positions.length();
            if (length > 80) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "职务长度0-80个字符");
            }
            node.setPositions(positions);
        }

        // 手机号
        String mobile = node.getMobile();
        if (!StringUtils.isEmpty(mobile)) {
            mobile = mobile.trim();
            if (mobile.length() > 0 && mobile.length() != 11) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入正确的手机号");
            }
            if (personService.isMobileExisted(node.getPersonId(), mobile)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "该手机号已存在，请输入新的手机号");
            }

            node.setMobile(mobile);
        }

        // 电话号码长度：
        String officePhone = node.getOfficePhone();
        if (!StringUtils.isEmpty(officePhone)) {
            officePhone = officePhone.trim();
            if (officePhone.length() > 32) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "办公电话长度0-32个字符");
            }
            node.setOfficePhone(officePhone);
        }

        // 办公地址
        String officeAddress = node.getOfficeAddress();
        if (!StringUtils.isEmpty(officeAddress)) {
            officeAddress = officeAddress.trim();
            if (officeAddress.length() > 80) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "办公地址长度0-80个字符");
            }
            node.setOfficeAddress(officeAddress);
        }

        // 账号长度验证
        if (isSave) {// 更新无需验证
            String uid = node.getUid();
            if (StringUtils.isEmpty(uid)) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "请输入账号");
            } else {
                if (node.getUid().length() < 2 || node.getUid().length() > 50) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "帐号长度2-50个字符");
                }
                String password = node.getPassword();
                if (StringUtils.isEmpty(password) || password.trim().length() < 1 || password.trim().length() > 16) {
                    throw new BaseRunTimeException(TipsMode.Message.toString(), "密码长度1-16个字符");
                }
            }
        }

        // 密码验证
        String password = node.getPassword();
        if (!StringUtils.isEmpty(password)) {
            if (password.trim().length() < 1 || password.trim().length() > 16) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "密码长度1-16个字符");
            }
        }

        // 移动端编码
        String mobileCode = node.getMobileCode();
        if (!StringUtils.isEmpty(mobileCode)) {
            mobileCode = mobileCode.trim();
            if (mobileCode.length() > 80) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "移动端编码0-80个字符");
            }
        }
    }

}
