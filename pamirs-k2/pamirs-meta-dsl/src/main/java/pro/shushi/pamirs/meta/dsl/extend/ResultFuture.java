package pro.shushi.pamirs.meta.dsl.extend;

public interface ResultFuture {

    public Object get();

    public Object get(long timeout);
}
