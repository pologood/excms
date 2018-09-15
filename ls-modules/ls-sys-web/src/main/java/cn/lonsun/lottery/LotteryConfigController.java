package cn.lonsun.lottery;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.service.ILotteryConfigService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-18.
 */
@Controller
@RequestMapping(value = "/lotteryConfig")
public class LotteryConfigController extends BaseController {
    @Autowired
    private ILotteryConfigService lotteryConfigService;


    @RequestMapping(value = "/configIndex")
    public String configIndex() {
        return  "/lottery/config_index";
    }


    @RequestMapping(value = "/getLotteryConfig")
    @ResponseBody
    public Object getLotteryConfig(QuestionsQueryVO vo){
        Pagination pagination = lotteryConfigService.getLotteryConfig(vo);
        return getObject(pagination);
    }


    @RequestMapping(value = "/editConfig")
    public String editConfig(Model model,Long configId) {
        model.addAttribute("configId",configId);
        return  "/lottery/edit_config";
    }



    @RequestMapping(value = "/getConfig")
    @ResponseBody
    public Object getConfig(Long configId){

        LotteryConfigEO lotteryConfigEO =new LotteryConfigEO();
        if(!AppUtil.isEmpty(configId)){
            lotteryConfigEO=lotteryConfigService.getEntity(LotteryConfigEO.class,configId);

        }

        return getObject(lotteryConfigEO);
    }


    @RequestMapping(value = "/saveConfig")
    @ResponseBody
    public Object saveConfig(LotteryConfigEO lotteryConfigEO){
        if(AppUtil.isEmpty(lotteryConfigEO.getConfigId())){
            Map<String,Object> param =new HashMap<String, Object>();
            param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<LotteryConfigEO> lotteryConfigEOs = lotteryConfigService.getEntities(LotteryConfigEO.class,param);
            if(null!=lotteryConfigEOs&&lotteryConfigEOs.size()>0){
               throw new BaseRunTimeException(TipsMode.Message.toString(),"已存在配置,无需添加 ");

            }
       }

       if(lotteryConfigEO.getConfigSum().doubleValue()<lotteryConfigEO.getLeftDum().doubleValue()||lotteryConfigEO.getConfigSum().doubleValue()<lotteryConfigEO.getDaySum().doubleValue()){
           throw new BaseRunTimeException(TipsMode.Message.toString(),"总金额不得小于剩余金额或每日发送金额 ");


       }
        if(AppUtil.isEmpty(lotteryConfigEO.getConfigId())){
            lotteryConfigService.saveEntity(lotteryConfigEO);

        } else {

            lotteryConfigService.updateEntity(lotteryConfigEO);
        }

        return getObject();

    }



}
