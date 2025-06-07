package pro.shushi.pamirs.meta.dsl.signal;

import pro.shushi.pamirs.meta.dsl.utils.UniqId;

public class Slot {

    private Object listener;
    private Boolean sync = true;
    private String method;
    private String args;
    private String result;

    public Slot(Object listener, Boolean sync, String method, String args,
                String result) {
        super();
        this.listener = listener;
        this.sync = sync;
        this.method = method;
        this.args = args;
        this.result = result;
    }

    public Object getListener() {
        return listener;
    }

    public void setListener(Object listener) {
        this.listener = listener;
    }

    public Boolean getSync() {
        return sync;
    }

    public void setSync(Boolean sync) {
        this.sync = sync;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String toDescription() {
        return listener.getClass().getName() + "#" + method + "#" + args;
    }

    public String toMD5Description() {
        return UniqId.getInstance().hashString(toDescription());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((args == null) ? 0 : args.hashCode());
        result = prime * result
                + ((listener == null) ? 0 : listener.hashCode());
        result = prime * result + ((method == null) ? 0 : method.hashCode());
        result = prime * result
                + ((this.result == null) ? 0 : this.result.hashCode());
        result = prime * result + ((sync == null) ? 0 : sync.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Slot other = (Slot) obj;
        if (args == null) {
            if (other.args != null)
                return false;
        } else if (!args.equals(other.args))
            return false;
        if (listener == null) {
            if (other.listener != null)
                return false;
        } else if (!listener.equals(other.listener))
            return false;
        if (method == null) {
            if (other.method != null)
                return false;
        } else if (!method.equals(other.method))
            return false;
        if (result == null) {
            if (other.result != null)
                return false;
        } else if (!result.equals(other.result))
            return false;
        if (sync == null) {
            if (other.sync != null)
                return false;
        } else if (!sync.equals(other.sync))
            return false;
        return true;
    }
}
