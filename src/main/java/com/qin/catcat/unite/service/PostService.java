package com.qin.catcat.unite.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.qin.catcat.unite.popo.dto.PostDTO;
import com.qin.catcat.unite.popo.entity.Post;
import com.qin.catcat.unite.popo.vo.ApplyPostVO;
import com.qin.catcat.unite.popo.vo.HomePostVO;
import com.qin.catcat.unite.popo.vo.SinglePostVO;

public interface PostService {
    /**
    * 新增帖子
    * @param 
    * @return 
    */
    public Boolean add(PostDTO post);

    /**
    * 查询全部帖子
    * @param 
    * @return 
    */
    public List<Post> getAllPost();

    /**
    * 根据帖子ID查询帖子
    * @param 
    * @return 
    */
    public SinglePostVO getPostByPostId(String PostId);

    /**
    * 根据发布时间分页查询前十条帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getPostBySendtime(int page,int pageSize);

    /**
    * 权重随机推送帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getRandomWeightedPosts(int page,int pageSize);

    /**
    * 时间衰减权重推送帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getTimeDecayWeightedPosts(int page,int pageSize);

    /**
    * 基于用户兴趣推送帖子
    * @param 
    * @return 
    */
    public List<HomePostVO> getRecommendedPosts(int page,int pageSize);

    /**
    * 根据发布时间分页查询待审核帖子
    * @param 
    * @return 
    */
    public List<ApplyPostVO> getApplyPostBySendtimeForPage(int page,int pageSize);

    /**
    * 帖子通过审核
    * @param 
    * @return 
    */
    public Boolean passApprove(Long postId);

    /**
    * 帖子审核拒绝通过
    * @param 
    * @return 
    */
    public Boolean refuseApprove(Long postId);

    /**
    * 根据点赞数分页查询前十条帖子
    * @param 
    * @return 
    */
    public List<Post> getPostByLikecount(int page,int pageSize);

    /**
    * 根据标题搜索相关帖子（匹配标题、匹配文章内容）分页搜索
    * @param 
    * @return 
    */
    public IPage<Post> getPostByTitle(String title,int page,int pageSize);

    /**
    * 根据作者昵称搜索相关帖子
    * @param 
    * @return 
    */
    public IPage<Post> getPostByNickname(String nickName,int page,int pageSize);
    
    /**
    * 判断是否有权限删除
    * @param 
    * @return 
    */
    public Boolean isLegalDelete(String postId);

    /**
    * 根据帖子ID删除帖子
    * @param 
    * @return 
    */
    public Boolean delete(String postId);

    /**
    * 更新帖子
    * @param 
    * @return 
    */
    public int update(Post post);

    /**
    * 根据帖子ID点赞
    * @param 
    * @return 
    */
    public int likePost(Long postId);

    /**
    * 根据帖子ID取消点赞
    * @param 
    * @return 
    */
    public int unlikePost(Long postId);   

    /**
    * 收藏帖子
    * @param 
    * @return 
    */
    public int collectPost(Long postId);

    /**
    * 取消收藏帖子
    * @param 
    * @return 
    */
    public int unCollectPost(Long postId);
}
