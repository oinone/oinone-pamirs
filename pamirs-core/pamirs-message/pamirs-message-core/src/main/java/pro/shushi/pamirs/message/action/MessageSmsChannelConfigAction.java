package pro.shushi.pamirs.message.action;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import pro.shushi.pamirs.framework.connectors.data.sql.Pops;
import pro.shushi.pamirs.message.enmu.MessageEngineTypeEnum;
import pro.shushi.pamirs.message.model.SmsChannelConfig;
import pro.shushi.pamirs.meta.annotation.Action;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.validation.Validation;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.api.core.orm.WriteWithFieldApi;
import pro.shushi.pamirs.meta.common.exception.PamirsException;

import jakarta.annotation.Resource;

import static pro.shushi.pamirs.message.enmu.MessageExpEnumerate.BIZ_ERROR;

/**
 * @author shier
 * date  2022/6/28 下午4:59
 */
@Model.model(SmsChannelConfig.MODEL_MODEL)
@Component
public class MessageSmsChannelConfigAction {

    @Resource
    private WriteWithFieldApi defaultWriteWithFieldApi;

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.signName)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.accessKeyId)", error = "Access Key不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.accessKeySecret)", error = "Access Secret不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.endpoint)", error = "发送渠道Endpoint不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.regionId)", error = "API支持的RegionID不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.timeZone)", error = "时区不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.signatureMethod)", error = "签名方式不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.signatureVersion)", error = "签名算法版本不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.version)", error = "API的版本号版本不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.channel)", error = "短信通道不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.action)", error = "发送形式不能为空")
    })
    @Action(displayName = "创建")
    public SmsChannelConfig create(SmsChannelConfig data) {
        data.setType(MessageEngineTypeEnum.SMS_SEND);
        if (StringUtils.isNotBlank(data.getSignName()) && StringUtils.isNotBlank(data.getAccessKeyId()) && data.getChannel() != null
                && Models.origin().count(Pops.<SmsChannelConfig>lambdaQuery()
                .from(SmsChannelConfig.MODEL_MODEL)
                .eq(SmsChannelConfig::getChannel, data.getChannel())
                .eq(SmsChannelConfig::getSignName, data.getSignName())
                .eq(SmsChannelConfig::getAccessKeyId, data.getAccessKeyId())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg("短信通道配置已经存在，不能重复").errThrow();
        }
        return defaultWriteWithFieldApi.createWithField(data);
    }

    @Validation(ruleWithTips = {
            @Validation.Rule(value = "!IS_BLANK(data.signName)", error = "名称不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.accessKeyId)", error = "Access Key不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.accessKeySecret)", error = "Access Secret不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.endpoint)", error = "发送渠道Endpoint不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.regionId)", error = "API支持的RegionID不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.timeZone)", error = "时区不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.signatureMethod)", error = "签名方式不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.signatureVersion)", error = "签名算法版本不能为空"),
            @Validation.Rule(value = "!IS_BLANK(data.version)", error = "API的版本号版本不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.channel)", error = "短信通道不能为空"),
            @Validation.Rule(value = "!IS_NULL(data.action)", error = "发送形式不能为空")
    })
    @Action(displayName = "更新")
    public SmsChannelConfig update(SmsChannelConfig data) {
        data.setType(MessageEngineTypeEnum.SMS_SEND);
        if (StringUtils.isNotBlank(data.getSignName()) && StringUtils.isNotBlank(data.getAccessKeyId()) && data.getChannel() != null
                && Models.origin().count(Pops.<SmsChannelConfig>lambdaQuery()
                .from(SmsChannelConfig.MODEL_MODEL)
                .eq(SmsChannelConfig::getChannel, data.getChannel())
                .ne(SmsChannelConfig::getId, data.getId())
                .eq(SmsChannelConfig::getSignName, data.getSignName())
                .eq(SmsChannelConfig::getAccessKeyId, data.getAccessKeyId())) > 0) {
            throw PamirsException.construct(BIZ_ERROR).appendMsg("短信通道配置已经存在，不能重复").errThrow();
        }
        return defaultWriteWithFieldApi.updateWithField(data);
    }

}
