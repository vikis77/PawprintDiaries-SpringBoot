package com.qin.catcat.unite.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.ShotLink;

/**
 * @Description 短链接分享Mapper
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2025-01-18 22:47
 */
@Mapper
public interface ShotLinkShareMapper extends BaseMapper<ShotLink> {

}
