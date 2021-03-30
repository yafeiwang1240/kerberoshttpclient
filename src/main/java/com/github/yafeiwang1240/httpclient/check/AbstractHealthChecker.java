package com.github.yafeiwang1240.httpclient.check;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 健康检查
 * @author wangyafei
 * @date 2021-03-30
 */
public abstract class AbstractHealthChecker implements HealthChecker<InstanceInfo> {

    private static final long IGNORE_TIME = 60 * 1000; // 60秒

    /**
     * 记录上次停止命令时间，用来判断在收到停止命令后 {@value #IGNORE_TIME} 时间之内忽视主动心跳
     */
    private ConcurrentMap<InstanceInfo, Long> stopTimeMap = new ConcurrentHashMap<InstanceInfo, Long>();

    private int retryCount = 2;

    private int failedCount = 2;

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    @Override
    public boolean checkAliveOrDead(InstanceInfo instanceInfo, boolean currentStatus) {
        if (skip(instanceInfo)) {
            return currentStatus;
        }
        int retry = currentStatus ? failedCount : retryCount;
        boolean result = false;
        while (retry-- > 0) {
            if (!((result = check(instanceInfo)) ^ currentStatus)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        return result;
    }

    @Override
    public boolean skip(InstanceInfo t) {
        Long stopTime = stopTimeMap.get(t);
        if (stopTime == null) {
            // 没有收到停止命名,不忽略
            return false;
        }
        long time = System.currentTimeMillis();
        if (time >= stopTime.longValue()) {
            if ((time - stopTime.longValue()) <= IGNORE_TIME) {
                // 如果在忽略时间范围内,忽略
                return true;
            } else {
                stopTimeMap.remove(t);
            }
        }
        return false;
    }

    /**
     * 接到停止规则
     * @param t
     */
    public void suspend(InstanceInfo t) {
        stopTimeMap.put(t, System.currentTimeMillis());
    }

    /**
     * 被动接收心跳
     * @param t
     * @param time
     */
    public void receiveHeartbeat(InstanceInfo t, long time) {
        // 实现类自己实现
    }
}
