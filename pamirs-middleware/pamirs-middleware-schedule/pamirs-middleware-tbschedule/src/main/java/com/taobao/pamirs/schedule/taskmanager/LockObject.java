package com.taobao.pamirs.schedule.taskmanager;

class LockObject {

    private int m_threadCount = 0;
    private Object m_waitOnObject = new Object();

    public LockObject() {
    }

    public void waitCurrentThread() throws Exception {
        synchronized (m_waitOnObject) {
            // System.out.println(Thread.currentThread().getName() + ":" +
            // "休眠当前线程");
            this.m_waitOnObject.wait();
        }
    }

    public void notifyOtherThread() throws Exception {
        synchronized (m_waitOnObject) {
            // System.out.println(Thread.currentThread().getName() + ":" +
            // "唤醒所有等待线程");
            this.m_waitOnObject.notifyAll();
        }
    }

    public void addThread() {
        synchronized (this) {
            m_threadCount = m_threadCount + 1;
        }
    }

    public void realseThread() {
        synchronized (this) {
            m_threadCount = m_threadCount - 1;
        }
    }

    /**
     * 降低线程数量，如果是最后一个线程，则不能休眠
     *
     * @return boolean
     */
    public boolean realseThreadButNotLast() {
        synchronized (this) {
            if (this.m_threadCount == 1) {
                return false;
            } else {
                m_threadCount = m_threadCount - 1;
                return true;
            }
        }
    }

    public int count() {
        synchronized (this) {
            return m_threadCount;
        }
    }
}
