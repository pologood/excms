package cn.lonsun.site.label.internal.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import cn.lonsun.core.base.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.exception.util.Jacksons;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.label.internal.dao.ILabelDao;
import cn.lonsun.site.label.internal.entity.LabelEO;
import cn.lonsun.site.label.internal.service.ILabelService;
import cn.lonsun.site.label.internal.vo.LabelFieldVO;
import cn.lonsun.site.label.internal.vo.LabelVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author DooCal
 * @ClassName: LabelServiceImpl
 * @Description:
 * @date 2015/9/6 17:18
 */
@Transactional
@Service("labelService")
public class LabelServiceImpl extends MockService<LabelEO> implements ILabelService {

    @Autowired
    private ILabelDao labelDao;

    @Override
    public LabelEO getById(Long id) {
        return labelDao.getById(id);
    }

    @Override
    public List<LabelVO> getByName(String name) {
        List<LabelEO> list = labelDao.getByName(name);

        LabelVO labelVO = null;
        List<LabelVO> vlist = new ArrayList<LabelVO>();
        for (LabelEO lv : list) {
            labelVO = new LabelVO();
            labelVO.setId(lv.getId());
            labelVO.setName(lv.getLabelName());
            labelVO.setpId(lv.getParentId());
            labelVO.setDescription(lv.getLabelNotes());
            vlist.add(labelVO);
        }
        return vlist;
    }

    @Override
    public List<LabelVO> getTree(Long pid) {
        List<LabelEO> list = labelDao.getTree(pid);
        LabelVO labelVO = null;
        List<LabelVO> vlist = new ArrayList<LabelVO>();
        for (LabelEO lv : list) {
            labelVO = new LabelVO();
            labelVO.setId(lv.getId());
            labelVO.setName(lv.getLabelName());
            labelVO.setpId(lv.getParentId());
            labelVO.setIsParent(lv.getIsParent());
            labelVO.setDescription(lv.getLabelNotes());
            labelVO.setIsRoot(LoginPersonUtil.isRoot());
            if (AppUtil.isEmpty(lv.getLabelConfig()) || lv.getLabelConfig() == "[]") {

            } else {
                labelVO.setConfig(convertConfig(lv.getLabelName(), lv.getLabelConfig()));
            }
            vlist.add(labelVO);
        }
        return vlist;
    }

    @Override
    public Object saveLabel(LabelEO eo) {

        List<LabelEO> labelEO = labelDao.getByName(eo.getLabelName());
        if (labelEO.size() > 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "相同的标签名已经存在");
        }
        eo.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        //重置父目录 isparent 为真
        LabelEO eo2 = this.getById(eo.getParentId());
        if (!AppUtil.isEmpty(eo)) {
            labelDao.updateLabel(1L, eo.getParentId());
        }
        if (eo.getParentId() == 0) {
            eo.setIsParent(1L);
        }
        if(StringUtils.isEmpty(eo.getLabelConfig())){
            eo.setLabelConfig("[]");
        }
        return labelDao.saveLabel(eo);
    }

    @Override
    public Object updateLabel(LabelEO eo) {

        LabelEO labelEO = labelDao.getOneByName(eo.getLabelName(), eo.getId());
        if (!AppUtil.isEmpty(labelEO)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "相同的标签名已经存在");
        }
        if(StringUtils.isEmpty(eo.getLabelConfig())){
            eo.setLabelConfig("[]");
        }
        return labelDao.updateLabel(eo);
    }

    @Override
    public Object delLabel(Long id) {
        LabelEO eo = this.getById(id);
        labelDao.delLabel(id);
        //如果当前目录下不存在兄弟元素，更新isparent等于0
        Long childCount = labelDao.childCount(eo.getParentId());
        return labelDao.updateLabel(childCount == 0 ? 0L : 1L, eo.getParentId());
    }

    @Override
    public Object updateLabelConfig(Long id, String config) {
        return labelDao.updateLabelConfig(id, config);
    }

    @Override
    public Object copyLabel(Long id, String labelName, String labelNotes) {
        LabelEO eo = this.getById(id);

        List<LabelEO> labelEO = labelDao.getByName(labelName);
        if (labelEO.size() > 0) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "相同的标签名已经存在");
        }

        LabelEO newEO = new LabelEO();
        newEO.setLabelName(labelName);
        newEO.setParentId(eo.getParentId());
        newEO.setLabelNotes(labelNotes);
        newEO.setLabelType(eo.getLabelType());
        newEO.setLabelConfig(eo.getLabelConfig());
        return labelDao.saveLabel(newEO);
    }

    private String convertConfig(String name, String str) {
        LabelFieldVO[] json = Jacksons.json().fromJsonToObject(str, LabelFieldVO[].class);
        String label = "{ls:" + name + " ";
        String val = null, fieldName = null, fieldVal = null;
        Long i = 0L;
        for (LabelFieldVO j : json) {
            fieldName = j.getFieldname();
            fieldVal = j.getDefaultval();
            if (!fieldName.equals("label_desc")) {
                label += fieldName + "=\"" + fieldVal + "\" ";
                i++;
            }
        }

        if (i < 1) {
            label += "/}";
        } else {
            label += "}{/ls:" + name + "}";
        }

        return label;
    }

    @Override
    public Long childCount(Long pid) {
        return labelDao.childCount(pid);
    }
}
