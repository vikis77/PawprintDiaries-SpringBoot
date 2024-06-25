package com.qin.catcat.unite.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Component;

//自定义生成ID工具类（时间戳+随机数）
@Component
public class GeneratorIdUtil {
    private  final SimpleDateFormat dateFormat = new SimpleDateFormat("MMddHHmmss");
    private  final int RANDOM_NUM_BOUND = 10000;//定义随机数范围

    public String GeneratorRandomId(){
        //生成时间戳部分
        String timeStamp = dateFormat.format(new Date());
        //生成随机数部分
        int randomNumber = ThreadLocalRandom.current().nextInt(RANDOM_NUM_BOUND);
        //组合返回
        return String.format("%04d", randomNumber)+timeStamp;
    }
}
