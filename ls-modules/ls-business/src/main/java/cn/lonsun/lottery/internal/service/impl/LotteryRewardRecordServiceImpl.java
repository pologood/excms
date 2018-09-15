package cn.lonsun.lottery.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.lottery.internal.dao.ILotteryRewardRecordDao;
import cn.lonsun.lottery.internal.entity.LotteryConfigEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardEO;
import cn.lonsun.lottery.internal.entity.LotteryRewardRecordEO;
import cn.lonsun.lottery.internal.entity.LotteryTypeEO;
import cn.lonsun.lottery.internal.service.ILotteryConfigService;
import cn.lonsun.lottery.internal.service.ILotteryRewardRecordService;
import cn.lonsun.lottery.internal.service.ILotteryRewardService;
import cn.lonsun.lottery.internal.service.ILotteryTypeService;
import cn.lonsun.lottery.internal.utils.LotteryUtil;
import cn.lonsun.lottery.internal.vo.QuestionsQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;

/**
 * Created by lonsun on 2017-1-11.
 */
@Service
public class LotteryRewardRecordServiceImpl extends MockService<LotteryRewardRecordEO> implements ILotteryRewardRecordService {
    @Autowired
    private ILotteryRewardRecordDao lotteryRewardRecordDao;
    @Autowired
    private ILotteryTypeService lotteryTypeService;
    @Autowired
    private ILotteryRewardService lotteryRewardService;
    @Autowired
    private ILotteryConfigService lotteryConfigService;

    @Override
    public Object getCountRecordByReward(LotteryRewardEO lotteryRewardEO) {
        return lotteryRewardRecordDao.getCountRecordByReward(lotteryRewardEO);
    }

    @Override
    public Object checkTodayUser(String name, String phone) {
        return lotteryRewardRecordDao.checkTodayUser( name, phone);
    }

    @Override
    public Map<String,Object> draw(String name, String phone) {
        List<LotteryTypeEO> lotteryTypeEOs =lotteryTypeService.getAllType();
        List<Double> changceList =new ArrayList<Double>();
        List<Long> idList =new ArrayList<Long>();
        Double reward=0d;
        LotteryRewardEO rewardEO =new LotteryRewardEO();
        for(LotteryTypeEO lotteryTypeEO:  lotteryTypeEOs){
            changceList.add(lotteryTypeEO.getChance());
            idList.add(lotteryTypeEO.getTypeId());
        }
        changceList.add(100d-changceList.get(changceList.size()-1));
        idList.add(null);
        LotteryUtil ll = new LotteryUtil(changceList,idList);
        int index=ll.randomColunmIndex();
        Long typeId = ll.getLotteryList().get(index).getTypeId();
        LotteryRewardRecordEO lotteryRewardRecordEO =new LotteryRewardRecordEO();
        lotteryRewardRecordEO.setName(name);
        lotteryRewardRecordEO.setPhone(phone);
        Map<String,Object> map =new HashMap<String, Object>();
        //获取奖品
        if(!AppUtil.isEmpty(typeId)){

            List<LotteryRewardEO > rewardEOs =  lotteryRewardService.getRewardByType(typeId);
            Random random =new Random();
            index= random.nextInt(rewardEOs.size());
            rewardEO =rewardEOs.get(index);
            reward =rewardEO.getRewardPrice();

            //验证金额
            map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
            List<LotteryConfigEO> configEOs  = lotteryConfigService.getEntities(LotteryConfigEO.class, map);
            map.clear();
            if(null!=configEOs&&configEOs.size()>0){
                LotteryConfigEO configEO =  configEOs.get(0);
                if(rewardEO.getRewardPrice()>configEO.getLeftDum()){
                    map.put("flag",false);
                    map.put("error", "红包已全部发送完毕。");
                    lotteryRewardRecordEO.setRewardName("红包已全部发送完毕。");

                    saveEntity(lotteryRewardRecordEO);
                    return map;
                }
               Object object =  lotteryRewardRecordDao.getSumToday();
                if(null!=object&&configEO.getDaySum()<rewardEO.getRewardPrice()+(Double) object) {
                    map.put("flag",false);
                    map.put("error","今日红包已全部发送完毕。");
                    lotteryRewardRecordEO.setRewardName("今日红包已全部发送完毕。");

                    saveEntity(lotteryRewardRecordEO);

                    return map;

                }

                lotteryRewardRecordEO.setRewardId(rewardEO.getRewardId());
                lotteryRewardRecordEO.setRewardName(rewardEO.getRewardName());
                lotteryRewardRecordEO.setRewardPrice(rewardEO.getRewardPrice());
                map.put("flag",true);
                map.put("success","话费红包将于第二天直接充值到登记的手机号码上，请确保手机号码正确。");
                configEO.setLeftDum(configEO.getLeftDum() - reward);
                lotteryConfigService.updateEntity(configEO);
            }


        } else {
            map.put("flag",false);
            map.put("error","很遗憾未抽取任何红包");
            lotteryRewardRecordEO.setRewardName("很遗憾未抽取任何红包");


        }

         saveEntity(lotteryRewardRecordEO);




        return map;
    }

    @Override
    public Pagination getRewardRecord(QuestionsQueryVO vo) throws ParseException {
        return lotteryRewardRecordDao.getRewardRecord(vo);
    }
}
