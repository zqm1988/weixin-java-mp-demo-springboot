package com.github.binarywang.demo.wx.mp.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.binarywang.demo.wx.mp.config.WxMpConfiguration;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Action;

/**
 * @author Edward
 */
@AllArgsConstructor
@RestController
@RequestMapping("/wx/redirect/{appid}")
public class WxRedirectController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WxMpService wxService;
    
    @RequestMapping("/greetBase")
    public String greetUserBase(@PathVariable String appid, @RequestParam String code) {
    	logger.debug(String.format("greetBase:code=%s", code));
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        
        try {
            WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
            logger.debug(String.format("greetBase:openid=%s", accessToken.getOpenId()));
            return accessToken.getOpenId();
        } catch (WxErrorException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }

    @RequestMapping("/greetUserInfo")
    public String greetUserInfo(@PathVariable String appid, @RequestParam String code, ModelMap map) {
    	logger.debug(String.format("greetUserInfo:code=%s", code));
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        try {
            WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
            WxMpUser user = wxService.oauth2getUserInfo(accessToken, null);
            map.put("user", user);
            logger.debug(String.format("greetUserInfo:openid=%s", user.getOpenId()));
            return user.getOpenId();
        } catch (WxErrorException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }
    
    @RequestMapping("/getOpenID4ThirdParty")
    protected String handleGetOpenID4ThirdParty(@PathVariable String appid, @RequestParam String code) {
    	logger.debug(String.format("getOpenID4ThirdParty, code=%s", code));
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        try {
            WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
            logger.debug(String.format("getOpenID4ThirdParty: openid=%s", accessToken.getOpenId()));
            JSONObject json = new JSONObject();
            json.put("openID", accessToken.getOpenId());
            return json.toJSONString();
        } catch (WxErrorException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }
    
    @RequestMapping("/echoCode")
    public String echoCode(@PathVariable String appid, @RequestParam String code) {
    	logger.debug(String.format("echoCode:code=%s", code));
    	return code;
    }
    
    @RequestMapping("/echoOpenId")
    public String echoOpenId(@PathVariable String appid, @RequestParam String openId) {
    	logger.debug(String.format("accepted openId:%s", openId));
    	return openId;
    }
}
