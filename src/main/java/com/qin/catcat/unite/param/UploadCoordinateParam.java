package com.qin.catcat.unite.param;

import lombok.Data;

@Data
public class UploadCoordinateParam {
    /* 
     * 猫猫id
     */
    private Integer catId;

    /* 
     * 经度
     */
    private Double latitude;

    /* 
     * 纬度
     */ 
    private Double longitude;

    /* 
     * 上传者昵称
     */
    private String uploader;
}
