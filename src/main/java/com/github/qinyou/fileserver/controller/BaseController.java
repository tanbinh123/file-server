package com.github.qinyou.fileserver.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;

public class BaseController extends Controller {

    public void renderFail(String msg) {
        Ret ret = Ret.create().setFail().set("msg", msg).set("timestamp", System.currentTimeMillis());
        renderJson(ret);
    }

    public void renderOk(Object data) {
        Ret ret = Ret.create().setOk().set("data", data).set("timestamp", System.currentTimeMillis());
        renderJson(ret);
    }
}
