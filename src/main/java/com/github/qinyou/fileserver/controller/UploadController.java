package com.github.qinyou.fileserver.controller;


import com.github.qinyou.fileserver.bean.ComUploadRet;
import com.github.qinyou.fileserver.interceptor.SecurityInterceptor;
import com.github.qinyou.fileserver.service.UploadService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.kit.Ret;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;

/**
 * 文件上传下载
 *
 * @author chuang
 */
@Before(SecurityInterceptor.class)
@Slf4j
public class UploadController extends BaseController {
    /**
     * 单文件上传
     */
    @ActionKey("/common/upload")
    public void upload(){
        UploadFile uploadFile = getFile("file");
        String basePath = get("basePath");
        Ret ret = UploadService.upload(uploadFile, basePath);
        if(ret.isOk()){
            ComUploadRet uploadRet = (ComUploadRet) ret.get("data");
            uploadRet.setUri(getContextPath()+uploadRet.getPath());
        }
        renderRet(ret);
    }
}
