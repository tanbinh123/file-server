package com.github.qinyou.fileserver.controller;

import com.github.qinyou.fileserver.Constant;
import com.github.qinyou.fileserver.interceptor.UploadInterceptor;
import com.github.qinyou.fileserver.model.SingleRet;
import com.github.qinyou.fileserver.service.FileService;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

/**
 * 文件上传下载
 *
 * @author chuang
 */
@Slf4j
public class FileController extends BaseController {

    /**
     * 单文件上传
     */
    @Before(UploadInterceptor.class)
    public void upload() throws IOException {
        UploadFile uploadFile = getFile("file");

        if (uploadFile == null) {
            renderFail(Constant.PARAM_FILE_EMPTY);
            return;
        }

        String originalFileName = uploadFile.getOriginalFileName();
        String extension = FilenameUtils.getExtension(originalFileName);

        // 文件类型非法
        if (!FileService.checkFileType(extension)) {
            FileService.deleteFile(uploadFile.getFile());
            renderFail(extension + Constant.FILE_TYPE_NOT_LIMIT);
            return;
        }

        // 文件保存
        String relativePath = FileService.fileRelativeSavePath(extension);
        File saveFile = new File(PathKit.getWebRootPath() + "/" + relativePath);
        if (saveFile.exists()) {
            // 应该 不会发生
            FileService.deleteFile(uploadFile.getFile());
            renderFail(originalFileName + Constant.FILE_EXIST);
            return;
        }

        FileUtils.copyFile(uploadFile.getFile(), saveFile);
        FileService.deleteFile(uploadFile.getFile());

        SingleRet singleRet = new SingleRet();
        singleRet.setName(originalFileName);
        singleRet.setPath(relativePath);
        long sizeL = saveFile.length();
        singleRet.setSizeL(sizeL);
        singleRet.setSize(FileUtils.byteCountToDisplaySize(sizeL));
        StringBuffer url = getRequest().getRequestURL();
        String uri = url.delete(url.length() - getRequest().getRequestURI().length(), url.length()).append(getRequest().getServletContext().getContextPath()).append("/").toString();
        singleRet.setUri(uri + relativePath);

        renderOk(singleRet);
    }

    /**
     * 文件下载
     */
    public void download(){
        String path = getPara("path");
        String name = getPara("name");
        if(StrKit.isBlank(path)){
            renderFail(Constant.PARAM_PATH_EMPTY);
            return;
        }
        path = path.replace("$","/");
        path = PathKit.getWebRootPath()+"/"+path;
        File downloadFile = new File(path);
        if(!downloadFile.exists()){
            renderFail(Constant.DOWNLOAD_FILE_NOT_EXIST);
            return;
        }
        if(StrKit.isBlank(name)){
            renderFile(downloadFile);
        }else{
            String fileName = name.contains(".")?name:name+"."+FilenameUtils.getExtension( downloadFile.getName());
            renderFile(downloadFile,fileName);
        }
    }
}
