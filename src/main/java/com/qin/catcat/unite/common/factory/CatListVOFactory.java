package com.qin.catcat.unite.common.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.qin.catcat.unite.popo.vo.CatListVO;

/**
 * 猫咪列表VO工厂类
 * 用于创建和回收CatListVO对象
 * @author qin
 * @date 2025-01-03 22:33
 * @version 1.0
 * @since 1.0
 */
public class CatListVOFactory extends BasePooledObjectFactory<CatListVO> {
    @Override
    public CatListVO create() {
        return new CatListVO();
    }

    @Override
    public PooledObject<CatListVO> wrap(CatListVO catListVO) {
        return new DefaultPooledObject<>(catListVO);
    }
}
