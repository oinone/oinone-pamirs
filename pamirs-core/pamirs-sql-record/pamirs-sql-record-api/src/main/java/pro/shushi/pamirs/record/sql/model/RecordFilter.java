package pro.shushi.pamirs.record.sql.model;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.IdModel;
import pro.shushi.pamirs.record.sql.enmu.FilterType;

/**
 * RecordFilter
 *
 * @author yakir on 2023/06/28 18:15.
 */
@Base
@Model(displayName = "过滤设置")
@Model.model(RecordFilter.MODEL_MODEL)
@Model.Advanced(unique = "filterType,filter")
public class RecordFilter extends IdModel {

    private static final long serialVersionUID = -3011945797396861190L;

    public final static String MODEL_MODEL = "record.RecordFilter";

    @Field(displayName = "过滤类型")
    @Field.Enum
    private FilterType filterType;

    @Field(displayName = "过滤表达式")
    @Field.String(size = 128)
    private String filter;

}
