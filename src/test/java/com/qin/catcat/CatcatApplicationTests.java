package com.qin.catcat;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;  // 导入status()
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content; // 导入content()

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.entity.Cat;

// import com.baomidou.mybatisplus.test.autoconfigure.MybatisPlusTest;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
class CatcatApplicationTests {

	@Resource
	DataSource dataSource;
	@Autowired
	private GeneratorIdUtil generatorIdUtil;
	@Autowired
	CatMapper catMapper;

	// 测试获取数据库连接
	@Test
	void contextLoadsOne() throws Exception {
		System.out.println("获取的数据库连接为:" + dataSource.getConnection());
	}

	// 测试生成随机ID
	@Test
	public void generateID() {
		System.out.println(Timestamp.from(Instant.now()));
		System.out.println("@@" + generatorIdUtil.GeneratorRandomId());
	}

	// 测试分页
	@Test
	public void test() {
		Page<Cat> pageObj = new Page<>(1, 1);
		catMapper.selectPage(pageObj, null);
		List<Cat> pageinfo = pageObj.getRecords();
		log.info(pageinfo.toString());
	}

	// 测试redis
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Test
	void RedisTemplateTest() {
		redisTemplate.opsForValue().set("key_name", "my name is Jacky");
		System.out.println("缓存设置成功");
		String value = (String) redisTemplate.opsForValue().get("key_name");
		System.out.println(value);
	}

// 	@Test
//     public void uploadLocalImageToEcs() throws IOException {
//         // 本地图片路径
//         String localImagePath = "src/main/resources/cat_pics/biyan.jpg";
//         // 服务器存储路径
//         String serverImagePath = "/var/www/images/biyan.jpg";

//         // 读取本地图片文件
//         FileInputStream inputFile = new FileInputStream(localImagePath);
//         MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "biyan.jpg", "image/jpeg", FileCopyUtils.copyToByteArray(inputFile));

//         // 调用上传方法
//         ResponseEntity<String> response = uploadFile(mockMultipartFile, serverImagePath);

//         // 检查上传是否成功
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         System.out.println("图片URL: " + response.getBody());
//     }

//     // 上传文件的方法
//     private ResponseEntity<String> uploadFile(MockMultipartFile file, String destinationPath) {
//         try {
//             // 目标文件
//             File dest = new File(destinationPath);
            
//             // 检查目标目录是否存在，如果不存在则创建
//             File directory = new File(dest.getParent());
//             if (!directory.exists()) {
//                 directory.mkdirs();
//             }

//             // 保存文件到ECS服务器
//             file.transferTo(dest);

//             // 返回文件的访问URL
//             String fileUrl = "http://47.113.190.94/images/" + file.getOriginalFilename();
//             return ResponseEntity.ok(fileUrl);
//         } catch (IOException e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("上传失败");
//         }
//     }

}

// @MybatisPlusTest
// class MybatisPlusSampleTest {

// @Autowired
// private SampleMapper sampleMapper;

// @Test
// void testInsert() {
// Sample sample = new Sample();
// sampleMapper.insert(sample);
// assertThat(sample.getId()).isNotNull();
// }
// }