package com.qin.catcat;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;  // 导入status()
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
// 

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qin.catcat.unite.common.utils.ElasticsearchUtil;
import com.qin.catcat.unite.common.utils.GeneratorIdUtil;
import com.qin.catcat.unite.mapper.CatMapper;
import com.qin.catcat.unite.popo.entity.Cat;
import com.qin.catcat.unite.popo.entity.EsPostIndex;
// import com.qin.catcat.unite.repository.EsPostIndexRepository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
@AutoConfigureMockMvc
// @ImportAutoConfiguration(exclude = {SecurityAutoConfiguration.class})
class CatcatApplicationTests {

	@Resource
	DataSource dataSource;
	@Autowired
	private GeneratorIdUtil generatorIdUtil;
	@Autowired
	CatMapper catMapper;
	// @Autowired
    // private EsPostIndexRepository esPostIndexRepository;
	@Autowired
	private ElasticsearchUtil elasticsearchUtil;

    // 缓存过期时间 5分钟
    @Value("${spring.cache.caffeine.spec}")
    private int caffeineSpec; 
    // 缓存最大容量 1000
    @Value("${spring.cache.caffeine.maximum-size}")
    private int cacheMaxSize; 

    @Test
    public void testCache() {
        System.out.println("缓存过期时间: " + caffeineSpec);
        System.out.println("缓存最大容量: " + cacheMaxSize);
    }

	// EsPostIndexRepository获取所有索引
	@Test
	public void getAllIndices() {
		// List<EsPostIndex> indices = esPostIndexRepository.findAll();
		// System.out.println(indices);
	}


	// 获取所有索引
	// @Test
	// public void getAllIndices() throws IOException {
	// 	List<String> indices = elasticsearchUtil.getAllIndices();
	// 	System.out.println(indices);
	// }


	// // 测试ES查询全部
	@Test
    public void getAllPosts() {
		List<EsPostIndex> EsPostIndexs = new ArrayList<>();
		// EsPostIndexs = (List<EsPostIndex>) esPostIndexRepository.findAll();
		// 断言返回的列表不为空
		assertNotNull(EsPostIndexs);
		assertFalse(EsPostIndexs.isEmpty());
		// // 打印数据
		log.info(EsPostIndexs.toString());
		System.out.println("111");
    }

	// // 测试ES查询文章标题
    // public List<EsPostIndex> searchByTitle(String title) {
    //     return EsPostIndexRepository.findByTitle(title);
    // }

	// // 测试ES查询文章标题或内容
	// @Test
    // public List<EsPostIndex> searchByTitleOrArticle(String query) {
    //     // return EsPostIndexRepository.findByTitleOrArticle(query, query);
    //     return EsPostIndexRepository.findByTitleOrArticle("流浪猫", "流浪猫");
    // }
	
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