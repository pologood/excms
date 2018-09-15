package cn.lonsun.lottery;


import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;
import cn.lonsun.lottery.internal.service.ILotteryRewardService;
import cn.lonsun.lottery.internal.service.ILotteryTypeService;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.webservice.processEngine.vo.TaskHandleParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-9.
 */
@Controller
@RequestMapping(value = "/lotteryType")
public class LotteryTypeController extends BaseController {
    @Autowired
    private ILotteryTypeService lotteryTypeService;
    @Autowired
    private ILotteryRewardService lotteryRewardService;


    @RequestMapping(value = "/typeIndex")
    public String typeIndex() {
        return  "/lottery/type_index";
    }


    @RequestMapping(value = "/getLotteryTypeList")
    @ResponseBody
    public Object getLotteryTypeList(PageQueryVO vo){
        Pagination pagination = lotteryTypeService.getLotteryTypeList(vo);
        return getObject(pagination);
    }


    @RequestMapping(value = "/editType")
    public String editType(Model model,Long  typeId) {
        model.addAttribute("typeId",typeId);
        return  "/lottery/edit_type";
    }

    @RequestMapping(value = "/getLotteryType")
    @ResponseBody
    public Object getLotteryType(Long id){
        LotteryTypeEO lotteryTypeEO =new LotteryTypeEO();
        if(!AppUtil.isEmpty(id)){
            lotteryTypeEO =  lotteryTypeService.getEntity(LotteryTypeEO.class,id);
        }
        return getObject(lotteryTypeEO);
    }


    @RequestMapping(value = "/saveLotteryType")
    @ResponseBody
    public Object saveLotteryType(LotteryTypeEO lotteryTypeEO){
        if(AppUtil.isEmpty(lotteryTypeEO.getChance())) {

            throw new BaseRunTimeException(TipsMode.Message.toString(),"中奖概率不能为空!");
        }

        if(!AppUtil.isEmpty(lotteryTypeEO.getTypeId())){
            lotteryTypeService.updateEntity(lotteryTypeEO);
        }
        else {
            lotteryTypeEO.setSiteId(LoginPersonUtil.getSiteId() );
            lotteryTypeService.saveEntity(lotteryTypeEO);
        }
        return getObject(lotteryTypeEO);
    }
    @RequestMapping(value = "/delTypes")
    @ResponseBody
    public Object delTypes(Long [] ids){
        if(null==ids||ids.length<=0){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择类别!");

        }

        for( Long id: ids ){
            LotteryTypeEO lotteryTypeEO = lotteryTypeService.getEntity(LotteryTypeEO.class, id);
              Object object =  lotteryRewardService.getCountRewardByTypeId(lotteryTypeEO.getTypeId());
              Long count=(Long)object;
              if(null!=count&&count>0){

                  throw new BaseRunTimeException(TipsMode.Message.toString(),"奖品类别"+lotteryTypeEO.getTypeName()+"已被奖品绑定!,无法删除");
              }


        }
        lotteryTypeService.delete(LotteryTypeEO.class,ids);

            return getObject();
    }

    @RequestMapping(value = "/getAllType")
    @ResponseBody
    public Object getAllType(){
        Map<String,Object> map =new HashMap<String,Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId",LoginPersonUtil.getSiteId());
        List<LotteryTypeEO> list = lotteryTypeService.getEntities(LotteryTypeEO.class, map);
        return getObject(list);
    }


}
