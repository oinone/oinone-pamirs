package pro.shushi.pamirs.eip.api;

public interface IEipPaging<T> extends IEipAfterProperty<IEipPaging<T>> {

    /**
     * 每页数据行（默认为：2000）
     */
    Integer getPageSize();

    /**
     * 起始页数（默认为：0）
     */
    Integer getStartPage();

    /**
     * 结束页数（默认为：0）
     */
    Integer getEndPage();

    /**
     * 分页处理器
     */
    IEipPagingProcessor<T> getProcessor();

    /**
     * 分页判定
     */
    IEipPagingPredict<T> getPredict();
}
