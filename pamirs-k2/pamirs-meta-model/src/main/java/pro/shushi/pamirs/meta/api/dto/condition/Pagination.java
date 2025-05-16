package pro.shushi.pamirs.meta.api.dto.condition;

import pro.shushi.pamirs.meta.annotation.Field;
import pro.shushi.pamirs.meta.annotation.Model;
import pro.shushi.pamirs.meta.annotation.sys.Base;
import pro.shushi.pamirs.meta.api.Models;
import pro.shushi.pamirs.meta.base.TransientModel;
import pro.shushi.pamirs.meta.common.lambda.Getter;
import pro.shushi.pamirs.meta.enmu.ModelTypeEnum;
import pro.shushi.pamirs.meta.enmu.SortDirectionEnum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static pro.shushi.pamirs.meta.api.dto.condition.Pagination.MODEL_MODEL;

/**
 * 分页
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
@Base
@Model.model(MODEL_MODEL)
@Model.Advanced(type = ModelTypeEnum.TRANSIENT, unInheritedFunctions = "construct")
@Model(displayName = "分页", summary = "分页")
public class Pagination<T> extends TransientModel {

    public final static String MODEL_MODEL = "base.Pagination";

    private static final long serialVersionUID = 1870981950551938351L;

    public static final String CONTENT = "content";

    public final static long defaultSize = 5000L;

    @Base
    @Field(displayName = "聚合")
    private String aggs;

    @Base
    @Field(displayName = "当前页", defaultValue = "1")
    private Integer currentPage;

    @Base
    @Field(displayName = "每页展示数据数", defaultValue = "5000")
    private Long size;

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
    private List<T> content = new ArrayList<>();

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

    @Base
    @Field(displayName = "是否需要排序", summary = "是否需要排序,默认排序")
    private Boolean sortable = Boolean.TRUE;

    private boolean searchCount = Boolean.TRUE;

    private boolean hitCount = Boolean.FALSE;

    private boolean optimizeCountSql = Boolean.TRUE;

    public Pagination() {
        super();
        this.setCurrentPage(1);
        this.setSize(defaultSize);
        this.setStart(0L);
        this.setTotalElements(0L);
        this.setTotalPages(0);
        this.setNeedPage(Boolean.TRUE);
        this.setSortable(Boolean.TRUE);
    }

    public Pagination(Boolean sortable) {
        super();
        this.setCurrentPage(1);
        this.setSize(defaultSize);
        this.setStart(0L);
        this.setTotalElements(0L);
        this.setTotalPages(0);
        this.setNeedPage(Boolean.TRUE);
        this.setSortable(sortable);
    }

    /**
     * 有参的构造方法
     *
     * @param currentPage 当前页
     * @param pageSize    分页数
     */
    public Pagination(int currentPage, long pageSize) {
        super();
        if (currentPage <= 0) {
            this.setCurrentPage(1);
        }
        this.setCurrentPage(currentPage);
        this.setSize(pageSize);
        this.setNeedPage(Boolean.TRUE);
        this.setSortable(Boolean.TRUE);

        //开始记录索引
        this.setStart((currentPage - 1) * size);

        this.setTotalElements(0L);
        this.setTotalPages(0);
    }

    public Pagination(int currentPage, long pageSize, Sort sort, String groupBy) {
        super();
        if (currentPage <= 0) {
            currentPage = 1;
        }
        this.setCurrentPage(currentPage);
        this.setSize(pageSize);
        this.setSort(sort);
        this.setGroupBy(groupBy);
        this.setNeedPage(Boolean.TRUE);
        this.setSortable(Boolean.TRUE);

        //开始记录索引
        this.setStart((currentPage - 1) * pageSize);

        this.setTotalElements(0L);
        this.setTotalPages(0);
    }

    public List<Order> orders() {
        if (null == this.getSort()) {
            return null;
        }
        return this.getSort().getOrders();
    }

    //上一页
    @SuppressWarnings("unused")
    public int getPrePageNum() {
        this.setPrePageNum(this.getCurrentPage() - 1);
        if (null == this._d.get("prePageNum") || (Integer) this._d.get("prePageNum") < 1) {
            this.setPrePageNum(1);
        }
        return (Integer) this._d.get("prePageNum");
    }

    //下一页
    @SuppressWarnings("unused")
    public int getNextPageNum() {
        this.setNextPageNum(this.getCurrentPage() + 1);
        if (null == this._d.get("nextPageNum") || ((int) this._d.get("nextPageNum")) > this.getTotalPages()) {
            this.setNextPageNum(this.getTotalPages());
        }
        return (Integer) this._d.get("nextPageNum");
    }

    @SuppressWarnings("unused")
    public Pagination<T> orderBy(SortDirectionEnum direction, String field) {
        if (null == getSort()) {
            setSort(new Sort());
        }
        getSort().addOrder(direction, field);
        return this;
    }

    public <G, R> Pagination<T> orderBy(SortDirectionEnum direction, Getter<G, R> getter) {
        if (null == getSort()) {
            setSort(new Sort());
        }
        getSort().addOrder(direction, getter);
        return this;
    }

    public Pagination<T> setCurrentPage(Integer currentPage) {
        if (null == currentPage || currentPage <= 0) {
            this._d.put("currentPage", 1);
        }
        this._d.put("currentPage", currentPage);
        return this;
    }

    public void setTotalElements(Long totalElements) {
        if (null == totalElements) {
            totalElements = 0L;
        }
        if (this.getSize() <= 0) {
            this.setTotalPages(0);
        } else {
            this.setTotalPages(Long.valueOf(totalElements % this.getSize() == 0 ? totalElements / this.getSize() : totalElements / this.getSize() + 1).intValue());
        }
        this._d.put("totalElements", totalElements);
    }

    public long getStart() {
        if (this.getCurrentPage() <= 0) {
            this.setCurrentPage(1);
        }
        this.setStart((this.getCurrentPage() - 1) * this.getSize());
        return start;
    }

    @Override
    public String toString() {
        return "limit " + getStart() + "," + getSize();
    }


    public boolean isSearchCount() {
        return searchCount;
    }

    @SuppressWarnings("unused")
    public void setSearchCount(boolean searchCount) {
        this.searchCount = searchCount;
    }

    public boolean isHitCount() {
        return hitCount;
    }

    @SuppressWarnings("unused")
    public void setHitCount(boolean hitCount) {
        this.hitCount = hitCount;
    }

    public boolean isOptimizeCountSql() {
        return optimizeCountSql;
    }

    @SuppressWarnings("unused")
    public void setOptimizeCountSql(boolean optimizeCountSql) {
        this.optimizeCountSql = optimizeCountSql;
    }

    public Pagination<T> setModel(String model) {
        Models.api().setDataModel(model, this);
        return this;
    }

    public String getModel() {
        return Models.api().getDataModel(this);
    }

    @SuppressWarnings({"rawtypes"})
    public Pagination to(Pagination result) {
        Integer currentPage = this.getCurrentPage();
        if (currentPage != null) {
            result.setCurrentPage(currentPage);
        }
        Long size = this.getSize();
        if (size != null) {
            result.setSize(size);
        }
        Long totalElements = this.getTotalElements();
        if (totalElements != null) {
            result.setTotalElements(totalElements);
        }
        Integer totalPages = this.getTotalPages();
        if (totalPages != null) {
            result.setTotalPages(totalPages);
        }
        String groupBy = this.getGroupBy();
        if (groupBy != null) {
            result.setGroupBy(groupBy);
        }
        Sort sort = this.getSort();
        if (sort != null) {
            result.setSort(sort);
        }
        sortable = this.getSortable();
        if (sortable != null) {
            result.setSortable(sortable);
        }
        result.setSearchCount(this.isSearchCount());
        result.setHitCount(this.isHitCount());
        computePagination(result);
        return result;
    }

    public void computePagination(Pagination<?> result) {
        Long totalElements = result.getTotalElements();
        if (totalElements == null || totalElements == 0) {
            long newSize = Optional.ofNullable(result.getContent())
                    .map(List::size)
                    .map(Long::valueOf)
                    .orElse(0L);
            result.setTotalElements(newSize);
        }
    }

    public Pagination<T> setContent(List<T> content) {
        this.content = content;
        this.get_d().put(CONTENT, content);
        return this;
    }

}
