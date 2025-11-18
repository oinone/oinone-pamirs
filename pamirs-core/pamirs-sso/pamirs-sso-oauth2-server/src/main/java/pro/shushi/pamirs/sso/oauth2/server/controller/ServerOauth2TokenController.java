package pro.shushi.pamirs.sso.oauth2.server.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.dto.SsoRequestParameters;
import pro.shushi.pamirs.sso.api.dto.SsoUserVo;
import pro.shushi.pamirs.sso.api.service.SsoOauth2TokenService;
import pro.shushi.pamirs.sso.api.tmodel.ApiCommonTransient;
import pro.shushi.pamirs.sso.api.utils.Result;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/pamirs/sso/oauth2")
public class ServerOauth2TokenController {
    @Autowired
    private SsoOauth2TokenService ssoOauth2TokenService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @RequestMapping("/authorize")
    public Result authorize(@RequestBody SsoRequestParameters ssoRequestParameters) {
        try {
            return Result.success(ssoOauth2TokenService.authorize(ssoRequestParameters));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @RequestMapping("/login")
    public void login(SsoUserVo ssoUserVo) {
        try {
            ssoOauth2TokenService.login(ssoUserVo);
        } catch (Exception e) {
            String errorMessage;
            String redirectUri = ssoUserVo.getRedirectUri();
            try {
                errorMessage = URLEncoder.encode(e.getMessage(), "UTF-8");
                redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                errorMessage = "encoding_failed";
            }
            //抓所有报错，重定向到登录页面
            StringBuilder urlBuilder = new StringBuilder(pamirsSsoProperties.getServer().getLoginUrl());
            urlBuilder.append(";client_id=").append(ssoUserVo.getClientId())
                    .append(";redirect_uri=").append(redirectUri)
                    .append(";error=").append(errorMessage);
            if (StringUtils.isNotEmpty(ssoUserVo.getState())) {
                urlBuilder.append(";state=").append(ssoUserVo.getState());
            }
            String url = urlBuilder.toString();
            HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(
                    RequestContextHolder.getRequestAttributes())).getResponse();
            try {
                Objects.requireNonNull(response).sendRedirect(url);
            } catch (Exception ignored) {
            }
        }

    }


    @RequestMapping("/refresh")
    public Result refresh(SsoRequestParameters ssoRequestParameters) {
        try {
            return Result.success(ssoOauth2TokenService.refresh(ssoRequestParameters));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/getUserInfo")
    public Result getUserInfo(@RequestBody SsoRequestParameters ssoRequestParameters) {
        try {
            return Result.success(ssoOauth2TokenService.getUserInfo(ssoRequestParameters.getClient_id()));
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }


    @PostMapping("/logout")
    public void logout(@RequestBody Map<String, Object> map) {
        ssoOauth2TokenService.logout(map);
    }

}
