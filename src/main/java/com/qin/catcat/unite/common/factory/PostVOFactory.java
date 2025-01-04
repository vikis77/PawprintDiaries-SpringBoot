package com.qin.catcat.unite.common.factory;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.qin.catcat.unite.popo.vo.PostVO;

/**
 * 帖子VO工厂类
 * 用于创建和回收PostVO对象
 * @author qin
 * @date 2025-01-03 22:28
 * @version 1.0
 * @since 1.0
 */
public class PostVOFactory extends BasePooledObjectFactory<PostVO> {
    @Override
    public PostVO create() {
        return new PostVO();
    }

    @Override
    public PooledObject<PostVO> wrap(PostVO postVO) {
        return new DefaultPooledObject<>(postVO);
    }
} 