package com.qin.catcat.unite.common.utils;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.popo.vo.PostVO;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description 对象池工具类
 * @author qin
 * @date 2025-01-03 22:36
 * @version 1.0
 * @since 1.0
 */
@Component
@Slf4j
public class ObjectPoolUtil {
    @Autowired GenericObjectPool<CatListVO> catListVOPool; // 猫咪列表VO对象池
    @Autowired GenericObjectPool<PostVO> postVOPool; // 帖子VO对象池
    @Autowired GenericObjectPool<CoordinateVO> coordinateVOPool; // 坐标VO对象池

    /**
     * @Description 获取猫咪列表VO对象
     * @return CatListVO
     */
    public CatListVO getCatListVO() {
        try {
            return catListVOPool.borrowObject();
        } catch (Exception e) {
            return new CatListVO();
        }
    }
    /**
     * @Description 归还猫咪列表VO对象
     * @param catListVO CatListVO
     */
    public void returnCatListVO(CatListVO catListVO) {
        try {
            catListVOPool.returnObject(catListVO);
        } catch (Exception e) {
            log.error("归还猫咪列表VO对象失败", e);
        }
    }

    /**
     * @Description 获取帖子VO对象
     * @return PostVO
     */
    public PostVO getPostVO() {
        try {
            return postVOPool.borrowObject();
        } catch (Exception e) {
            // 如果获取对象失败，创建新对象
            return new PostVO();
        }
    }
    /**
     * @Description 归还帖子VO对象
     * @param postVO PostVO
     */
    public void returnPostVO(PostVO postVO) {
        try {
            postVOPool.returnObject(postVO);
        } catch (Exception e) {
            // 处理异常
            log.error("归还帖子VO对象失败", e);
        }
    }

    /**
     * @Description 获取坐标VO对象
     * @return CoordinateVO
     */
    public CoordinateVO getCoordinateVO() {
        try {
            return coordinateVOPool.borrowObject();
        } catch (Exception e) {
            return new CoordinateVO();
        }
    }
    /**
     * @Description 归还坐标VO对象
     * @param coordinateVO CoordinateVO
     */
    public void returnCoordinateVO(CoordinateVO coordinateVO) {
        try {
            coordinateVOPool.returnObject(coordinateVO);
        } catch (Exception e) {
            log.error("归还坐标VO对象失败", e);
        }
    }
}
