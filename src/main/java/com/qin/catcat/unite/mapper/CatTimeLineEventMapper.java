package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.CatTimeLineEvent;

/**
 * @Description 猫咪时间线事件Mapper.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-27 21:18
 */
@Mapper
public interface CatTimeLineEventMapper extends BaseMapper<CatTimeLineEvent> {
    
}
