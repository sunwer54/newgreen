package com.newgreen.rabbitmqreceive;

import com.newgreen.commons.JsonUtils;
import com.newgreen.commons.SeckillStatus;
import com.newgreen.mapper.TbItemSeckillMapper;
import com.newgreen.pojo.TbItemSeckill;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class Receiver {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private TbItemSeckillMapper itemSeckillMapper;
    /**
     * 死信队列消费者
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "order_dlx_queue"),
            exchange = @Exchange(value = "order_dlx_exchange"),
            key = "order.dlx.key"))
    public void ReceiveDlx(String msg){
        System.out.println("死信队列消息："+msg);
        /*由于该死信队列中都是过期未支付的订单的消息，所以需要对产生该订单的操作执行回滚
        1.redis中恢复商品库存，即对key:"seckill_" + time,field:goodsId,value:itemSeckill恢复库存，
        2.删除redis中该用户的抢单记录，即删除key："UserOrderCount",field:username
        3.删除redis中用户的抢单状态，即删除key:"userOrderStatus",field:username
        4.删除redis中的用户已创建的订单，即删除key:"userCreatedOrder",field:username
        库存恢复的特殊情况:由于当redis中的商品库存为0后删除了redis中该商品数据，并同步更
        新到了mysql，所以在对商品库存回滚时，需要对这个情况进行特殊处理*/
        //处理死信队列中的消息（未支付的过期订单消息）
        SeckillStatus seckillStatus = JsonUtils.jsonToPojo(msg, SeckillStatus.class);
        if (seckillStatus!=null) {
            String username = seckillStatus.getUsername();
            long id = seckillStatus.getGoodsId();
            String time = seckillStatus.getTime();
            //1.删除redis中该用户的订单数记录
            redisTemplate.boundHashOps("userOrderCount").delete(username);
            //2.删除redis中该用户的订单状态
            redisTemplate.boundHashOps("userOrderStatus").delete(username);
            //3.把已创建的订单从redis中删除
            redisTemplate.boundHashOps("userCreatedOrder").delete(username);
            //4.恢复库存，并更新到redis中
            TbItemSeckill itemSeckill = (TbItemSeckill)redisTemplate.boundHashOps("seckill_" + time).get(id);

            if (itemSeckill==null){
                //itemSeckill==null,则说明该商品库存已为0，并已更新到mysql中，需要在mysql中恢复商品库存,重新添加到redis中
                //从mysql中获取商品
                itemSeckill = itemSeckillMapper.selectByPrimaryKey(id);
                //恢复库存
                itemSeckill.setStockCount(itemSeckill.getStockCount()+1);
                //更新mysql中商品
                itemSeckillMapper.updateByPrimaryKey(itemSeckill);
            }else {
                //itemSeckill不为null，说明redis中还存在该商品，则说明商品还有库存，执行库存加1，再存入redis即可
                itemSeckill.setStockCount(itemSeckill.getStockCount()+1);
            }
            //更新到redis中
            redisTemplate.boundHashOps("seckill_" + time).put(id,itemSeckill);
            //恢复防止超卖的redis中的商品
            redisTemplate.boundListOps("goodsList"+id).leftPush(id);
            System.out.println("超时未支付订单处理完成");
        }
    }
}
