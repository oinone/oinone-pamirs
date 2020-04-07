package pro.shushi.pamirs.meta.api.dto.crud;

import org.apache.commons.lang3.StringUtils;
import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.common.constants.CharacterConstants;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

/**
 * 分页条件
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.PageCondition")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "分页条件", summary = "分页条件")
public class PageCondition<T> extends QueryCondition<T> {

    private static final long serialVersionUID = -4905152863811585304L;

    @Base
    @Field(displayName = "聚合")
    private String aggs;

    @Base
    @Field(displayName = "页数")
    private Integer page;

    @Base
    @Field(displayName = "大小")
    private Integer size;

    @Base
    @Field.many2one
    @Field(displayName = "排序")
    private Sort sort;

    @Base
    @Field(displayName = "分组条件")
    private String groupBy;

    public PageCondition(Class<T> modelClazz) {
        super(modelClazz);
        constructObject();
    }

    public PageCondition(String model) {
        super(model);
        this.setModel(model);
        constructObject();
    }

    private void constructObject(){
        this.setAggs(CharacterConstants.SEPARATOR_EMPTY);
        this.setPage(0);
        this.setSize(Page.defaultSize);
        this.setSort(null);
        this.setGroupBy(CharacterConstants.SEPARATOR_EMPTY);
    }

    public PageCondition setAggs(String aggs) {
        if(StringUtils.isNotBlank(aggs)){
            this._d.put("aggs", aggs);
            this.setSize(0);
        }
        return this;
    }

    public PageCondition setPage(Integer page) {
        if(null == page){
            page = 1;
        }
        this._d.put("page", page);
        return this;
    }

    public PageCondition setSize(Integer size) {
        if(null == size){
            size = Page.defaultSize;
        }
        this._d.put("size", size);
        return this;
    }

}
