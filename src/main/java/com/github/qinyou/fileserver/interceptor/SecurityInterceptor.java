package com.github.qinyou.fileserver.interceptor;

import com.github.qinyou.fileserver.controller.BaseController;
import com.github.qinyou.fileserver.utils.FileKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.i18n.I18n;
import com.jfinal.i18n.Res;
import com.jfinal.kit.*;
import com.jfinal.upload.UploadFile;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 服务授权验证
 * token生成方式: MD5(MD5(secret + timestamp)+timestamp)
 *
 * @author chuang
 */
@Slf4j
public class SecurityInterceptor implements Interceptor {

    private static final String secret; // 认证密文
    private static final Long maxIdle;   // 超时最大数(毫秒)
    private static final Res res;

    static {
        Prop prop = PropKit.use("config.txt");
        secret = prop.get("secret");
        maxIdle = prop.getInt("max-idle") * 3600 * 1000L;
        res = I18n.use(BaseController.lang);
    }


    @Override
    public void intercept(Invocation invocation) {
        UploadFile uploadFile = invocation.getController().getFile();
        HttpServletRequest req = invocation.getController().getRequest();

        // 认证参数不为空
        String authToken = req.getParameter("token");
        if (StrKit.isBlank(authToken)) {
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(res.get("TOKEN_EMPTY")));
            return;
        }

        // 时间戳不可为空
        String timestampStr = req.getParameter("timestamp");
        if (StrKit.isBlank(timestampStr)) {
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(res.get("TIMESTAMP_EMPTY")));
            return;
        }

        // 时间戳格式错误
        Long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (NumberFormatException e) {
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(res.get("TIMESTAMP_NOT_LONG")));
            return;
        }

        // 服务器时间戳 - 客户端时间戳 > max-idle 认为 authToken 超时
        Long serverTimeStamp = System.currentTimeMillis();
        if (serverTimeStamp - timestamp > maxIdle) {
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(res.get("TOKEN_TIMEOUT")));
            return;
        }

        String realAuthToken = HashKit.md5(HashKit.md5(secret + timestamp) + timestamp);
        if (!authToken.equals(realAuthToken)) {
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(res.get("TOKEN_ERROR")));
            return;
        }

        // 访问 controller
        try {
            invocation.invoke();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            FileKit.deleteFile(uploadFile.getFile());
            invocation.getController().renderJson(buildFail(e.toString()));
        }
    }

    /**
     * 生成 错误提示信息
     *
     * @param msg
     * @return
     */
    private Ret buildFail(String msg) {
        return Ret.create().setFail().set("msg", msg).set("timestamp", System.currentTimeMillis());
    }
}
