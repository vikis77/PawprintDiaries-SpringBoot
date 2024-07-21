package com.qin.catcat.unite.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.DonateMapper;
import com.qin.catcat.unite.popo.dto.DonateDTO;
import com.qin.catcat.unite.popo.entity.Donate;
import com.qin.catcat.unite.service.DonateService;

/* 
 * 捐赠实现类
 * @author qin
 */
@Service
public class DonateServiceImpl implements DonateService{
    @Autowired DonateMapper donateMapper;
    @Autowired GeneratorIdUtil generatorIdUtil;
    /**
    * 新增捐赠 
    * @param 
    * @return 
    */
    public Boolean addDonate(DonateDTO donateDTO){
        // 将DTO对象转换为实体
        Donate donate = new Donate();
        BeanUtils.copyProperties(donateDTO, donate);

        // 设置捐赠时间
        donate.setTime(Timestamp.from(Instant.now()));

        // 设置捐赠ID
        String id = generatorIdUtil.GeneratorRandomId();
        donate.setId(Long.parseLong(id));
        
        // 插入数据库
        donateMapper.insert(donate);
        return true;
    }

    /**
    * 删除捐赠
    * @param 
    * @return 
    */
    public Boolean deleteDonate(Long id){
        donateMapper.deleteById(id);
        return true;
    }

    /**
    * 更新捐赠 不更新捐赠时间字段
    * @param 
    * @return 
    */
    public Boolean updateDonate(DonateDTO donateDTO){
        //从数据库中获取捐赠时间
        QueryWrapper<Donate> donateWrapper = new QueryWrapper<>();
        donateWrapper.select("time");
        donateWrapper.eq("id",donateDTO.getId());
        Donate donateDB = donateMapper.selectOne(donateWrapper);
        Timestamp time = donateDB.getTime();

        // 将DTO对象转换为实体
        Donate donate = new Donate();
        BeanUtils.copyProperties(donateDTO, donate);
        // 设置捐赠时间
        donate.setTime(time);

        // 更新数据库
        donateMapper.updateById(donate);
        return true;
    }

    /**
    * 获取捐赠信息 （分页）
    * @param 
    * @return 
    */
    public IPage<Donate> getDonate(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit",defaultValue = "10") Integer limit){

        IPage<Donate> donateIPage = donateMapper.selectPage(new Page<>(page, limit), null);
        return donateIPage;
    }
}
