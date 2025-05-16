package pro.shushi.pamirs.eip.api.entity.openapi;

/**
 * @author Adamancy Zhang at 14:09 on 2021-02-24
 */
public class PaginationResponseBody<T> extends SimpleResponseBody<T> {

    private int pageIndex;

    private long pageSize;

    private long total;

    public PaginationResponseBody() {
    }

    public PaginationResponseBody(Boolean success, int errorCode, String errorMsg, T data) {
        super(success, errorCode, errorMsg, data);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public PaginationResponseBody<T> setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public long getPageSize() {
        return pageSize;
    }

    public PaginationResponseBody<T> setPageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public PaginationResponseBody<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public static <T> PaginationResponseBody<T> error(Integer errorCode, String errorMsg) {
        return new PaginationResponseBody<>(false, errorCode, errorMsg, null);
    }

    public static <T> PaginationResponseBody<T> success() {
        return new PaginationResponseBody<>(true, SimpleResponseBody.DEFAULT_SUCCESS_CODE, SimpleResponseBody.DEFAULT_SUCCESS_MESSAGE, null);
    }

    public static <T> PaginationResponseBody<T> success(T data) {
        return new PaginationResponseBody<>(true, SimpleResponseBody.DEFAULT_SUCCESS_CODE, SimpleResponseBody.DEFAULT_SUCCESS_MESSAGE, data);
    }
}
