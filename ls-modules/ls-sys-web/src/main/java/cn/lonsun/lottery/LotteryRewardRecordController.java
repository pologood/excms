package cn.lonsun.lottery;

import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.entity.LotteryRewardRecordEO;
import cn.lonsun.lottery.internal.service.ILotteryRewardRecordService;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.ParseException;

/**
 * Created by lizhi on 2017-1-15.
 */
@Controller
@RequestMapping(value = "/lotteryRewardRecord")
public class LotteryRewardRecordController extends BaseController {
    @Autowired
    private ILotteryRewardRecordService lotteryRewardRecordService;

    @RequestMapping(value = "/rewardRecordIndex")
    public String rewardRecordIndex() {
        return  "/lottery/reward_record_index";
    }



    @RequestMapping(value = "/getRewardRecord")
    @ResponseBody
    public Object getRewardRecord(QuestionsQueryVO vo) throws ParseException {

        Pagination pagination = lotteryRewardRecordService.getRewardRecord(vo);
        return getObject(pagination);
    }




    @RequestMapping(value = "/sureStatus")
    @ResponseBody
    public Object  sureStatus(Long id){
        LotteryRewardRecordEO lotteryRewardRecordEO=  lotteryRewardRecordService.getEntity(LotteryRewardRecordEO.class, id);
        lotteryRewardRecordEO.setStatus(1);
        lotteryRewardRecordService.updateEntity(lotteryRewardRecordEO);
        return  getObject(lotteryRewardRecordEO);

    }
}
