package pro.shushi.pamirs.sso.server.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;
import pro.shushi.pamirs.sso.api.service.SsoCommonService;

@Slf4j
@RestController
@RequestMapping("/pamirs/sso/apply/trans")
public class ServerSsoApplicationTransferController {

    @Autowired
    private SsoCommonService ssoCommonService;

    @GetMapping("/transfer")
    public void transfer(
            @RequestParam("client_id") String clientId) {
        ssoCommonService.ssoServerTransfer(clientId);
    }
}
