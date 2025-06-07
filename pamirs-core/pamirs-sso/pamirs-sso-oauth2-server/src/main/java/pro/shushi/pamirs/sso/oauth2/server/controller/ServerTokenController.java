package pro.shushi.pamirs.sso.oauth2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.service.SsoTokenService;
import pro.shushi.pamirs.sso.api.tmodel.ApiCommonTransient;
import pro.shushi.pamirs.sso.api.utils.OAuthTokenResponse;
import pro.shushi.pamirs.sso.api.utils.Result;
import pro.shushi.pamirs.sso.oauth2.server.model.SsoOauth2ClientDetailsService;

import java.security.NoSuchAlgorithmException;
import java.util.Map;

@RestController
@RequestMapping("/pamirs/sso")
public class ServerTokenController {
    @Autowired
    private SsoTokenService ssoTokenService;
    @Autowired
    private SsoOauth2ClientDetailsService ssoOauth2ClientDetailsService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @GetMapping("/getPrivateKey")
    public Result getPrivateKey(@RequestParam("username") String username) throws NoSuchAlgorithmException {
        return ssoTokenService.getPrivateKey(username);
    }


    @RequestMapping("/authorize")
    public OAuthTokenResponse authorize(SsoRequestParameters ssoRequestParameters) {
        return ssoTokenService.authorize(ssoRequestParameters);
    }

    @RequestMapping("/login")
    public void login(SsoUserVo ssoUserVo) {
        ssoTokenService.login(ssoUserVo);
    }


    @RequestMapping("/refresh")
    public OAuthTokenResponse refresh(SsoRequestParameters ssoRequestParameters) {
        return ssoTokenService.refresh(ssoRequestParameters);
    }

    @PostMapping("/userinfo")
    public ApiCommonTransient getUserInfo(@RequestBody Map<String, Object> map) {
        return ssoTokenService.getUserInfo(map);
    }


    @PostMapping("/logout")
    public void logout(@RequestBody Map<String, Object> map) {
        ssoTokenService.logout(map);
    }

}
