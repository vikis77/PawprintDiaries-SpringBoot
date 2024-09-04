package com.qin.catcat;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.entity.Cat;

// import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;


@SpringBootTest
@Slf4j
class CatcatApplicationTests {

	@Resource
	DataSource dataSource;
	@Autowired private GeneratorIdUtil generatorIdUtil;
	@Autowired CatMapper catMapper;

	//测试获取数据库连接
	@Test
	void contextLoadsOne() throws Exception{
		System.out.println("获取的数据库连接为:"+dataSource.getConnection());
	}

	//测试生成随机ID
	@Test
	public void generateID(){
		System.out.println(Timestamp.from(Instant.now()));
		System.out.println("@@"+generatorIdUtil.GeneratorRandomId());
	}

	//测试分页
	@Test
	public void test(){
		Page<Cat> pageObj = new Page<>(1,1);
        catMapper.selectPage(pageObj, null);
        List<Cat> pageinfo = pageObj.getRecords();
		log.info(pageinfo.toString());
	}

	//测试redis
	@Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Test
    void RedisTemplateTest() {
        redisTemplate.opsForValue().set("key_name", "my name is Jacky");
        System.out.println("缓存设置成功");
        String value = (String) redisTemplate.opsForValue().get("key_name");
        System.out.println(value);
    }


	    
}

// @MybatisPlusTest
// class MybatisPlusSampleTest {

//     @Autowired
//     private SampleMapper sampleMapper;

//     @Test
//     void testInsert() {
//         Sample sample = new Sample();
//         sampleMapper.insert(sample);
//         assertThat(sample.getId()).isNotNull();
//     }
// }