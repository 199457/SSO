package com.wei.ssoserver.controller;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.sso.SaSsoHandle;
import cn.dev33.satoken.sso.SaSsoUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.ejlchina.okhttps.OkHttps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sa-Token-SSO Server端 Controller
 */
@RestController
public class SsoServerController {

    /*
     * SSO-Server端：处理所有SSO相关请求
     */
    @RequestMapping("/sso/*")
    public Object ssoRequest() {
        return SaSsoHandle.serverRequest();
    }

    /**
     * 配置SSO相关参数
     */
    @Autowired
    private void configSso(SaTokenConfig cfg) {
        // 配置：未登录时返回的View 
        cfg.sso.setNotLoginView(() -> "当前会话在SSO-Server端尚未登录，请先访问"
                + "<a href='/sso/doLogin?name=sa&pwd=123456' target='_blank'> doLogin登录 </a>"
                + "进行登录之后，刷新页面开始授权");

        // 配置：登录处理函数 
        cfg.sso.setDoLoginHandle((name, pwd) -> {
            // 此处仅做模拟登录，真实环境应该查询数据进行登录 
            if ("sa".equals(name) && "123456".equals(pwd)) {
                StpUtil.login(10001);
                return SaResult.ok("登录成功！").setData(StpUtil.getTokenValue());
            }
            return SaResult.error("登录失败！");
        });

        // 配置 Http 请求处理器 （在模式三的单点注销功能下用到，如不需要可以注释掉） 
        cfg.sso.setSendHttp(url -> OkHttps.sync(url).get().getBody().toString());
    }

    // 自定义接口：获取userinfo
    @RequestMapping("/sso/userinfo")
    public Object userinfo(String loginId, String secretkey) {
        System.out.println("---------------- 获取userinfo --------");

        // 校验调用秘钥
        SaSsoUtil.checkSecretkey(secretkey);

        // 自定义返回结果（模拟）
        return SaResult.ok()
                .set("id", loginId)
                .set("name", "lingua")
                .set("sex", "女")
                .set("age", 18);
    }

}