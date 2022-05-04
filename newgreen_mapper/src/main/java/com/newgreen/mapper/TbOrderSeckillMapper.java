package com.newgreen.mapper;

import com.newgreen.pojo.TbOrderSeckill;
import com.newgreen.pojo.TbOrderSeckillExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbOrderSeckillMapper {
    long countByExample(TbOrderSeckillExample example);

    int deleteByExample(TbOrderSeckillExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbOrderSeckill record);

    int insertSelective(TbOrderSeckill record);

    List<TbOrderSeckill> selectByExample(TbOrderSeckillExample example);

    TbOrderSeckill selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbOrderSeckill record, @Param("example") TbOrderSeckillExample example);

    int updateByExample(@Param("record") TbOrderSeckill record, @Param("example") TbOrderSeckillExample example);

    int updateByPrimaryKeySelective(TbOrderSeckill record);

    int updateByPrimaryKey(TbOrderSeckill record);
}