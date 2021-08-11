package rest.controller.plugin.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * manager thread/submit task
 */
public class ThreadManager {
    private static final ThreadPoolExecutor reqPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors() * 2, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
        new NamedThreadFactory("request-thread"));

    private static final ThreadPoolExecutor downloadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(),
        Runtime.getRuntime().availableProcessors(), 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
        new NamedThreadFactory("download-thread"));


    private static final ThreadPoolExecutor persistPool = new ThreadPoolExecutor(1,
        1, 5, TimeUnit.SECONDS, new LinkedBlockingQueue<>(),
        new NamedThreadFactory("persist-thread"));
    {
        downloadPool.allowCoreThreadTimeOut(true);
    }
    private ThreadManager() {
    }

    public static void main(String[] args) {
        System.out.println(Runtime.getRuntime().availableProcessors());
    }

    public static ExecutorService requestPool() {
        return reqPool;
    }

    public static ExecutorService downloadPool() {
        return downloadPool;
    }

    public static ExecutorService persistPool() {
        return persistPool;
    }
    public static void close() {
        reqPool.shutdownNow();
        downloadPool.shutdownNow();
        persistPool.shutdownNow();
    }

    static class NamedThreadFactory implements ThreadFactory {
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        NamedThreadFactory(String name) {

            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            if (null == name || name.isEmpty()) {
                name = "pool";
            }

            namePrefix = name + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r, namePrefix + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }
}
