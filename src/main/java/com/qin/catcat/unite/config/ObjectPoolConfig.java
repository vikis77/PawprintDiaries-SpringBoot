package com.qin.catcat.unite.config;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.qin.catcat.unite.popo.vo.PostVO;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.common.factory.PostVOFactory;
import com.qin.catcat.unite.common.factory.CatListVOFactory;
import com.qin.catcat.unite.common.factory.CoordinateVOFactory;

/**
 * 对象池配置
 * 用于管理频繁创建的对象，减少GC压力
 * @author qin
 * @date 2025-01-03 22:22
 * @version 1.0
 * @since 1.0
 */

@Configuration
public class ObjectPoolConfig {
    /**
     * 猫咪列表VO对象池
     * @return GenericObjectPool<CatListVO>
     */
    @Bean
    public GenericObjectPool<CatListVO> catListVOPool() {
        return new GenericObjectPool<>(new CatListVOFactory(), createBaseConfig());
    }

    /**
     * 帖子VO对象池
     * @return GenericObjectPool<PostVO>
     */
    @Bean
    public GenericObjectPool<PostVO> postVOPool() {
        return new GenericObjectPool<>(new PostVOFactory(), createBaseConfig());
    }
    
    /**
     * 坐标VO对象池
     * @return GenericObjectPool<CoordinateVO>
     */
    @Bean
    public GenericObjectPool<CoordinateVO> coordinateVOPool() {
        GenericObjectPoolConfig<CoordinateVO> config = createBaseConfig();
        config.setMaxTotal(50);
        config.setMaxIdle(10);
        config.setMinIdle(2);
        return new GenericObjectPool<>(new CoordinateVOFactory(), config);
    }

    private <T> GenericObjectPoolConfig<T> createBaseConfig() {
        GenericObjectPoolConfig<T> config = new GenericObjectPoolConfig<>();
        config.setJmxEnabled(false);  // 禁用JMX
        config.setMaxTotal(100);
        config.setMaxIdle(20);
        config.setMinIdle(5);
        config.setTestOnBorrow(true);
        return config;
    }
} 