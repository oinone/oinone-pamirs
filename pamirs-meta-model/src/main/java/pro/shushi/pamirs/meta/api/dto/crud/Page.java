package pro.shushi.pamirs.meta.api.dto.crud;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;

import java.util.List;
import java.util.Optional;

/**
 * 分页
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 *
 */
@Base
@Model.model("base.Page")
@Model.Advanced(type = ModelTypeEnum.TRANSIENT)
@Model(displayName = "分页", summary = "分页")
public class Page<T> extends TransientModel {

    private static final long serialVersionUID = 1870981950551938351L;

    public final static int defaultSize = 500;

    @Base
    @Field(displayName = "当前页", defaultValue = "1")
    private Integer currentPage;

    @Base
    @Field(displayName = "每页展示数据数", defaultValue = "500")
    private Integer size;

    @Base
    @Field(displayName = "总记录数", defaultValue = "0", invisible = true)
    private Long totalElements;

    private Long start;

    @Base
    @Field(displayName = "总页数", defaultValue = "0", invisible = true)
    private Integer totalPages;

    @Base
    @Field(displayName = "上一页", invisible = true)
    private Integer prePageNum;

    @Base
    @Field(displayName = "下一页", invisible = true)
    private Integer nextPageNum;

    //当前页的数据
    private List<T> content;

    @Base
    @Field.many2one
    @Field(displayName = "排序")
    private Sort sort;

    @Base
    @Field(displayName = "分组条件")
    private String groupBy;

    @Base
    @Field(displayName = "是否需要分页")
    private Boolean needPage = Boolean.TRUE;

    private String model;

    private boolean isSearchCount = Boolean.TRUE;

    private boolean optimizeCountSql = Boolean.FALSE;

    public Page(){
        super();
        this.setCurrentPage(1);
        this.setSize(defaultSize);
        this.setStart(0L);
        this.setTotalElements(0L);
        this.setTotalPages(0);
        this.setNeedPage(Boolean.TRUE);
    }

    /**
     * 有参的构造方法
     * @param currentPage	当前页
     * @param total		总记录数
     */
    public Page(int currentPage, Long total) {
        super();
        if(currentPage <= 0){
            this.setCurrentPage(1);
        }
        this.setCurrentPage(currentPage);
        this.setSize(defaultSize);
        this.setTotalElements(total);
        this.setNeedPage(Boolean.TRUE);

        //开始记录索引
        this.setStart((currentPage-1)*this.getSize().longValue());

        if(this.getSize()==0){
            this.setTotalPages(0);
        }else{
            //总页数
            this.setTotalPages(Long.valueOf(total%this.getSize()==0?total/this.getSize():total/this.getSize()+1).intValue());
        }

    }

    public Page(int currentPage, int pageSize, Sort sort, String groupBy) {
        super();
        if(currentPage <= 0){
            currentPage = 1;
        }
        this.setCurrentPage(currentPage);
        this.setSize(pageSize);
        this.setTotalElements(Optional.ofNullable(this.getTotalElements()).orElse(0L));
        this.setSort(sort);
        this.setGroupBy(groupBy);
        this.setNeedPage(Boolean.TRUE);

        //开始记录索引
        this.setStart((currentPage-1)* (long) pageSize);

        //总页数
        if (pageSize == 0) {
            this.setTotalPages(0);
        }else{
            this.setTotalPages(Long.valueOf(this.getTotalElements()%pageSize==0?this.getTotalElements()/pageSize:this.getTotalElements()/pageSize+1).intValue());
        }
    }

    public List<Order> orders(){
        if(null == this.getSort()){
            return null;
        }
        return this.getSort().getOrders();
    }

    //上一页
    @SuppressWarnings("unused")
    public int getPrePageNum() {
        this.setPrePageNum(this.getCurrentPage() - 1);
        if(null == this._d.get("prePageNum") || (Integer)this._d.get("prePageNum") < 1) {
            this.setPrePageNum(1);
        }
        return (Integer)this._d.get("prePageNum");
    }

    //下一页
    @SuppressWarnings("unused")
    public int getNextPageNum() {
        this.setNextPageNum(this.getCurrentPage() + 1);
        if(null == this._d.get("nextPageNum") || ((int)this._d.get("nextPageNum")) > this.getTotalPages()) {
            this.setNextPageNum(this.getTotalPages());
        }
        return (Integer)this._d.get("nextPageNum");
    }

    public void setCurrentPage(Integer currentPage) {
        if(null == currentPage || currentPage <= 0){
            this._d.put("currentPage", 1);
        }
        this._d.put("currentPage", currentPage);
    }

    public void setTotalElements(Long totalElements) {
        if(null == totalElements){
            totalElements = 0L;
        }
        if(this.getSize()==0){
            this.setTotalPages(0);
        }else {
            this.setTotalPages(Long.valueOf(totalElements%this.getSize()==0?totalElements/this.getSize():totalElements/this.getSize()+1).intValue());
        }
        this._d.put("totalElements", totalElements);
    }

    public long getStart() {
        if(this.getCurrentPage() <= 0){
            this.setCurrentPage(1);
        }
        this.setStart((this.getCurrentPage()-1)*this.getSize().longValue());
        return (Long)this._d.get("start");
    }

    @Override
    public String toString(){
        return "limit " + getStart() + "," + getSize();
    }

    public boolean isSearchCount() {
        return isSearchCount;
    }

    @SuppressWarnings("unused")
    public void setSearchCount(boolean searchCount) {
        isSearchCount = searchCount;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    @SuppressWarnings("unused")
    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }
}
