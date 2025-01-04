package com.qin.catcat.unite.common.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.qin.catcat.unite.popo.vo.CoordinateVO;

/**
 * 坐标VO工厂类
 * 用于创建和回收CoordinateVO对象
 * @author qin
 * @date 2025-01-03 22:27
 * @version 1.0
 * @since 1.0
 */
public class CoordinateVOFactory extends BasePooledObjectFactory<CoordinateVO> {
    @Override
    public CoordinateVO create() {
        return new CoordinateVO();
    }

    @Override
    public PooledObject<CoordinateVO> wrap(CoordinateVO coordinateVO) {
        return new DefaultPooledObject<>(coordinateVO);
    }
} 