package com.newgreen.seckillservice.timer;

import com.newgreen.commons.DateUtil;
import com.newgreen.mapper.TbItemSeckillMapper;
import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.pojo.TbItemSeckillExample;
import jdk.nashorn.internal.ir.CallNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 定时任务类：定时从数据库查询各秒杀时间段中符合秒杀活动的商品并存入redis中
 */
@Component
public class TimerTask {
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     *  @Scheduled (cron = "0 * * * * ?")定时任务
     *  定时时间格式:[秒][分][小时][日][月][周][年]
     *  通配符说明:
     *  * 表示所有值. 例如:在分的字段上设置 "*",表示每一分钟都会触发。
     *  ? 表示不指定值。使用的场景为不需要关心当前设置这个字段的值。
     *  例如:要在每月的10号触发一个操作，但不关心是周几，所以需要周位置的那个字段设置为"?" 具体设置为 0 0 0 10 * ?
     *  - 表示区间。例如 在小时上设置 "10-12",表示 10,11,12点都会触发。
     *  , 表示指定多个值，例如在周字段上设置 "MON,WED,FRI" 表示周一，周三和周五触发
     *  / 用于递增触发。如在秒上面设置"5/15" 表示从5秒开始，每增15秒触发(5,20,35,50)。 在月字段上设置'1/3'所示每
     *  月1号开始，每隔三天触发一次。
     *  L 表示最后的意思。在日字段设置上，表示当月的最后一天(依据当前月份，如果是二月还会依据是否是润年[leap]), 在周
     *  字段上表示星期六，相当于"7"或"SAT"。如果在"L"前加上数字，则表示该数据的最后一个。例如在周字段上设置"6L"这样
     *  的格式,则表示“本月最后一个星期五"
     *  W 表示离指定日期的最近那个工作日(周一至周五). 例如在日字段上设置"15W"，表示离每月15号最近的那个工作日触发。
     */
    @Scheduled(cron = "0 * * * * ?")
    public void selSeckillGoods(){
        /*
          封装符合秒杀的商品的条件：
          条件1：商品审核通过的，即status=1
          条件2：商品库存>0的，即stock_count>0
          条件3：商品的秒杀起始时间点大于等于秒杀活动的起始时间点的，即start_time>=startTime
          条件4：商品的秒杀结束时间点小于等于秒杀活动的结束时间点的，即end_time<=startTime+2
          条件5：该商品在redis中不存在的
         */
        //1.获取秒杀活动的各个时间段
        List<Date> seckillTime = DateUtil.getDateMenus();
        //2.查询各时间段内符合秒杀活动条件的商品
        for (Date startTime:seckillTime){
            TbItemSeckillExample exp = new TbItemSeckillExample();
            TbItemSeckillExample.Criteria criteria = exp.createCriteria();
            //2.1条件1：商品审核通过的，即status=1
            criteria.andStatusEqualTo("1");
            //2.2条件2：商品库存>0的，即stock_count>0
            criteria.andStockCountGreaterThan(0);
            //2.3条件3：商品的秒杀起始时间点大于等于秒杀活动的起始时间点的，即start_time>=startTime
            criteria.andStartTimeGreaterThanOrEqualTo(startTime);
            //2.4条件4：商品的秒杀结束时间点小于等于秒杀活动的结束时间点的，即end_time<=startTime+2
            criteria.andEndTimeLessThanOrEqualTo(DateUtil.addDateHour(startTime,2));
            //2.5条件5：该商品在redis中不存在的
                //redis中已经存在的秒杀商品
            String key = "seckill_"+DateUtil.date2Str(startTime);
            Set goodsIds = redisTemplate.boundHashOps(key).keys();
            if (goodsIds!=null&&goodsIds.size()>0) {
                criteria.andIdNotIn(new ArrayList<>(goodsIds));
            }
            //2.6执行查询
            List<TbItemSeckill> itemSeckills = itemSeckillMapper.selectByExample(exp);
            System.out.println("查询到"+DateUtil.date2Str(startTime)+"时间段商品-"+itemSeckills);
            //2.7把查询到的商品存入redis中
            for (TbItemSeckill itemSeckill:itemSeckills) {
                redisTemplate.boundHashOps(key).put(itemSeckill.getId(), itemSeckill);
                /*为了解决商品超卖问题，以商品的id为key，以List<id>为value把每种商品中的每个
                商品存入redis中。创建订单时，从该redis中使用rightPop取商品，当该redis中的value
                为null时，即说明商品已售罄，则无法再继续创建订单，从而避免商品超卖*/
                Long[] ids = getIds(itemSeckill.getStockCount(), itemSeckill.getId());
                Long nums = redisTemplate.boundListOps("goodsList" + itemSeckill.getId()).leftPushAll(ids);
                System.out.println(itemSeckill.getTitle()+"商品总数："+nums);
            }
        }
    }

    /**
     * 解决商品超卖问题
     * @param stock ：商品库存
     * @param id ：商品id
     * @return
     */
    public Long[] getIds(int stock,long id){
        Long[] ids = new Long[stock];
        for (int i = 0; i < stock;i++){
            ids[i] = id;
        }
        return ids;
    }
}
