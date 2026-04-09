package pro.shushi.pamirs.eip.core.init;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pro.shushi.pamirs.boot.common.api.command.AppLifecycleCommand;
import pro.shushi.pamirs.boot.common.api.init.InstallDataInit;
import pro.shushi.pamirs.boot.common.api.init.UpgradeDataInit;
import pro.shushi.pamirs.eip.api.EipModule;
import pro.shushi.pamirs.eip.api.model.EipConnGroup;
import pro.shushi.pamirs.eip.api.model.alarm.EipAlarmRule;
import pro.shushi.pamirs.eip.core.manager.EipAlarmNotifyManager;
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
        // 只在安装时初始化一次,允许用户删除
        new EipConnGroup().createOrUpdateBatch(
                Lists.newArrayList(
                        new EipConnGroup().setName("OA"),
                        new EipConnGroup().setName("ERP"),
                        new EipConnGroup().setName("物流仓储"),
                        new EipConnGroup().setName("生产制造"),
                        new EipConnGroup().setName("商业交易"),
                        new EipConnGroup().setName("工具"),
                        new EipConnGroup().setName("其他")
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
        template.setTitle("【接口告警】${interfaceName} 在 ${timeWindow} 内 ${alarmDesc}");
        template.setModel(EipAlarmRule.MODEL_MODEL);

        String bodyFilePath = "classpath:templates/" + EipAlarmNotifyManager.EIP_ALARM_EMAIL_TEMPLATE + ".html";
        try {
            String body = FileUtils.read(bodyFilePath);
            template.setBody(body);
        } catch (Exception e) {
            throw new RuntimeException("读取邮件模板文件失败: " + bodyFilePath, e);
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
