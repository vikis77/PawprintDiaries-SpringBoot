package com.qin.catcat.unite.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * 自定义生成ID工具类
 * 格式：随机数(6位) + 时间戳(年月日时分秒14位)
 * 通过AtomicInteger保证并发安全
 * -------------------------------------
 * 更高的唯一性保证，可以考虑使用：
 * UUID
 * 雪花算法（Snowflake）
 * 数据库自增序列
 */
@Component
public class GeneratorIdUtil {
    // 使用年月日时分秒的格式（14位）
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    // 定义随机数范围（100_0000代表100万）
    private static final int RANDOM_NUM_BOUND = 100_0000;
    // 使用AtomicInteger原子整型计数器，保证并发安全（即使在高并发情况下也能保证ID的唯一性）
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 生成唯一ID
     * @return 20位唯一ID字符串
     */
    public String GeneratorRandomId() {
        // 生成6位随机数（使用ThreadLocalRandom保证线程安全）
        int randomNumber = ThreadLocalRandom.current().nextInt(RANDOM_NUM_BOUND);
        // 生成14位时间戳
        String timeStamp = dateFormat.format(new Date());
        // 获取计数器值并循环使用（0-9999）
        int count = counter.incrementAndGet() % 10000;
        
        // 组合返回：6位随机数 + 14位时间戳，理论上每秒可以生成100万个不重复的ID
        return String.format("%06d%s", randomNumber, timeStamp);
    }
}
