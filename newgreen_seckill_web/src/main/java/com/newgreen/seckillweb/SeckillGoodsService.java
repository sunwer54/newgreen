package com.newgreen.seckillweb;

import com.newgreen.api.SeckillGoodsServiceApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("seckill-service")
public interface SeckillGoodsService extends SeckillGoodsServiceApi {
}
