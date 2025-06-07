package pro.shushi.pamirs.message.model;

import pro.shushi.pamirs.message.enmu.SMSActionEnum;
import pro.shushi.pamirs.message.enmu.SMSChannelEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;

/**
 * SMSChannelConfig
 *
 * @author yakir on 2019/08/22 11:32.
 */
@Model.Advanced(name = "SmsChannelConfig", unique = {"channel", "signName", "accessKeyId"})
@Model(displayName = "短信通道配置")
@Model.model(SmsChannelConfig.MODEL_MODEL)
public class SmsChannelConfig extends MessageSource {

    public static final String MODEL_MODEL = "pamirs.message.SmsChannelConfig";

    @Field.Enum
    @Field(displayName = "短信通道")
    private SMSChannelEnum channel;

    @Field.String
    @Field(displayName = "短信签名名称")
    private String signName; // 数式科技

    @Field.String
    @Field(displayName = "主账号AccessKey的ID")
    private String accessKeyId;

    @Field.String
    @Field(displayName = "主账号AccessKey的密钥")
    private String accessKeySecret;

    @Field.Enum
    @Field(displayName = "SMSAction")
    private SMSActionEnum action;

    @Field.String
    @Field(displayName = "发送渠道Endpoint")
    private String endpoint; // http[s]://dysmsapi.aliyuncs.com

    @Field.String
    @Field(displayName = "API支持的RegionID")
    private String regionId; // cn-hangzhou

    @Field.String
    @Field(displayName = "时区")
    private String timeZone; // GMT

    @Field.String
    @Field(displayName = "签名方式")
    private String signatureMethod; // HMAC-SHA1

    @Field.String
    @Field(displayName = "签名算法版本")
    private String signatureVersion; // 1.0

    @Field.String
    @Field(displayName = "API的版本号")
    private String version; // 2017-05-25
}
