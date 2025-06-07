package pro.shushi.pamirs.meta.api.session;

import pro.shushi.pamirs.meta.domain.fun.Hook;

import java.util.concurrent.ConcurrentLinkedQueue;

public class HookConcurrentLinkedQueue {
    public static ConcurrentLinkedQueue<Hook> queue = new ConcurrentLinkedQueue<>();
}
