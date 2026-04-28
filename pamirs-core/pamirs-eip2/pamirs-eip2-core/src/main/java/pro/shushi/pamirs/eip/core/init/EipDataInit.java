package pro.shushi.pamirs.eip.core.init;

import com.google.common.collect.Lists;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.eip.core.manager.EipAlarmNotifyManager;
import pro.shushi.pamirs.locale.utils.I18nUtils;
import pro.shushi.pamirs.message.model.EmailTemplate;
import pro.shushi.pamirs.meta.common.util.FileUtils;

import java.util.Collections;
import java.util.List;

/**
 * EipDataInit
 *
 * @author yakir on 2023/03/30 10:46.
 */
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

        String lang = I18nUtils.getLocale().getLanguage();
        String path = "templates/" + lang + "/" + EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE + ".html";
        try {
            ClassPathResource resource = new ClassPathResource(path);
            String body = null;
            if (!resource.exists() && !"zh".equals(lang)) {
                String bodyFilePath = "templates/zh" + EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE + ".html";
                body = FileUtils.read("classpath:" + bodyFilePath);
            }

            if (resource.exists()) {
                body = FileUtils.read("classpath:" + path);
            }

            template.setBody(body);
        } catch (Exception e) {
            throw new RuntimeException(I18nUtils.getMessage("pamirs.eip.alarm.email.template.read.error", path), e);
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
