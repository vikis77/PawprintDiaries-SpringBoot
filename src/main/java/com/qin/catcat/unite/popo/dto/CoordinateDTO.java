package com.qin.catcat.unite.popo.dto;

import java.sql.Timestamp;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CoordinateDTO {
    /**
     * 区域
     */
    private String area;
    /**
     * 猫名列表
     */
    private List<String> catNames;
    /**
     * 描述
     */
    private String description;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 上传者
     */
    // private String uploader;

}
