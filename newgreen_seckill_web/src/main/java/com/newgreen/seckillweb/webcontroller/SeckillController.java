package com.newgreen.seckillweb.webcontroller;

import com.newgreen.commons.DateUtil;
import com.newgreen.commons.Result;
import com.newgreen.commons.SeckillStatus;
import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.pojo.TbUser;
import com.newgreen.seckillweb.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RestController
public class SeckillController {
    /**
     * 查询时间菜单，获取秒杀的时间分段：
     * 共五个时间分段，每2个小时为一个时间分段
     * @return
     */
    @RequestMapping("/seckill/goods/menus")
    public List<Date> getTimeMenus(){
        List<Date> timeMenus = DateUtil.getDateMenus();
        return timeMenus;
    }

    /**
     * 获取用户登录身份信息
     * @return
     */
    @RequestMapping("/getuser")
    public String getUser(HttpSession session){
        TbUser user = (TbUser)session.getAttribute("user");
        if (user!=null){
            return user.getUsername();
        }
        return null;
    }

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    /**
     * 获取各秒杀活动时间段的所有商品
     */
    @GetMapping("/seckill/goods/list")
    public List<TbItemSeckill> getSeckillGoodsListOfEveryTime(@RequestParam("time") String time){
        return seckillGoodsService.getSeckillGoodsListOfEveryTime(time);
    }
    /**
     * 获取获取抢单商品的信息
     */
    @GetMapping("/seckill/goods/selOne")
    public TbItemSeckill selOne(@RequestParam("time") String time,@RequestParam("id") long id){
        TbItemSeckill itemSeckill = seckillGoodsService.selOne(time, id);
        return itemSeckill;
    }
    /**
     * 秒杀下单
     */
    @RequestMapping("/seckill/order/add")
    public Result addOrder(@RequestParam("time") String time,long id,HttpSession session){
        TbUser user = (TbUser)session.getAttribute("user");
        if (user!=null){
            SeckillStatus seckillStatus = seckillGoodsService.addOrder(time, id, user.getUsername());
            if (seckillStatus==null){
                return new Result(400,"100");
            }
            if (seckillStatus.getStatus()==404){
                return new Result(404,"抢单失败");
            }
            return new Result(0,"您的订单正在排队中，请稍候");
        }
        return new Result(403,"请登录后再抢单");

    }
    /**
     * 查询订单状态，1排队等待抢单，2抢单成功，等待支付
     * @return
     */
    @GetMapping("/seckill/order/query")
    public Result orderQuery(HttpSession session){
        TbUser user = (TbUser)session.getAttribute("user");
        if (user!=null){
            SeckillStatus seckillStatus = seckillGoodsService.orderQuery(user.getUsername());
            return new Result(seckillStatus.getStatus(),"");
        }
        return new Result(403,"未登录");
    }
}
