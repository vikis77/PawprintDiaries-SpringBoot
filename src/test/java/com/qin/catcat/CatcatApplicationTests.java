package com.qin.catcat;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

// import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;

import jakarta.annotation.Resource;


@SpringBootTest
class CatcatApplicationTests {

	@Resource
	DataSource dataSource;

	@Test
	void contextLoadsOne() throws Exception{
		System.out.println("获取的数据库连接为:"+dataSource.getConnection());
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