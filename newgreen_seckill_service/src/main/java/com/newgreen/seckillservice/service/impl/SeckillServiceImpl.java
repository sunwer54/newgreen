package com.newgreen.seckillservice.service.impl;

import com.newgreen.commons.SeckillStatus;
import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.seckillservice.service.SeckillService;
import com.newgreen.seckillservice.service.multithreadasync.AsyncOrderTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

@Service
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private AsyncOrderTask asyncOrderTask;
    /**
     * 从redis中获取各秒杀活动时间段的所有商品
     */
    @Override
    public List<TbItemSeckill> getGoodsListOfEveryTime(String time) {
        String key = "seckill_"+ time;
        List<TbItemSeckill> itemSeckills = (List<TbItemSeckill>)redisTemplate.boundHashOps(key).values();
        return itemSeckills;
    }

    /**
     * 从redis中获取抢单商品的信息
     * @param time
     * @param id
     * @return
     */
    @Override
    public TbItemSeckill selOne(String time, long id) {
        String key = "seckill_"+ time;
        TbItemSeckill itemSeckill = (TbItemSeckill)redisTemplate.boundHashOps(key).get(id);
        return itemSeckill;
    }
    /**
     * 秒杀下单
     */
    @Override
    public SeckillStatus addOrder(String time, long id,String username) {
        /*为了避免重复抢单，所以首先判断用户是否已抢过单。
        以"userOrderCount"为key，使用hash(username,count)在redis中记录用户的抢单的状态。:
        increment(username, 1)方法，对field是username的value值执行+1，并返回+1后的value的结果count
        count>1,则说明该用户已经抢过一次单，不可重复抢单
        count=1,则说明该用户是第一次抢单，可以继续抢单*/
        //1.判断用户是否已经抢过单
        long userOrderCount = redisTemplate.boundHashOps("userOrderCount").increment(username, 1);
        if (userOrderCount>1){
            //该用户已经抢过单，不能重复抢单
            return null;
        }
        /*把抢单的请求封装成SeckillStatus类对象，暂时将抢单请求存入redis中，等待创建订单
          存储抢单请求使用list类型，因为要保证用户抢单的顺序性，使用list才可以的模拟队列的先进先出。
          以key："userOrderQueue"，value：seckillStatus 把订单请求信息存入redis中*/
        //用户未抢过单，把用户的抢单请求，封装成SeckillStatus对象，并存入redis，排队等待抢单
        SeckillStatus seckillStatus = new SeckillStatus(username,new Date(),1,id,time);
        //把抢单请求存入redis中，排队等待抢单
        redisTemplate.boundListOps("userOrderQueue").leftPush(seckillStatus);
        //2.记录用户的订单状态(存入redis中,key："userOrderStatus"，field：username，value：seckillStatus)
        redisTemplate.boundHashOps("userOrderStatus").put(username,seckillStatus);

        /*使用多线程异步执行抢单操作(取出redis中userOrderQueue中的List中的排队等待的抢单请求),更新redis中的
        用户订单状态"userOrderStatus",key："userOrderStatus",field：username,value：seckillStatus*/
        //3.调用异步任务，实现多线程下单：从redis中取出等待排队的抢单请求，进行订单的创建
        Future<SeckillStatus> order = asyncOrderTask.createOrder(username);
        try {
            seckillStatus = order.get();
            return seckillStatus;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return seckillStatus;
    }
    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付，404，抢单失败
     */
    @Override
    public SeckillStatus orderQuery(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps("userOrderStatus").get(username);
    }
}
