package pro.shushi.pamirs.sequence.domain;

import java.io.Serializable;

/**
 * SegmentBuffer 双buffer
 *
 * @author yakir on 2020/04/08 16:17.
 */
public class SegmentBufferView implements Serializable {

    private static final long serialVersionUID = -2898812351912031331L;

    private String  key;
    private long    value0;
    private int     step0;
    private long    max0;
    private boolean threadRunning;

    private long    value1;
    private int     step1;
    private long    max1;
    private int     pos;
    private boolean nextReady;
    private boolean initOk;


    public String getKey() {
        return key;
    }

    public SegmentBufferView setKey(String key) {
        this.key = key;
        return this;
    }

    public long getValue0() {
        return value0;
    }

    public SegmentBufferView setValue0(long value0) {
        this.value0 = value0;
        return this;
    }

    public int getStep0() {
        return step0;
    }

    public SegmentBufferView setStep0(int step0) {
        this.step0 = step0;
        return this;
    }

    public long getMax0() {
        return max0;
    }

    public SegmentBufferView setMax0(long max0) {
        this.max0 = max0;
        return this;
    }

    public boolean getThreadRunning() {
        return threadRunning;
    }

    public SegmentBufferView setThreadRunning(boolean threadRunning) {
        this.threadRunning = threadRunning;
        return this;
    }

    public long getValue1() {
        return value1;
    }

    public SegmentBufferView setValue1(long value1) {
        this.value1 = value1;
        return this;
    }

    public int getStep1() {
        return step1;
    }

    public SegmentBufferView setStep1(int step1) {
        this.step1 = step1;
        return this;
    }

    public long getMax1() {
        return max1;
    }

    public SegmentBufferView setMax1(long max1) {
        this.max1 = max1;
        return this;
    }

    public int getPos() {
        return pos;
    }

    public SegmentBufferView setPos(int pos) {
        this.pos = pos;
        return this;
    }

    public boolean isNextReady() {
        return nextReady;
    }

    public SegmentBufferView setNextReady(boolean nextReady) {
        this.nextReady = nextReady;
        return this;
    }

    public boolean isInitOk() {
        return initOk;
    }

    public SegmentBufferView setInitOk(boolean initOk) {
        this.initOk = initOk;
        return this;
    }
}
