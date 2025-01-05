package com.qin.catcat.unite.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.FundCalculateVO;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 小猫管理类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-21 23:05
 */
@Component
@Slf4j
public class CatManage {
    @Autowired
    CatMapper catMapper;

    /**
     * @Description 查询全部猫猫信息.
     * @return 猫猫列表
     */
    public List<Cat> getCatList(){
        QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0);
        return catMapper.selectList(queryWrapper);
    }
    
    /**
     * @Description 查询全部猫猫信息,并连表查询每只猫的评论数量.
     * @return 猫咪列表VO,包含评论数量
     */
    public List<CatListVO> getCatListForCatClaw(){
        List<CatListVO> result = catMapper.selectCatList();
        log.info("MySQL查询全部猫猫信息及评论数完成，共查询到 {} 条数据", result.size());
        return result;
    }
}
