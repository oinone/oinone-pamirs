package pro.shushi.pamirs.meta.domain;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.IdModel;


/**
 * PlatformVersion
 *
 * @author yakir on 2024/07/22 14:03.
 */
@Base
@MetaSimulator(onlyBasicTypeField = false)
@Model.Advanced(table = PlatformVersion.TABLE_NAME, unique = {"jar"})
@Model.model(PlatformVersion.MODEL_MODEL)
@Model(displayName = "平台版本", summary = "平台版本")
public class PlatformVersion extends IdModel {

    private static final long serialVersionUID = -3829946152303799286L;

    public final static String MODEL_MODEL = "base.PlatformVersion";

    public static final String TABLE_NAME = "base_platform_version";

    @Field(displayName = "Jar名称")
    @Field.String
    private String jar;

    @Field(displayName = "版本")
    @Field.String
    private String version;

    public String sign() {
        return this.getJar() + "#" + this.getVersion();
    }
}
