package pro.shushi.pamirs.sso.server.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import pro.shushi.pamirs.sso.api.config.PamirsSsoProperties;
import pro.shushi.pamirs.sso.api.service.SsoCommonService;
import pro.shushi.pamirs.sso.api.service.SsoOauth2TokenService;
import pro.shushi.pamirs.sso.common.dto.Result;
import pro.shushi.pamirs.sso.common.dto.SsoUserVo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@RestController
@RequestMapping("/pamirs/sso")
public class ServerSsoController {

    private static final Logger log = LoggerFactory.getLogger(ServerSsoController.class);

    @Autowired
    private SsoCommonService ssoCommonService;
    @Autowired
    private SsoOauth2TokenService ssoOauth2TokenService;
    @Autowired
    private PamirsSsoProperties pamirsSsoProperties;

    @GetMapping("/auth")
    public void auth(
            @RequestParam("client_id") String clientId,
            @RequestParam(value = "redirect_uri") String redirectUri,
            @RequestParam(value = "state") String state) {
        ssoCommonService.checkAuth(clientId, redirectUri, state);
    }

    // 生成对称密钥
    @GetMapping("/getKey")
    public Result<String> getPrivateKey(@RequestParam("username") String username) throws NoSuchAlgorithmException {
        return Result.success(ssoCommonService.getKey(username));
    }

    @RequestMapping("/login")
    public void login(SsoUserVo ssoUserVo) {
        try {
            ssoOauth2TokenService.login(ssoUserVo);
        } catch (Exception e) {
            log.error("login error", e);
            String errorMessage;
            String redirectUri = ssoUserVo.getRedirectUri();
            try {
                errorMessage = StringUtils.isBlank(e.getMessage()) ? URLEncoder.encode("未知异常", "UTF-8") : URLEncoder.encode(e.getMessage(), "UTF-8");
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

}
