package com.newgreen.mapper;

import com.newgreen.pojo.TbItemSeckill;
import com.newgreen.pojo.TbItemSeckillExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TbItemSeckillMapper {
    long countByExample(TbItemSeckillExample example);

    int deleteByExample(TbItemSeckillExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbItemSeckill record);

    int insertSelective(TbItemSeckill record);

    List<TbItemSeckill> selectByExample(TbItemSeckillExample example);

    TbItemSeckill selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbItemSeckill record, @Param("example") TbItemSeckillExample example);

    int updateByExample(@Param("record") TbItemSeckill record, @Param("example") TbItemSeckillExample example);

    int updateByPrimaryKeySelective(TbItemSeckill record);

    int updateByPrimaryKey(TbItemSeckill record);
}