package com.qin.catcat.unite.mapper;



import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.vo.UserLoginVO;

@Mapper
public interface UserMapper extends BaseMapper<User> {
//     /**
//     * 根据 name 检索一条记录
//     * @param name
//     * @return
//     */
//    User getByName (String name);
}