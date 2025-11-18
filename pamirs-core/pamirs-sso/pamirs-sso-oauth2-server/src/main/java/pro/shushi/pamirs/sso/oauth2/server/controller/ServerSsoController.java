package pro.shushi.pamirs.sso.oauth2.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pro.shushi.pamirs.sso.api.service.SsoCommonService;
import pro.shushi.pamirs.sso.api.utils.Result;

import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/pamirs/sso")
public class ServerSsoController {
    @Autowired
    private SsoCommonService ssoCommonService;


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
}
