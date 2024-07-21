package com.qin.catcat.unite.service;

import org.springframework.web.bind.annotation.RequestParam;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.dto.DonateDTO;
import com.qin.catcat.unite.popo.entity.Donate;

public interface DonateService {
    /**
    * 新增捐赠
    * @param 
    * @return 
    */
    public Boolean addDonate(DonateDTO donateDTO);

    /**
    * 删除捐赠
    * @param 
    * @return 
    */
    public Boolean deleteDonate(Long id);

    /**
    * 更新捐赠
    * @param 
    * @return 
    */
    public Boolean updateDonate(DonateDTO donateDTO);

    /**
    * 获取捐赠信息 （分页）
    * @param 
    * @return 
    */
    public IPage<Donate> getDonate(@RequestParam(name = "page") Integer page, @RequestParam(name = "limit",defaultValue = "10") Integer limit);
}
