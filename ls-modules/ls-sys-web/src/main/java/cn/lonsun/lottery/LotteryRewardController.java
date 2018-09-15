package cn.lonsun.lottery;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;
import cn.lonsun.lottery.internal.service.ILotteryRewardRecordService;
import cn.lonsun.lottery.internal.service.ILotteryRewardService;
import cn.lonsun.util.LoginPersonUtil;
import com.fasterxml.jackson.databind.deser.Deserializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by lonsun on 2017-1-11.
 */
@Controller
@RequestMapping(value = "/lotteryReward")
public class LotteryRewardController extends BaseController {

    @Autowired
    private ILotteryRewardService lotteryRewardService;
    @Autowired
    private ILotteryRewardRecordService lotteryRewardRecordService;
    @RequestMapping(value = "/rewardIndex")
    public String rewardIndex() {
        return  "/lottery/reward_index";
    }


    @RequestMapping(value = "/getLotteryRewardList")
    @ResponseBody
    public Object getLotteryRewardList(PageQueryVO vo){

        Pagination pagination = lotteryRewardService.getLotteryRewardList(vo);
       return getObject(pagination);
    }




    @RequestMapping(value = "/editReward")
    public String editReward(Model model,Long  rewardId) {
        model.addAttribute("rewardId",rewardId);
        return  "/lottery/edit_reward";
    }




    @RequestMapping(value = "/getLotteryReward")
    @ResponseBody
    public Object getLotteryReward(Long rewardId){
        LotteryRewardEO lotteryRewardEO =new LotteryRewardEO();
        if(!AppUtil.isEmpty(rewardId)){
            lotteryRewardEO =  lotteryRewardService.getEntity(LotteryRewardEO.class,rewardId);
        }
        return getObject(lotteryRewardEO);
    }


    @RequestMapping(value = "/saveLotteryReward")
    @ResponseBody
    public Object saveLotteryReward(LotteryRewardEO lotteryRewardEO){
        if(AppUtil.isEmpty(lotteryRewardEO.getSiteId())){
            lotteryRewardEO.setSiteId(LoginPersonUtil.getSiteId());
            lotteryRewardService.saveEntity(lotteryRewardEO);
        }
        else {

            Object count = lotteryRewardRecordService.getCountRecordByReward(lotteryRewardEO);
//             if(lotteryRewardEO.getCount()<(Long)count){
//
//                 throw  new BaseRunTimeException(TipsMode.Message.toString(),"奖品数量不得小于已抽出奖品数量");
//
//             }

            lotteryRewardService.updateEntity(lotteryRewardEO);

        }
        return getObject(lotteryRewardEO);
    }


    @RequestMapping(value = "/delLotteryReward")
    @ResponseBody
    public Object delLotteryReward(Long [] ids){
        if(null==ids||ids.length<=0){

            throw new BaseRunTimeException(TipsMode.Message.toString(),"请选择类别!");

        }


        lotteryRewardService.delete(LotteryRewardEO.class,ids);

        return getObject();
    }
}
