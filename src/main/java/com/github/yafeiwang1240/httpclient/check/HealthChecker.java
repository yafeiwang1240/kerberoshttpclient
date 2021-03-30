package com.github.yafeiwang1240.httpclient.check;

public interface HealthChecker<T> {

    boolean skip(T t);

    boolean check(T t);

    /**
     * 健康检查需要依据当前的状态选择不同的算法
     * @param t
     * @param currentStatus
     * @return true alive | false dead
     */
    boolean checkAliveOrDead(T t, boolean currentStatus);
}
