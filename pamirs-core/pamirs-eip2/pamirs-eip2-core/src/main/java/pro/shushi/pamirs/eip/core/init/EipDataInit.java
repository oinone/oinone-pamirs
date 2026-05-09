package pro.shushi.pamirs.eip.core.init;

import com.google.common.collect.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.eip.core.manager.EipAlarmNotifyManager;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.annotation.fun.extern.Slf4j;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * EipDataInit
 *
 * @author yakir on 2023/03/30 10:46.
 */
@Slf4j
@Component
public class EipDataInit implements InstallDataInit, UpgradeDataInit {

    private void initConnGroup() {
        // Initialize only once during installation, allowing user deletion
        new EipConnGroup().createOrUpdateBatch(
                Lists.newArrayList(
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.OA")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.ERP")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Logistics")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Manufacturing")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Commerce")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Tools")),
                        new EipConnGroup().setName(I18nUtils.getMessage("pamirs.eip.connGroup.name.Others"))
                )
        );
    }

    @Override
    @Transactional
    public boolean init(AppLifecycleCommand command, String version) {
        initConnGroup();
        initAlarmEmailTemplate();
        return true;
    }

    @Override
    public boolean upgrade(AppLifecycleCommand command, String version, String existVersion) {
        initAlarmEmailTemplate();
        return true;
    }

    private void initAlarmEmailTemplate() {
        EmailTemplate template = new EmailTemplate();
        template.setName(EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE);
        template.setTitle(I18nUtils.getMessage("pamirs.eip.alarm.email.template.title"));
        template.setModel(EipAlarmRule.MODEL_MODEL);
        String path;
        if (I18nUtils.isZh()) {
            path = "templates/zh/" + EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE + ".html";
        } else {
            path = "templates/en/" + EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE + ".html";
        }
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream is = resource.getInputStream()) {
            String body = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
            template.setBody(body);
        } catch (Exception e) {
            log.error(I18nUtils.getMessage("pamirs.eip.alarm.email.template.read.error", path), e);
        }
        template.createOrUpdate();
    }

    @Override
    public List<String> modules() {
        return Collections.singletonList(EipModule.MODULE_MODULE);
    }

    @Override
    public int priority() {
        return 0;
    }
}