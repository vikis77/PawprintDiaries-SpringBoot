package com.qin.catcat.unite.config;

// import org.apache.http.ssl.SSLContexts;
// import org.apache.http.ssl.TrustStrategy;
// import javax.net.ssl.SSLContext;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.elasticsearch.client.ClientConfiguration;
// import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
// import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
// import java.time.Duration;


/**
 * @Description ElasticSearch相关配置 - 当前已停用.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-
 */
// 暂时停用ES相关配置
/*
@Configuration
// 启用Elasticsearch仓库
@EnableElasticsearchRepositories(basePackages = "com.qin.catcat.unite.repository")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.username}")
    private String username;

    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.elasticsearch.uris}")
    private String uris;

    // 配置Elasticsearch客户端
    @Override
    public ClientConfiguration clientConfiguration() {
        // 本地开发启用这段：
        // try {
        //     // 创建SSLContext，信任所有证书
        //     SSLContext sslContext = SSLContexts.custom()
        //         .loadTrustMaterial(null, (TrustStrategy) (x509Certificates, s) -> true)
        //         .build();

        //     return ClientConfiguration.builder()
        //         .connectedTo(uris.replace("https://", ""))
        //         .usingSsl(sslContext)
        //         .withBasicAuth(username, password)
        //         .withConnectTimeout(Duration.ofSeconds(5))
        //         .withSocketTimeout(Duration.ofSeconds(3))
        //         .build();
        // } catch (Exception e) {
        //     throw new RuntimeException("SSL Context creation failed", e);
        // }


        // 生产环境启用这段：
        return ClientConfiguration.builder()
            .connectedTo(uris.replace("http://", ""))
            .withBasicAuth(username, password)
            .withConnectTimeout(Duration.ofSeconds(5))
            .withSocketTimeout(Duration.ofSeconds(3))
            .build();
    }
}
*/ 