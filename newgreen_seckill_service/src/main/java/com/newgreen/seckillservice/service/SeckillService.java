package com.newgreen.seckillservice.service;

import com.newgreen.commons.SeckillStatus;
import com.newgreen.pojo.TbItemSeckill;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

public interface SeckillService {
    /**
     * 获取各秒杀活动时间段的所有商品
     */
    public List<TbItemSeckill> getGoodsListOfEveryTime(String time);
    /**
     * 获取获取抢单商品的信息
     */
    public TbItemSeckill selOne(String time,long id);
    /**
     * 秒杀下单
     */
    public SeckillStatus addOrder(String time,long id,String username);
    /**
     * 查询订单状态信息，1排队等待抢单，2抢单成功，等待支付
     */
    @GetMapping("/seckill/order/query")
    public SeckillStatus orderQuery(String username);
}
