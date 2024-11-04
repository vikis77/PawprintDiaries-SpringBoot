package com.qin.catcat.unite.service;

import java.util.List;

import com.qin.catcat.unite.popo.vo.SearchVO;

public interface SearchService {
    /**
    * 搜索（帖子或猫猫）
    * @param 
    * @return 
    */
    public SearchVO search(String words, int page, int size);
}
