package com.newgreen.api;

import com.newgreen.commons.SeckillStatus;
import com.newgreen.pojo.TbItemSeckill;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SeckillGoodsServiceApi {
    /**
     * 获取各秒杀活动时间段的所有商品
     */
    @GetMapping("/seckill/goods/list")
    public List<TbItemSeckill> getSeckillGoodsListOfEveryTime(@RequestParam("time") String time);
    /**
     * 获取获取抢单商品的信息
     */
    @GetMapping("/seckill/goods/selOne")
    public TbItemSeckill selOne(@RequestParam("time") String time,@RequestParam("id") long id);

    /**
     * 秒杀下单
     */
    @GetMapping("/seckill/order/add")
    public SeckillStatus addOrder(@RequestParam("time") String time,@RequestParam("id") long id,@RequestParam("username") String username);
    /**
     * 查询订单状态信息，1排队等待抢单，2抢单成功，等待支付
     */
    @GetMapping("/seckill/order/query")
    public SeckillStatus orderQuery(@RequestParam("username") String username);
}
