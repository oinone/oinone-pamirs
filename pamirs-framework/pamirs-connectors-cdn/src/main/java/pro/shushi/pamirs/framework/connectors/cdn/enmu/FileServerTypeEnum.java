package pro.shushi.pamirs.framework.connectors.cdn.enmu;

import pro.shushi.pamirs.meta.annotation.Dict;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.enmu.IEnum;

@Base
@Dict(dictionary = "file.FileServerTypeEnum", displayName = "文件服务器类型")
public enum FileServerTypeEnum implements IEnum<String> {

    OSS("OSS", "阿里云OSS", "阿里云OSS"),
    COS("COS", "腾讯云COS", "腾讯云COS"),
    MINIO("MINIO", "本地", "本地"),
    UPYUN("UPYUN", "又拍云OSS", "又拍云OSS"),
    HUAWEI_OBS("HUAWEI_OBS", "华为云OBS", "华为云OBS"),
    TENCENT_COS("TENCENT_COS", "腾讯COS", "腾讯COS"),
    ;

    private String help;

    private String value;

    private String displayName;

    FileServerTypeEnum(String value, String displayName, String help) {
        this.help = help;
        this.value = value;
        this.displayName = displayName;
    }


    public String getKey() {
        return help;
    }


    public String getValue() {
        return value;
    }


    public String getDisplayName() {
        return displayName;
    }
}
