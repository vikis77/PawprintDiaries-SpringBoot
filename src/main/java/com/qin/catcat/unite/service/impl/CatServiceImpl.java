package com.qin.catcat.unite.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.catalina.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qin.catcat.unite.common.constant.Constant;
import com.qin.catcat.unite.common.utils.CacheUtils;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.common.utils.JwtTokenProvider;
import com.qin.catcat.unite.common.utils.ObjectPoolUtil;
import com.qin.catcat.unite.common.utils.TokenHolder;
import com.qin.catcat.unite.manage.CatManage;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.mapper.CatPicsMapper;
import com.qin.catcat.unite.mapper.CatTimeLineEventMapper;
import com.qin.catcat.unite.mapper.CoordinateMapper;
import com.qin.catcat.unite.param.AddCatTimelineParam;
import com.qin.catcat.unite.param.UpdateCatTimelineParam;
import com.qin.catcat.unite.param.UploadCoordinateParam;
import com.qin.catcat.unite.popo.dto.CatDTO;
import com.qin.catcat.unite.popo.dto.CoordinateDTO;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.CatPics;
import com.qin.catcat.unite.popo.entity.CatTimeLineEvent;
import com.qin.catcat.unite.popo.entity.Coordinate;
import com.qin.catcat.unite.popo.vo.AddCatVO;
import com.qin.catcat.unite.popo.vo.CatListVO;
import com.qin.catcat.unite.popo.vo.CatTimelineVO;
import com.qin.catcat.unite.popo.vo.CoordinateVO;
import com.qin.catcat.unite.popo.vo.DataAnalysisVO;
import com.qin.catcat.unite.popo.vo.UpdateCatVO;
import com.qin.catcat.unite.service.CatService;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;

@Service
@Slf4j
public class CatServiceImpl extends ServiceImpl<CatMapper, Cat> implements CatService {
    @Autowired
    CatMapper catMapper;
    @Autowired
    CatPicsMapper catPicsMapper;
    @Autowired
    CoordinateMapper coordinateMapper;
    @Autowired
    GeneratorIdUtil generatorIdUtil;
    @Autowired
    JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RBloomFilter<String> likeBloomFilter; // 点赞布隆过滤器
    @Autowired
    CacheUtils cacheUtils;
    @Autowired
    CatManage catManage;
    @Autowired
    CatTimeLineEventMapper catTimeLineEventMapper;
    @Autowired
    ObjectPoolUtil objectPoolUtil; // 对象池工具类

    /**
     * @Description 新增猫猫
     * @param catDTO
     */
    @Override
    public AddCatVO createCat(CatDTO catDTO) {
        Cat cat = new Cat();
        // 属性拷贝DTO to entity
        BeanUtils.copyProperties(catDTO, cat);
        // 根据年龄反推计算生日
        cat.setBirthday(LocalDateTime.now().minusMonths(catDTO.getAge()));
        // 将图片名转换为新的文件名
        String newFileName = generatorIdUtil.GeneratorRandomId()
                + catDTO.getAvatar().substring(catDTO.getAvatar().lastIndexOf("."));
        cat.setAvatar(newFileName);
        cat.setIsAdopted(0);
        cat.setIsDeleted(0);
        cat.setTrending(0);
        cat.setLikeCount(0);
        catMapper.insert(cat);

        // 更新缓存
        cacheUtils.remove(Constant.HOT_FIRST_TIME_CAT_LIST);
        log.info("新增完成");
        AddCatVO addCatVO = new AddCatVO();
        Map<String, String> fileNameConvertMap = new HashMap<>();
        fileNameConvertMap.put(catDTO.getAvatar(), newFileName);
        addCatVO.setFileNameConvertMap(fileNameConvertMap);
        return addCatVO;
    }

    /**
     * @Description 查询全部猫猫信息
     * @return
     */
    // @Cacheable(value = "Hot_FirstTime_CatList", cacheManager =
    // "redisCacheManager")
    @Override
    public List<CatListVO> CatList() {
        // 从缓存中获取数据
        @SuppressWarnings("unchecked")
        List<CatListVO> cats = cacheUtils.getWithMultiLevel(Constant.CAT_LIST_FOR_CATCLAW, List.class, () -> {
            // 如果缓存中没有数据，则从数据库中查询
            log.info("缓存中没有数据，从数据库中查询");
            return catManage.getCatListForCatClaw();
        });
        // log.info(TokenHolder.getToken());
        List<CatListVO> catListVOs = new ArrayList<>();
        // 如果用户未登录，则不检查点赞
        if (StringUtils.isBlank(TokenHolder.getToken())) {
            for (CatListVO cat : cats) {
                catListVOs.add(cat);
            }
            return catListVOs;
        }
        // 如果用户已登录，则检查点赞
        String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
        for (CatListVO cat : cats) {
            // 检查是否已点赞
            cat.setIsLikedToday(likeBloomFilter.contains(generateLikeKey(currentUserId, cat.getCatId())));
            catListVOs.add(cat);
        }
        return catListVOs;
    }

    @Override
    public IPage<Cat> selectByPage(int page, int size) {
        Page<Cat> pageObj = new Page<>(page, size);
        QueryWrapper<Cat> wrapper = new QueryWrapper<>();
        IPage<Cat> pageInfo = catMapper.selectPage(pageObj, wrapper);
        log.info("查找全部猫猫信息完成");
        return pageInfo;
    }

    @Override
    public List<Cat> selectByName(String name) {
        if (name == null || name.isEmpty()) {
            log.warn("猫猫名字不能为空");
            // TODO throw new
            return null;
        }

        QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("catname", name);
        List<Cat> cats = catMapper.selectList(queryWrapper);

        if (cats == null || cats.isEmpty()) {
            log.info("没有找到名字为 {} 的猫猫", name);
        } else {
            log.info("根据猫猫名字查找猫猫信息完成，找到 {} 只猫猫", cats.size());
        }

        log.info("根据猫猫名字查找猫猫信息完成");
        return cats;
    }

    @Override
    public Cat getById(Long ID) {
        Cat cat = catMapper.selectById(ID);
        log.info("根据猫猫ID查找某一只猫猫信息完成");
        return cat;
    }

    /**
     * @Description 更新猫猫
     * @param cat
     */
    public UpdateCatVO update(Cat cat) {
        UpdateCatVO updateCatVO = new UpdateCatVO();
        Map<String, String> fileNameConvertMap = new HashMap<>();
        if (cat.getAvatar() != null && !cat.getAvatar().isEmpty()) {
            // 将图片名转换为新的文件名
            String newFileName = generatorIdUtil.GeneratorRandomId()
                    + cat.getAvatar().substring(cat.getAvatar().lastIndexOf("."));
            cat.setAvatar(newFileName);
            fileNameConvertMap.put(cat.getAvatar(), newFileName);
        }
        catMapper.updateById(cat);
        // 更新缓存
        cacheUtils.remove(Constant.HOT_FIRST_TIME_CAT_LIST);
        cacheUtils.remove(Constant.CAT_LIST_FOR_CATCLAW);
        log.info("更新{}猫信息完成", cat.getCatname());
        updateCatVO.setFileNameConvertMap(fileNameConvertMap);
        return updateCatVO;
    }

    public void delete(Long ID) {
        // 逻辑删除
        Cat cat = catMapper.selectById(ID);
        cat.setIsDeleted(1);
        catMapper.updateById(cat);
        log.info("根据猫猫ID删除信息完成");
    }

    @Override
    public void likeCat(Long catId) {
        // 当前用户已登录
        if (StringUtils.isNotBlank(TokenHolder.getToken())) {
            // 获取当前用户ID
            String currentUserId = jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken());
            // 生成当天的点赞key
            String likeKey = generateLikeKey(currentUserId, catId);
            // 检查是否已经点赞
            if (likeBloomFilter.contains(likeKey)) {
                log.info("用户 {} 今天已经给猫咪 {} 点过赞了", currentUserId, catId);
                return;
            }
            // 添加到布隆过滤器
            likeBloomFilter.add(likeKey);
            // 更新猫咪的点赞数
            Cat cat = catMapper.selectById(catId);
            cat.setLikeCount(cat.getLikeCount() + 1);
            catMapper.updateById(cat);
            // 更新缓存
            cacheUtils.remove(Constant.HOT_FIRST_TIME_CAT_LIST);
            log.info("用户 {} 成功给猫咪 {} 点赞", currentUserId, catId);
        } else {
            log.warn("用户未登录，无法进行点赞操作");
        }
    }

    // 生成点赞key
    private String generateLikeKey(String userId, Long catId) {
        return String.format("like:%s:%d:%s", userId, catId, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
    }

    // ================ 猫咪照片相关方法 ================

    public List<CatPics> selectPhotoById(String ID, int page, int size) {
        // 创建分页对象
        Page<CatPics> pageObg = new Page<>(page, size);

        // 构造查询条件
        QueryWrapper<CatPics> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", ID)
                .orderByDesc("create_time"); // 最新的图片在前

        // 执行查询并返回结果
        IPage<CatPics> result = catPicsMapper.selectPage(pageObg, queryWrapper);

        log.info("根据猫猫ID:{} 查找猫猫图片完成, 返回{}张图片", ID, result.getRecords().size());
        return result.getRecords(); // 返回图片列表
    }

    @Override
    public String uploadPhoto(Long catId, MultipartFile file) {
        // TODO: 实现照片上传逻辑
        return "photo_url";
    }

    @Override
    public List<String> getPhotos(Long catId) {
        // TODO: 实现获取照片列表逻辑
        return List.of();
    }

    // ================ 猫咪坐标相关方法 ================

    public void addCoordinate(CoordinateDTO coordinateDTO) {
        // 获取列表中的猫猫名
        List<String> catNames = coordinateDTO.getCatNames();

        // 遍历猫名列表
        if (!CollectionUtils.isEmpty(catNames)) {
            for (String catName : catNames) {
                Coordinate coordinate = new Coordinate();
                // 属性拷贝DTO to entity
                BeanUtils.copyProperties(coordinateDTO, coordinate);

                // 生成ID并设置这一条坐标信息的唯一ID
                Long ID = Long.parseLong(generatorIdUtil.GeneratorRandomId());
                coordinate.setId(ID);

                // 设置更新时间
                coordinate.setUpdateTime(Timestamp.from(Instant.now()));

                // 根据猫猫名查找猫猫ID
                QueryWrapper<Cat> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("catname", catName);
                Cat cat = catMapper.selectOne(queryWrapper);

                if (cat == null) {
                    log.info("更新失败,猫猫名不存在" + catName);
                    // TODO throw new
                    // throw new CatNotFoundException("猫猫名不存在: " + catName);
                    continue;
                }
                coordinate.setCatId(cat.getCatId());

                // 插入
                coordinateMapper.insert(coordinate);
                log.info("新增猫猫坐标完成");
            }
        } else {
            log.info("猫猫名列表为空");
            // TODO throw new
        }
    }

    @Override
    public void addUploadCoordinate(UploadCoordinateParam uploadCoordinateParam) {
        Coordinate coordinate = new Coordinate();
        coordinate.setCatId(uploadCoordinateParam.getCatId());
        coordinate.setLatitude(uploadCoordinateParam.getLatitude());
        coordinate.setLongitude(uploadCoordinateParam.getLongitude());
        coordinate.setUploader(uploadCoordinateParam.getUploader());
        coordinate.setDescription("");
        coordinate.setArea("");
        coordinate.setUpdateTime(Timestamp.from(Instant.now()));
        coordinateMapper.insert(coordinate);
    }

    public void deleteCoordinate(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            log.info("猫猫ID列表为空");
            // TODO throw new IllegalArgumentException("猫猫ID列表不能为空");
        }

        // 批量删除
        boolean removed = this.removeByIds(ids);
        if (!removed) {
            log.info("删除失败");
            // TODO throw new RuntimeException("删除猫猫坐标失败");
        }
        log.info("删除猫猫坐标完成");
    }

    public void updateCoordinate(Coordinate coordinate) {
        coordinateMapper.updateById(coordinate);
        log.info("更新猫猫坐标完成");
    }

    public List<CoordinateVO> selectCoordinate() {
        List<CoordinateVO> CoordinateVOs = coordinateMapper.getAllCatsWithLatestCoordinates();
        log.info("查找猫猫坐标完成");
        return CoordinateVOs;
    }

    public IPage<CoordinateVO> selectCoordinateByCatId(Long cat_id, int page, int size) {
        Page<CoordinateVO> pageObj = new Page<>(page, size);
        IPage<CoordinateVO> coordinateVOIPage = coordinateMapper.selectCoordinatesByCatId(pageObj, cat_id);
        log.info("分页查询单只猫的历史坐标信息完成");
        return coordinateVOIPage;
    }

    public List<CoordinateVO> selectCoordinateByDate(String date) {
        // 构建查询条件 - 使用LIKE进行日期匹配
        QueryWrapper<Coordinate> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("update_time", date)
                .orderByDesc("update_time"); // 按时间倒序排序

        // 执行查询
        List<Coordinate> coordinates = coordinateMapper.selectList(queryWrapper);

        // 使用HashSet存储已处理的猫ID
        Set<Long> processedCatIds = new HashSet<>();
        List<CoordinateVO> coordinateVOs = new ArrayList<>();

        // 遍历所有坐标记录
        for (Coordinate coordinate : coordinates) {
            Long catId = coordinate.getCatId();
            // 如果这只猫还没处理过
            if (!processedCatIds.contains(catId)) {
                CoordinateVO vo = new CoordinateVO();
                BeanUtils.copyProperties(coordinate, vo);
                // 查询猫名
                Cat cat = catMapper.selectById(catId);
                if (cat != null) {
                    vo.setCatName(cat.getCatname());
                }
                coordinateVOs.add(vo);
                processedCatIds.add(catId);
            }
        }

        log.info("按日期{}查询小猫坐标信息完成,共查询到{}条记录", date, coordinateVOs.size());
        return coordinateVOs;
    }

    public List<CoordinateVO> selectCoordinateByDateAndCatId(String date, Long catId) {
        QueryWrapper<Coordinate> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("update_time", date)
                .eq("cat_id", catId)
                .orderByDesc("update_time"); // 按时间倒序排序
        List<Coordinate> coordinates = coordinateMapper.selectList(queryWrapper);
        List<CoordinateVO> coordinateVOs = new ArrayList<>();

        // 查询猫名
        Cat cat = catMapper.selectById(catId);
        String catName = cat != null ? cat.getCatname() : null;

        for (Coordinate coordinate : coordinates) {
            CoordinateVO vo = new CoordinateVO();
            BeanUtils.copyProperties(coordinate, vo);
            vo.setCatName(catName); // 设置猫名
            coordinateVOs.add(vo);
        }
        return coordinateVOs;
    }

    @Override
    public List<CoordinateDTO> getLocations(Long catId) {
        // TODO: 实现获取位置列表逻辑
        return List.of();
    }

    // ================ 数据分析相关方法 ================

    public DataAnalysisVO analysis() {
        List<Cat> cats = catMapper.findAll();
        HashMap<String, Integer> ageDistribution = new HashMap<>();
        // HashMap<String, Integer> quantityChange = new HashMap<>();
        HashMap<String, Integer> healthStatus = new HashMap<>();
        HashMap<String, Integer> areaDistribution = new HashMap<>();
        HashMap<String, Integer> genderRatio = new HashMap<>();
        HashMap<String, Integer> sterilizationRatio = new HashMap<>();
        HashMap<String, Integer> vaccinationRatio = new HashMap<>();
        // 初始化
        ageDistribution.put("3个月以内", 0);
        ageDistribution.put("3-6个月", 0);
        ageDistribution.put("6-12个月", 0);
        ageDistribution.put("12-18个月", 0);
        ageDistribution.put("18-24个月", 0);
        ageDistribution.put("24个月以上", 0);
        healthStatus.put("健康", 0);
        healthStatus.put("疾病", 0);
        healthStatus.put("营养不良", 0);
        healthStatus.put("肥胖", 0);
        areaDistribution.put("北门", 0);
        areaDistribution.put("岐头", 0);
        areaDistribution.put("凤翔", 0);
        areaDistribution.put("厚德楼", 0);
        areaDistribution.put("香晖苑", 0);
        genderRatio.put("公猫", 0);
        genderRatio.put("母猫", 0);
        sterilizationRatio.put("已绝育", 0);
        sterilizationRatio.put("未绝育", 0);
        vaccinationRatio.put("已接种", 0);
        vaccinationRatio.put("未接种", 0);

        for (Cat cat : cats) {
            Integer age = cat.getAge();
            if (age < 3) {
                ageDistribution.put("3个月以内", ageDistribution.getOrDefault("3个月以内", 0) + 1);
            } else if (age >= 3 && age < 6) {
                ageDistribution.put("3-6个月", ageDistribution.getOrDefault("3-6个月", 0) + 1);
            } else if (age >= 6 && age < 12) {
                ageDistribution.put("6-12个月", ageDistribution.getOrDefault("6-12个月", 0) + 1);
            } else if (age >= 12 && age < 18) {
                ageDistribution.put("12-18个月", ageDistribution.getOrDefault("12-18个月", 0) + 1);
            } else if (age >= 18 && age < 24) {
                ageDistribution.put("18-24个月", ageDistribution.getOrDefault("18-24个月", 0) + 1);
            } else if (age >= 24) {
                ageDistribution.put("24个月以上", ageDistribution.getOrDefault("24个月以上", 0) + 1);
            }

            if (cat.getHealthStatus().equals("健康")) {
                healthStatus.put("健康", healthStatus.getOrDefault("健康", 0) + 1);
            } else if (cat.getHealthStatus().equals("疾病")) {
                healthStatus.put("疾病", healthStatus.getOrDefault("疾病", 0) + 1);
            } else if (cat.getHealthStatus().equals("营养不良")) {
                healthStatus.put("营养不良", healthStatus.getOrDefault("营养不良", 0) + 1);
            } else if (cat.getHealthStatus().equals("肥胖")) {
                healthStatus.put("肥胖", healthStatus.getOrDefault("肥胖", 0) + 1);
            }

            if (cat.getArea().equals("北门")) {
                areaDistribution.put("北门", areaDistribution.getOrDefault("北门", 0) + 1);
            } else if (cat.getArea().equals("岐头")) {
                areaDistribution.put("岐头", areaDistribution.getOrDefault("岐头", 0) + 1);
            } else if (cat.getArea().equals("凤翔")) {
                areaDistribution.put("凤翔", areaDistribution.getOrDefault("凤翔", 0) + 1);
            } else if (cat.getArea().equals("厚德楼")) {
                areaDistribution.put("厚德楼", areaDistribution.getOrDefault("厚德楼", 0) + 1);
            } else if (cat.getArea().equals("香晖苑")) {
                areaDistribution.put("香晖苑", areaDistribution.getOrDefault("香晖苑", 0) + 1);
            }

            if (cat.getGender().equals(1)) {
                genderRatio.put("公猫", genderRatio.getOrDefault("公猫", 0) + 1);
            } else if (cat.getGender().equals(0)) {
                genderRatio.put("母猫", genderRatio.getOrDefault("母猫", 0) + 1);
            }

            if (cat.getSterilizationStatus().equals("已绝育")) {
                sterilizationRatio.put("已绝育", sterilizationRatio.getOrDefault("已绝育", 0) + 1);
            } else if (cat.getSterilizationStatus().equals("未绝育")) {
                sterilizationRatio.put("未绝育", sterilizationRatio.getOrDefault("未绝育", 0) + 1);
            }

            if (cat.getVaccinationStatus().equals("已接种")) {
                vaccinationRatio.put("已接种", vaccinationRatio.getOrDefault("已接种", 0) + 1);
            } else if (cat.getVaccinationStatus().equals("未接种")) {
                vaccinationRatio.put("未接种", vaccinationRatio.getOrDefault("未接种", 0) + 1);
            }
        }

        DataAnalysisVO dataAnalysisVO = new DataAnalysisVO();
        dataAnalysisVO.setAgeDistribution(ageDistribution);
        // dataAnalysisVO.setQuantityChange(quantityChange);
        dataAnalysisVO.setHealthStatus(healthStatus);
        dataAnalysisVO.setAreaDistribution(areaDistribution);
        dataAnalysisVO.setGenderRatio(genderRatio);
        dataAnalysisVO.setSterilizationRatio(sterilizationRatio);
        dataAnalysisVO.setVaccinationRatio(vaccinationRatio);

        return dataAnalysisVO;
    }

    /**
     * @Description 获取猫咪时间线
     * @param catId 猫咪ID
     * @return 猫咪时间线VO列表
     */
    @Override
    public List<CatTimelineVO> getCatTimeline(Long catId) {
        List<CatTimelineVO> catTimelineVOs = new ArrayList<>();
        QueryWrapper<CatTimeLineEvent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("cat_id", catId);
        queryWrapper.orderByDesc("date");
        queryWrapper.eq("is_deleted", 0);
        queryWrapper.last("limit 10");
        List<CatTimeLineEvent> catTimeLineEvents = catTimeLineEventMapper.selectList(queryWrapper);
        for (CatTimeLineEvent catTimeLineEvent : catTimeLineEvents) {
            CatTimelineVO catTimelineVO = new CatTimelineVO();
            BeanUtils.copyProperties(catTimeLineEvent, catTimelineVO);
            // 将Date类型转换为字符串格式,使用SimpleDateFormat避免解析错误
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            catTimelineVO.setDate(sdf.format(catTimeLineEvent.getDate()));
            catTimelineVOs.add(catTimelineVO);
        }
        return catTimelineVOs;
    }

    /**
     * @Description 新增猫咪时间线
     * @param param 新增猫咪时间线参数
     */
    @Override
    public void addCatTimeline(AddCatTimelineParam param) {
        // 获取当前用户ID
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        CatTimeLineEvent catTimeLineEvent = new CatTimeLineEvent();
        BeanUtils.copyProperties(param, catTimeLineEvent);
        catTimeLineEvent.setCreateUserId(userId);
        catTimeLineEvent.setIsDeleted(0);
        catTimeLineEvent.setDate(Date.valueOf(param.getDate()));
        catTimeLineEventMapper.insert(catTimeLineEvent);
    }

    /**
     * @Description 更新猫咪时间线
     * @param param 更新猫咪时间线参数
     */
    @Override
    public void updateCatTimeline(UpdateCatTimelineParam param) {
        // 获取当前用户ID
        Integer userId = Integer.parseInt(jwtTokenProvider.getUserIdFromJWT(TokenHolder.getToken()));
        CatTimeLineEvent catTimeLineEvent = new CatTimeLineEvent();
        BeanUtils.copyProperties(param, catTimeLineEvent);
        catTimeLineEvent.setCreateUserId(userId);
        catTimeLineEventMapper.updateById(catTimeLineEvent);
    }

    /**
     * @Description 删除猫咪时间线
     * @param id 时间线ID
     */
    @Override
    public void deleteCatTimeline(Integer id) {
        CatTimeLineEvent catTimeLineEvent = new CatTimeLineEvent();
        catTimeLineEvent.setId(id);
        catTimeLineEvent.setIsDeleted(1);
        catTimeLineEventMapper.updateById(catTimeLineEvent);
    }
}
