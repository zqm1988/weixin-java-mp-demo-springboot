package com.github.binarywang.demo.wx.mp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import com.alibaba.fastjson.JSONObject;

@AllArgsConstructor
@Controller
@RequestMapping("/wx/oauth/{appid}")
public class WxAuthController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final WxMpService wxService;
	
	@Value("${server.host}")
	String host;
	
	@RequestMapping("/getBaseOauthUrl")
	protected String handleGetBaseOauthUrl(@PathVariable String appid,
			@RequestParam String redirectUrl) {
		if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
		
		String url = wxService.oauth2buildAuthorizationUrl(redirectUrl, "snsapi_base", "");
		return "redirect:"+url;
	}
	
	@RequestMapping("/getInfoOauthUrl")
	protected String handleGetInfoOauthUrl(@PathVariable String appid,
			@RequestParam String redirectUrl) {
		if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
		
		String url = wxService.oauth2buildAuthorizationUrl(redirectUrl, "snsapi_userinfo", "");
		return "redirect:"+url;
	}
	
	@RequestMapping("/thirdPartyGetOpenID")
	protected String handleThirdPartyGetOpenID(@PathVariable String appid) {
		if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
		String redirectUrl = String.format("%s/wx/redirect/%s/%s", host, appid, "getOpenID4ThirdParty");
		String url = wxService.oauth2buildAuthorizationUrl(redirectUrl, "snsapi_base", "");
		return "redirect:"+url;
	}
	
	@PostMapping("/getOpenID")
    protected String handleGetOpenID(@PathVariable String appid, @RequestParam String code) {
    	logger.debug(String.format("getOpenID: appid=%s", appid));
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        
        try {
            WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
            logger.debug(String.format("getOpenID: openid=%s", accessToken.getOpenId()));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", accessToken.getOpenId());
            jsonObject.put("user", accessToken.toString());
            return jsonObject.toJSONString();
        } catch (WxErrorException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
    }
	
	@PostMapping("/getOpenIDWithRedirectUrl")
	protected String handleGetOpenIDWithRedirectUrl(@PathVariable String appid, @RequestParam String code, @RequestParam String url) {
		logger.debug(String.format("getOpenID: appid=%s, redirectUrl=%s", appid, url));
        if (!this.wxService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }
        
        try {
            WxMpOAuth2AccessToken accessToken = wxService.oauth2getAccessToken(code);
            String decodedUrl = URLDecoder.decode(url, "UTF-8");
            String resUrl = String.format("%s?openId=%s", decodedUrl, accessToken.getOpenId());
            return "redirect:"+resUrl;
        } catch (WxErrorException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return e.getLocalizedMessage();
        }
	}
}