package com.qin.catcat.unite.service;

import java.util.List;

import com.qin.catcat.unite.popo.vo.SearchVO;

public interface SearchService {
    /**
    * ES 和 MySQL 联合搜索（帖子或猫猫）
    * @param words 搜索关键词
    * @param page 页码
    * @param size 每页大小
    * @return 搜索结果
    */
    public SearchVO searchForEsAndMysql(String words, int page, int size);

}
