package pro.shushi.pamirs.middleware.schedule.common;

import java.io.Serializable;
import java.util.List;

/**
 * 分页
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = -8363089242517342665L;

    private List<T> list;
    private Integer pageNo;
    private Integer pageSize;
    private Integer totalPage;
    private Integer total;

    public List<T> getList() {
        return list;
    }

    public Page<T> setList(List<T> list) {
        this.list = list;
        return this;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public Page<T> setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public Page<T> setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public Page<T> setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
        return this;
    }

    public Integer getTotal() {
        return total;
    }

    public Page<T> setTotal(Integer total) {
        this.total = total;
        int totalPage = total / pageSize;
        if (totalPage == 0 || total % pageSize != 0) {
            totalPage++;
        }
        this.totalPage = totalPage;
        return this;
    }
}
