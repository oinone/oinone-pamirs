package pro.shushi.pamirs.middleware.schedule.common;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class Query<T> extends QueryBase implements Serializable {

    private static final long serialVersionUID = 1930382256159908170L;

    private T data;
    private String orderBy;

    public Query() {
    }

    public T getData() {
        return this.data;
    }

    public Query<T> setData(T data) {
        this.data = data;
        return this;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public Query<T> setOrderBy(String orderBy) {
        if (StringUtils.isNotBlank(orderBy)) {
            this.orderBy = orderBy;
        }
        return this;
    }
}
