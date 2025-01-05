package com.qin.catcat.unite.service;

import java.util.List;
import com.qin.catcat.unite.popo.dto.AddFundRecordDTO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.popo.vo.FundCalculateVO;
import com.qin.catcat.unite.popo.vo.FundRecordVO;

/**
 * @Description 猫咪数据分析服务接口
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-04 17:19
 */
public interface CatAnalysisService {
    /**
     * 数据分析
     */
    DataAnalysisVO analysis();

    /**
     * 添加资金记录
     */
    void addOrUpdateFundRecord(AddFundRecordDTO addFundRecordDTO);

    /**
     * 获取资金记录
     */
    List<FundRecordVO> getFundRecord(Integer type);

    /**
     * 删除资金记录
     */
    void deleteFundRecord(Integer id);

    /**
     * 计算资金统计数据
     */
    List<FundCalculateVO> calculateFund(String type);
} 