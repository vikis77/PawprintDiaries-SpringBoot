package com.qin.catcat;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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


	@Test
	void contextLoadsOne() throws Exception{
		System.out.println("获取的数据库连接为:"+dataSource.getConnection());
	}

	@Test
	public void generateID(){
		System.out.println("111");
		System.out.println("@@"+generatorIdUtil.GeneratorRandomId());
	}
	@Test
	public void test(){
		Page<Cat> pageObj = new Page<>(1,1);
        catMapper.selectPage(pageObj, null);
        List<Cat> pageinfo = pageObj.getRecords();
		log.info(pageinfo.toString());
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