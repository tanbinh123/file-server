package com.github.qinyou.fileserver.bean;

import lombok.Data;

@Data
public class ImgUploadRet {
    private String name;  // 文件原名
    private String ope;   // 操作类型
    private String oriPath; // 原图路径
    private String newPath; // 新图路径
    private String oriUri;  // 原图 uri
    private String newUri;  // 新图 uri
}
