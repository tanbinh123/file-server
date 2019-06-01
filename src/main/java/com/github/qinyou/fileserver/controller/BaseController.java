package com.github.qinyou.fileserver.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.Ret;

public class BaseController extends Controller {
    public final static String lang = PropKit.use("config.txt").get("lang");


    /**
     * 获得上下文路径
     * @return
     */
    String getContextPath(){
        StringBuffer url = getRequest().getRequestURL();
        return url.delete(url.length() - getRequest().getRequestURI().length(), url.length())
                .append(getRequest().getServletContext().getContextPath()).append("/").toString();
    }
    void renderRet(Ret ret) {
        ret.set("timestamp", System.currentTimeMillis());
        renderJson(ret);
    }

    void renderFail(String msg) {
        Ret ret = Ret.create().setFail().set("msg", msg).set("timestamp", System.currentTimeMillis());
        renderJson(ret);
    }

    void renderOk(Object data) {
        Ret ret = Ret.create().setOk().set("data", data).set("timestamp", System.currentTimeMillis());
        renderJson(ret);
    }
}
