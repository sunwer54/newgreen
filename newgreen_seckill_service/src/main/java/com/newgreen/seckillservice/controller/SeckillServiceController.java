package com.newgreen.seckillservice.controller;

import com.newgreen.api.SeckillGoodsServiceApi;
import com.newgreen.commons.SeckillStatus;
import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.seckillservice.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SeckillServiceController implements SeckillGoodsServiceApi {
    @Autowired
    private SeckillService seckillService;
    /**
     * 获取各秒杀活动时间段的所有商品
     */
    @Override
    public List<TbItemSeckill> getSeckillGoodsListOfEveryTime(String time) {
        return seckillService.getGoodsListOfEveryTime(time);
    }

    /**
     * 获取抢单商品的信息
     * @param time
     * @param id
     * @return
     */
    @Override
    public TbItemSeckill selOne(String time, long id) {
        return seckillService.selOne(time, id);
    }
    /**
     * 秒杀下单
     */
    @Override
    public SeckillStatus addOrder(String time, long id,String username) {
        return seckillService.addOrder(time, id,username);
    }
    /**
     * 查询订单状态信息，1排队等待抢单，2抢单成功，等待支付
     */
    @Override
    public SeckillStatus orderQuery(String username) {
        return seckillService.orderQuery(username);
    }
}
