package pro.shushi.pamirs.record.sql.pojo;

import java.io.Serializable;

/**
 * ReadResult
 *
 * @author yakir on 2023/06/30 12:05.
 */
public class ReadResult implements Serializable {

    private static final long serialVersionUID = 9137610930184291769L;

    private long position;

    private String line;

    private ReadResult(){

    }

    public static ReadResult empty(){
        return new ReadResult();
    }

    public ReadResult(long position, String line) {
        this.position = position;
        this.line     = line;
    }

    public long getPosition() {
        return position;
    }

    public ReadResult setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getLine() {
        return line;
    }

    public ReadResult setLine(String line) {
        this.line = line;
        return this;
    }

    @Override
    public String toString() {
        return "ReadResult{" +
                "position=" + position +
                ", line='" + line + '\'' +
                '}';
    }
}
