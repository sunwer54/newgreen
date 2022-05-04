package com.newgreen.seckillservice.service.multithreadasync;

import com.newgreen.commons.IDUtils;
import com.newgreen.commons.JsonUtils;
import com.newgreen.commons.SeckillStatus;
import com.newgreen.mapper.TbItemSeckillMapper;
import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.pojo.TbOrderSeckill;
import com.newgreen.rabbbitmqsend.send.Sender;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.concurrent.Future;

/**
 * 异步任务类：多线程异步创建订单
 */

@Component
public class AsyncOrderTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    @Autowired
    private Sender sender;
    /**
     * @Async 这是spring中用来定义异步任务的注解
     * @Async 修饰类: 该类下所有的方法都是异步调用的（在一个新的线程中执行）
     * @Async 修饰方法: 这个方法是异步调用(在一个新的线程中执行)
     */
    @Async
    public Future<SeckillStatus> createOrder(String username){
        /*把redis(key:"userOrderQueue",value:List)中的抢单请求从List右边取出依次执行订单的
          创建,并把订单存入redis(key:"orderSeckill",field:username,value:orderSeckill)
          并更新redis中"userOrderStatus"的抢单的状态信息*/
        //1.从redis中取出用户的等待排队中的抢单请求(从右边取出),拿到SeckillStatus抢单请求对象
        SeckillStatus seckillStatus = (SeckillStatus)redisTemplate.boundListOps("userOrderQueue").rightPop();
        //从排队的请求信息中获取商品id
        long goodsId = seckillStatus.getGoodsId();
        //2.为防止超卖，从"goodsList" + goodsId中取商品id，判断是否还存在
        Long o = (Long)redisTemplate.boundListOps("goodsList" + goodsId).rightPop();
        //值为null，说明商品已卖完
        if (o==null){
            //删除用户订单记录信息
            redisTemplate.boundHashOps("userOrderCount").delete(username);
            //404表示抢单失败
            seckillStatus.setStatus(404);
            //删除该用户的订单状态信息
            redisTemplate.boundHashOps("userOrderStatus").put(username,seckillStatus);
            return new AsyncResult<>(seckillStatus);
        }
        //3.从redis中获取抢单的商品的信息
        String key = "seckill_"+seckillStatus.getTime();
        TbItemSeckill itemSeckill = (TbItemSeckill)redisTemplate.boundHashOps(key).get(goodsId);
        //商品不存在或者库存<=0时
        if (itemSeckill == null ||itemSeckill.getStockCount()<=0){
            //删除用户订单记录信息
            redisTemplate.boundHashOps("userOrderCount").delete(username);
            //404表示抢单失败
            seckillStatus.setStatus(404);
            //删除该用户的订单状态信息
            redisTemplate.boundHashOps("userOrderStatus").put(username,seckillStatus);
            return new AsyncResult<>(seckillStatus);
        }
        //4.创建用户订单
        TbOrderSeckill orderSeckill = new TbOrderSeckill();
        long orderId = IDUtils.genItemId();
        orderSeckill.setCreateTime(new Date());//订单创建时间
        orderSeckill.setId(orderId);//订单id
        orderSeckill.setMoney(itemSeckill.getCostPrice());//抢购价
        orderSeckill.setSeckillId(goodsId);//商品id
        orderSeckill.setStatus("0");//订单状态0：未支付，1：已支付
        orderSeckill.setUserId(username);//用户
        //把创建的用户订单存入redis中
        redisTemplate.boundHashOps("userCreatedOrder").put(username,orderSeckill);
        //5.更新redis中用户的用户订单状态--"userOrderStatus"
        seckillStatus.setMoney(itemSeckill.getCostPrice().floatValue());//抢购价
        seckillStatus.setStatus(2);//抢单成功，未支付
        seckillStatus.setOrderId(orderId);//订单id
        redisTemplate.boundHashOps("userOrderStatus").put(username,seckillStatus);
        //6.扣减商品库存
        int stockCount = itemSeckill.getStockCount() - 1;
        itemSeckill.setStockCount(stockCount);
        //库存为0时
        if (stockCount<=0){
            //删除redis中商品信息
            redisTemplate.boundHashOps(key).delete(itemSeckill.getId());
            //更新到mysql
            itemSeckillMapper.updateByPrimaryKey(itemSeckill);
        }else {
            //库存>0时，更新redis中商品库存
            redisTemplate.boundHashOps(key).put(goodsId,itemSeckill);
        }
        //7.把订单消息发送到延时队列中，等待支付消费
        String s = JsonUtils.objectToJson(seckillStatus);
        //通过expiration设置消息的过期时间（即设置了单条消息过期时间，又设置了队列过期时间，则二者取其短）
        MessagePostProcessor messagePostProcessor = new MessagePostProcessor() {
            @Override
            public Message postProcessMessage(Message message) throws AmqpException {
                message.getMessageProperties().setExpiration("10000");//设置这条消息的过期时间
                message.getMessageProperties().setContentEncoding("utf-8");//设置编码
                return message;
            }
        };
        sender.sendOrderMsg(s,messagePostProcessor);
        return new AsyncResult<>(seckillStatus);
    }
}
