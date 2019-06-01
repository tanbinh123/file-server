package com.github.qinyou.fileserver.controller;


import com.github.qinyou.fileserver.bean.ImgUploadRet;
import com.github.qinyou.fileserver.interceptor.SecurityInterceptor;
import com.github.qinyou.fileserver.service.UploadService;
import com.jfinal.aop.Before;
import com.jfinal.core.ActionKey;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.upload.UploadFile;
import net.coobird.thumbnailator.geometry.Positions;

import java.io.File;

/**
 * 图片上传，缩放、裁剪、水印等
 *
 * @author chuang
 */
@Before(SecurityInterceptor.class)
public class ImageUploadController extends BaseController {

    private static final Res res = I18n.use(lang);

    /**
     * 等比例 生成缩略图
     * 图片类型需要调用方自己控制
     */
    @ActionKey("/image/upload/resize")
    public void resize() {
        int width = getParaToInt("width", 100); // 默认缩放 到 宽 100
        UploadFile uploadFile = getFile("file");
        String basePath = get("basePath");

        Ret ret = UploadService.imgUploadResize(uploadFile, basePath, width);
        if (ret.isOk()) {
            renderImgResponse(ret);
        } else {
            renderRet(ret);
        }
    }


    /**
     * 图片加水印
     * 图片类型需要调用方自己控制
     */
    @ActionKey("/image/upload/watermark")
    public void watermark() {
        UploadFile uploadFile = getFile("file");
        String basePath = get("basePath");

        String watermark = get("watermark"); // 水印图片名
        String position = get("position", "center");  // 位置
        String tp = get("tp", "0.25"); // 透明度

        String waterImgPath;
        if (StrKit.isBlank(watermark)) {
            waterImgPath = PathKit.getRootClassPath() + "/watermark/" + watermark + ".png";
        } else {
            waterImgPath = PathKit.getRootClassPath() + "/watermark/duck.png";
        }
        if (!new File(waterImgPath).exists()) {
            renderFail(res.get("WATERMARK_NOT_EXIST"));
            return;
        }

        Positions positions;
        switch (position) {
            case "center":
                positions = Positions.CENTER;
                break;
            case "top_left":
                positions = Positions.TOP_LEFT;
                break;
            case "top_right":
                positions = Positions.TOP_RIGHT;
                break;
            case "bottom_right":
                positions = Positions.BOTTOM_RIGHT;
                break;
            case "bottom_left":
                positions = Positions.BOTTOM_LEFT;
                break;
            default:
                positions = Positions.TOP_CENTER;
                break;
        }

        Ret ret = UploadService.imgUploadWatermark(uploadFile, basePath, watermark, waterImgPath, positions, Float.parseFloat(tp));
        if (ret.isOk()) {
            renderImgResponse(ret);
        } else {
            renderRet(ret);
        }
    }

    // 图片上传响应 参数加工
    private void renderImgResponse(Ret ret) {
        ImgUploadRet imgUploadRet = (ImgUploadRet) ret.get("data");
        String contextPath = getContextPath();
        imgUploadRet.setOriUri(contextPath + imgUploadRet.getOriPath());
        imgUploadRet.setNewUri(contextPath + imgUploadRet.getNewPath());
        renderOk(imgUploadRet);
    }
}
