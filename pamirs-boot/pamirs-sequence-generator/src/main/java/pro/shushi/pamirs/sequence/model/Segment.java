package pro.shushi.pamirs.sequence.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Segment
 *
 * @author yakir on 2020/04/08 16:17.
 */
public class Segment {

    private AtomicLong value = new AtomicLong(0);
    private SegmentBuffer buffer;

    private volatile long max;
    private volatile int step;

    public Segment(SegmentBuffer buffer) {
        this.buffer = buffer;
    }

    public AtomicLong getValue() {
        return value;
    }

    public Segment setValue(AtomicLong value) {
        this.value = value;
        return this;
    }

    public long getMax() {
        return max;
    }

    public Segment setMax(long max) {
        this.max = max;
        return this;
    }

    public int getStep() {
        return step;
    }

    public Segment setStep(int step) {
        this.step = step;
        return this;
    }

    public SegmentBuffer getBuffer() {
        return buffer;
    }

    public long getIdle() {
        return this.getMax() - this.getValue().get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Segment(");
        sb.append("value:");
        sb.append(value);
        sb.append(",max:");
        sb.append(max);
        sb.append(",step:");
        sb.append(step);
        sb.append(")");
        return sb.toString();
    }

}