package pro.shushi.pamirs.meta.api.dto.meta;

/**
 * 执行上下文
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2020/3/2 11:51 上午
 */
public class ExecuteContext {

    private boolean success = Boolean.TRUE;

    // 用于标识串行业务调用是否终止
    private boolean broken = Boolean.FALSE;

    public boolean isSuccess() {
        return success;
    }

    public ExecuteContext setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public boolean isBroken() {
        return broken;
    }

    @SuppressWarnings("unused")
    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    @SuppressWarnings("unused")
    public ExecuteContext broken() {
        this.broken = Boolean.TRUE;
        return this;
    }

    public ExecuteContext error() {
        this.setSuccess(Boolean.FALSE);
        return this;
    }

}
