package cn.lonsun.lottery;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.lottery.internal.service.ILotteryRecordService;
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
@RequestMapping(value = "/lotteryRecord")
public class LotteryRecordController extends BaseController {
    @Autowired
    private ILotteryRecordService lotteryRecordService;


    @RequestMapping(value = "/recordIndex")
    public String recordIndex() {
        return  "/lottery/record_index";
    }


    @RequestMapping(value = "/getRecords")
    @ResponseBody
    public Object getRecords(QuestionsQueryVO vo) throws ParseException {

        
        Pagination pagination = lotteryRecordService.getRecords(vo);
        return getObject(pagination);
    }

}
