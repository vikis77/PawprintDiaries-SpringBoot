package com.qin.catcat.unite.service.impl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.catcat.entity.UserFollow;
import com.qin.catcat.unite.common.enumclass.CatcatEnumClass;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.exception.BusinessException;
import com.qin.catcat.unite.exception.PasswordIncorrectException;
import com.qin.catcat.unite.exception.UserAlreadyExistsException;
import com.qin.catcat.unite.exception.UserNotExistException;
import com.qin.catcat.unite.exception.updatePasswordFailedException;
import com.qin.catcat.unite.mapper.PostMapper;
import com.qin.catcat.unite.mapper.UserMapper;
import com.qin.catcat.unite.mapper.UserRoleMapper;
import com.qin.catcat.unite.mapper.UserFollowMapper;
import com.qin.catcat.unite.popo.dto.RegisterDTO;
import com.qin.catcat.unite.popo.dto.UpdateProfileDTO;
import com.qin.catcat.unite.popo.dto.UserLoginDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.entity.User;
import com.qin.catcat.unite.popo.entity.UserRole;
import com.qin.catcat.unite.popo.vo.MyPageVO;
import com.qin.catcat.unite.popo.vo.UpdateProfileVO;
import com.qin.catcat.unite.service.QiniuService;
import com.qin.catcat.unite.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private UserFollowMapper userFollowMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired GeneratorIdUtil generatorIdUtil;
    @Autowired
    private QiniuService qiniuService;
    @Autowired
    private UserRoleMapper userRoleMapper;
    /**
    * 登录
    * @param 
    * @return 1验证通过 2密码错误 3用户名不存在
    */
    public String loginUser(UserLoginDTO userLoginDTO){
        //1.查询用户是否存在且合法
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();// 使用QueryWrapper构建查询条件
        queryWrapper
            .or().eq("email", userLoginDTO.getUsername())
            .or().eq("phone_number", userLoginDTO.getUsername())
            .or().eq("id", userLoginDTO.getUsername())
            .or().eq("username", userLoginDTO.getUsername())
            .eq("status",1);//账号状态是否正常
        User user = null;
        try{
            user = userMapper.selectOne(queryWrapper);
        }catch(Exception e){
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_NOT_FOUND.getCode(), CatcatEnumClass.StatusCode.USER_NOT_FOUND.getMessage());
        }

        if(user!=null){
            //2.用户如果存在，验证密码
            QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();// 使用QueryWrapper构建查询条件
            queryWrapper2
                //TODO 实际上全部字段都查了出来
                .select("password")
                .eq("username", userLoginDTO.getUsername());

            List<User> userList = userMapper.selectList(queryWrapper);//查询数据库
            if(!userList.isEmpty()){
                User user2 = userList.get(0);
                String storePassword = user2.getPassword();//数据库中存储的密码
                String enteredPassword = userLoginDTO.getPassword();//用户输入的密码
                log.info("输入密码:"+enteredPassword);
                boolean passwordMatches = passwordEncoder.matches(enteredPassword, storePassword);//密码加密比对,两个参数不能反，前面用户输入明文，后面数据库密文
                if(passwordMatches){
                    log.info("密码验证通过");
                    // 认证用户

                    // 生成 token
                    String jwt = jwtTokenProvider.generateToken(user.getUsername(), user2.getUserId());
                    log.info("生成的token："+jwt);
                    // 返回 token
                    return jwt;
                }else{
                    //密码验证失败 
                    log.info("密码验证失败");
                    throw new BusinessException(CatcatEnumClass.StatusCode.PASSWORD_INCORRECT.getCode(), CatcatEnumClass.StatusCode.PASSWORD_INCORRECT.getMessage());
                }
            }else{
                //没有找到对应的用户记录
                //TODO 冗余操作
                log.info("用户不存在");
                throw new BusinessException(CatcatEnumClass.StatusCode.USER_NOT_FOUND.getCode(), CatcatEnumClass.StatusCode.USER_NOT_FOUND.getMessage());
            }
        }else{
            //用户不存在或账号状态不正常
            log.info("用户不存在或账号状态不正常");
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_NOT_FOUND_OR_ACCOUNT_STATUS_NOT_NORMAL.getCode(), CatcatEnumClass.StatusCode.USER_NOT_FOUND_OR_ACCOUNT_STATUS_NOT_NORMAL.getMessage());

        }
        // 返回查询结果
    }

    /**
    * 用户注册
    * @param 
    * @return 
    */
    @Transactional(rollbackFor = Exception.class)
    public Boolean registerUser(RegisterDTO registerDTO){
        QueryWrapper<User> wrapper = new QueryWrapper<>();//创建条件构造器
        wrapper.eq("username", registerDTO.getUsername());//条件构造
        User storeUser = userMapper.selectOne(wrapper);//条件查询数据库

        if(storeUser!=null){
            //数据库已经存在此用户名，注册失败
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_ALREADY_EXISTS.getCode(), CatcatEnumClass.StatusCode.USER_ALREADY_EXISTS.getMessage());
        }

        //创建新用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setEmail(registerDTO.getEmail());
        //使用BCryptPasswordEncoder 加密密码
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());
        user.setPassword(encodedPassword);
        user.setStatus(1);
        user.setFollowCount(0);
        user.setFansCount(0);
        user.setPostCount(0);
        user.setIsDeleted(0);
        user.setSignature("这家伙很懒，什么都没留下"); // 默认个性签名
        user.setAvatar("PawprintDiariesLogo.jpg"); // 默认头像
        user.setNickName("换一个闪亮的名字吧"); // 默认昵称
        //保存用户到数据库
        userMapper.insert(user);

        // 设置用户角色
        UserRole userRole = new UserRole();
        userRole.setUserId(user.getUserId());
        userRole.setRoleId(3);
        userRoleMapper.insert(userRole);
        return true;
    }

    /**
    * 更新用户密码
    * @param 
    * @return 
    */
    public boolean updatePassword(String userId,String newPassword){
        //根据用户ID获取用户信息
        User user = userMapper.selectById(userId);

        //用户存在
        if(user!=null){
            //使用 BCryptPasswordEncoder 加密新密码
            String encodedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encodedPassword);

            //更新用户密码到数据库
            int result = userMapper.updateById(user);
            if(result!=1){
                throw new updatePasswordFailedException("更新密码失败");
            }
            return true;
        }else{
            throw new UserNotExistException("用户不存在");
        }
    }

    /**
    * 根据userId获取用户信息
    * @param 
    * @return 
    */
    public MyPageVO getUserProfile(String userId){
        // 获取用户信息        
        User user = userMapper.selectById(userId);

        // 根据userId获取该用户的所有帖子信息
        QueryWrapper<Post> queryWrapperPost = new QueryWrapper<>();
        queryWrapperPost.eq("author_id", userId)
                       .eq("is_deleted", 0)
                       .eq("is_adopted", 1) // 通过审核
                       .or()
                       .eq("is_adopted", 0) // 未通过审核的帖子
                       .eq("author_id", userId);
        List<Post> postsList = postMapper.selectList(queryWrapperPost);
        Integer postLikedCount = 0;
        // 处理帖子信息
        for (Post post : postsList) {
            if (post.getIsAdopted() == 0) {
                post.setTitle(post.getTitle() + "（帖子审核中）");
            }
            postLikedCount += post.getLikeCount();
        }
        // 查询用户关注数
        QueryWrapper<UserFollow> queryWrapperFollow = new QueryWrapper<>();
        queryWrapperFollow.eq("user_id", userId).eq("is_deleted", 0);
        Long followCount = userFollowMapper.selectCount(queryWrapperFollow);
        // 查询用户粉丝数
        QueryWrapper<UserFollow> queryWrapperFans = new QueryWrapper<>();
        queryWrapperFans.eq("followed_user_id", userId).eq("is_deleted", 0);
        Long fansCount = userFollowMapper.selectCount(queryWrapperFans);
        MyPageVO userInfo = new MyPageVO();
        BeanUtils.copyProperties(user, userInfo); // 把user属性拷贝到userInfo
        userInfo.setPostList(postsList);
        userInfo.setPostLikedCount(postLikedCount);
        userInfo.setPostCount(postsList.size());
        userInfo.setFollowCount(followCount.intValue());
        userInfo.setFansCount(fansCount.intValue());
        return userInfo;
    }
    
    /**
    * 更新用户信息
    * @param updateProfileDTO 更新的用户信息
    * @return 更新是否成功
    */
    public UpdateProfileVO updateProfile(UpdateProfileDTO updateProfileDTO){
        // 1、查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", updateProfileDTO.getUserId());
        User user = userMapper.selectOne(queryWrapper);
        if (user.getUserId() != Integer.parseInt(updateProfileDTO.getUserId()) && user.getUsername() != updateProfileDTO.getUsername()) {
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_ALREADY_EXISTS.getCode(), CatcatEnumClass.StatusCode.USER_ALREADY_EXISTS.getMessage());
        }
        // 头像发生变化时删除旧头像
        if (updateProfileDTO.getAvatar() != null && !updateProfileDTO.getAvatar().equals(user.getAvatar())){
            // 删除七牛云上的头像
            if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                qiniuService.deleteFile(Arrays.asList(user.getAvatar()), "user_avatar");
            }
        }
        // 更新用户信息
        User updateUser = new User();
        BeanUtils.copyProperties(updateProfileDTO, updateUser);
        updateUser.setUserId(Integer.parseInt(updateProfileDTO.getUserId()));
        // 将图片名转换为新的文件名（前端已经处理了，这里就不需要处理了）
        // String newFileName = generatorIdUtil.GeneratorRandomId() + updateProfileDTO.getAvatar().substring(updateProfileDTO.getAvatar().lastIndexOf("."));
        updateUser.setAvatar(updateProfileDTO.getAvatar());
        userMapper.updateById(updateUser);
        try {
            // 执行更新操作
            int rows = userMapper.updateById(updateUser);
            if(rows != 1){
                log.error("更新用户信息失败，影响行数：" + rows + "，用户ID：" + updateProfileDTO.getUserId());
                throw new BusinessException(CatcatEnumClass.StatusCode.UPDATE_PROFILE_FAILURE.getCode(), CatcatEnumClass.StatusCode.UPDATE_PROFILE_FAILURE.getMessage());
            }
            UpdateProfileVO updateProfileVO = new UpdateProfileVO();
            Map<String, String> fileNameConvertMap = new HashMap<>();
            fileNameConvertMap.put(updateProfileDTO.getAvatar(), updateProfileDTO.getAvatar());
            updateProfileVO.setFileNameConvertMap(fileNameConvertMap);
            return updateProfileVO;
        } catch (Exception e) {
            log.error("更新用户信息发生异常：" + e.getMessage(), e);
            throw new BusinessException(CatcatEnumClass.StatusCode.UPDATE_PROFILE_FAILURE.getCode(), CatcatEnumClass.StatusCode.UPDATE_PROFILE_FAILURE.getMessage());
        }
    }

    //根据ID获取昵称
    public String getNicknameFromId(String id){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id).select("nick_name");
        User user = userMapper.selectOne(queryWrapper);
        if(user==null){
            //TODO throw new
            return "根据ID获取昵称失败";
        }else{
            String nickname = user.getNickName();
            return nickname;
        }
    }

    /**
     * 关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean followUser(Long userId){
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        // 查看是否已经关注
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId).eq("followed_user_id", userId).eq("is_deleted", 0);
        UserFollow userFollow = userFollowMapper.selectOne(queryWrapper);
        if(userFollow!=null){
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_ALREADY_FOLLOWED.getCode(), CatcatEnumClass.StatusCode.USER_ALREADY_FOLLOWED.getMessage());
        }
        // 获取当前登录用户ID
        UserFollow newUserFollow = new UserFollow();
        newUserFollow.setUserId(Long.parseLong(currentUserId));
        newUserFollow.setFollowedUserId(userId);
        newUserFollow.setCreateTime(LocalDateTime.now());
        newUserFollow.setUpdateTime(LocalDateTime.now());
        newUserFollow.setIsDeleted(0);
        userFollowMapper.insert(newUserFollow);
        return true;
    }

    /**
     * 取消关注用户
     * @param userId 用户ID
     * @return 操作结果
     */
    public boolean unfollowUser(Long userId){
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        QueryWrapper<UserFollow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserId).eq("followed_user_id", userId).eq("is_deleted", 0);
        UserFollow userFollow = userFollowMapper.selectOne(queryWrapper);
        if(userFollow==null){
            throw new BusinessException(CatcatEnumClass.StatusCode.USER_NOT_FOLLOWED.getCode(), CatcatEnumClass.StatusCode.USER_NOT_FOLLOWED.getMessage());
        }
        userFollow.setIsDeleted(1);
        userFollow.setUpdateTime(LocalDateTime.now());
        userFollowMapper.updateById(userFollow);
        return true;
    }
}
