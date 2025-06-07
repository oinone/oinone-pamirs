package pro.shushi.pamirs.boot.base.resource;

import pro.shushi.pamirs.boot.base.enmu.FileTypeEnum;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.MetaSimulator;
import pro.shushi.pamirs.meta.base.common.CodeModel;
import pro.shushi.pamirs.meta.enmu.MimeTypeEnum;

@MetaSimulator(onlyBasicTypeField = false, preCreateTable = false)
@Model.model(PamirsFile.MODEL_MODEL)
@Model.Advanced(name = "pamirsFile")
@Model(displayName = "文件", labelFields = "name")
@Model.Code(sequence = "UUID")
public class PamirsFile extends CodeModel {

    private static final long serialVersionUID = 1793373770519782798L;

    public static final String MODEL_MODEL = "base.PamirsFile";

    @Field.String(size = 512)
    @Field(index = true, displayName = "名称")
    private String name;

    @Field.Text
    @Field(required = true, displayName = "路径")
    private String url;

    @Field.Enum
    @Field(required = true, displayName = "类型")
    private FileTypeEnum type;

    @Field.Integer
    @Field(displayName = "大小")
    private Long size;

    @Field.Enum
    @Field(displayName = "MIME")
    private MimeTypeEnum mime;

    @Field.Boolean
    @Field(displayName = "是否公开")
    private Boolean isPublic;

    @Field.Integer
    @Field(displayName = "资源ID")
    private Long resId;

    @Field.String
    @Field(displayName = "资源类型")
    private String resType;

  /*  @Field.many2one
    @Field.Relation(relationFields = "icon",referenceFields = "code")
    @Field(displayName = "图片文件",store = NullableBoolEnum.FALSE)
    private PamirsIcon pamirsIcon;

    @Field.String(size = 1024)
    @Field(displayName = "ICON")
    private String icon;//换个名字*/


}
