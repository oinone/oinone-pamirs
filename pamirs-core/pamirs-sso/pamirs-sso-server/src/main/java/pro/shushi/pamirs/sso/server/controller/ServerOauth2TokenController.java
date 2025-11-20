package pro.shushi.pamirs.sso.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.service.SsoOauth2TokenService;
import pro.shushi.pamirs.sso.common.dto.Result;
import pro.shushi.pamirs.sso.common.dto.SsoRequestParameter;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Objects;

@RestController
@RequestMapping("/pamirs/sso/oauth2")
public class ServerOauth2TokenController {

    @Autowired
    private SsoOauth2TokenService ssoOauth2TokenService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @RequestMapping("/authorize")
    public Result authorize(@RequestBody SsoRequestParameter ssoRequestParameter) {
        try {
            return Result.success(ssoOauth2TokenService.authorize(ssoRequestParameter));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/refresh")
    public Result refresh(SsoRequestParameter ssoRequestParameter) {
        try {
            return Result.success(ssoOauth2TokenService.refresh(ssoRequestParameter));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/getUserInfo")
    public Result getUserInfo(@RequestBody SsoRequestParameter ssoRequestParameter) {
        try {
            return Result.success(ssoOauth2TokenService.getUserInfo(ssoRequestParameter.getClient_id()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public void logout(@RequestBody SsoRequestParameter ssoRequestParameter) {
        ssoOauth2TokenService.logout(ssoRequestParameter);
    }

}
