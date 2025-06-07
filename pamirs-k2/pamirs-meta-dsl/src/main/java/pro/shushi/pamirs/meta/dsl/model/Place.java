package pro.shushi.pamirs.meta.dsl.model;

import pro.shushi.pamirs.meta.dsl.exception.MachineException;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Place {

    public static final int DEFAULT_NONE_VERSION = -1;

    public static Map<String, LinkedList<Process>> processes = new ConcurrentHashMap<String, LinkedList<Process>>();

    public static Process getProcess(String name) {
        LinkedList<Process> group = processes.get(name);
        if (null == group) {
            throw new MachineException("process not found with name :" + name);
        }
        return group.peekFirst();
    }

    public static int getProcessMaxVersion(String name) {
        return getProcess(name).getVersion();
    }

    public static Process getProcessWithVersion(String name, int version) {

        if (DEFAULT_NONE_VERSION == version) {
            return getProcess(name);
        }

        LinkedList<Process> group = processes.get(name);
        Process p = null;
        if (null == group) {
            throw new MachineException("process not found with name :" + name);
        }
        for (Process process : group) {
            if (process.getVersion() == version) {
                p = process;
                break;
            }
        }
        // if version can't match any one, return the biggest.
        if (p == null) {
            p = getProcess(name);
        }
        return p;
    }

    /**
     * not sorted except the first Process
     *
     * @param p
     */
    public static void putProcess(Process p) {
        //fixme 缺少版本号支持，put的时候清理
//		LinkedList<Process> group = processes.get(p.name);
//		if (null == group) {
//			group = new LinkedList<Process>();
//			processes.put(p.name, group);
//		}

        LinkedList<Process> group = new LinkedList<Process>();
        processes.put(p.getName(), group);
        Process first = group.peekFirst();
        if (null != first && first.getVersion() > p.getVersion()) {
            group.addLast(p);
        } else {
            group.addFirst(p);
        }
    }
}
