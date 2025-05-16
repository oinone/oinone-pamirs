package pro.shushi.pamirs.sequence.common;

import java.io.Serializable;

/**
 * Result
 *
 * @author yakir on 2020/04/08 16:17.
 */
public class Result implements Serializable {

    private static final long serialVersionUID = 6116237472535355822L;

    private Long   id;
    private Status status;

    public Result() {
    }

    public Result(Long id, Status status) {
        this.id = id;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Result setId(Long id) {
        this.id = id;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public Result setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Override
    public String toString() {
        return "Result{" +
                "id=" + id +
                ", status=" + status +
                '}';
    }
}
