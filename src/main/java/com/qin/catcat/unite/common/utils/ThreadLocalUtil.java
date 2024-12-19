package com.qin.catcat.unite.common.utils;

/**
 * @Description ThreadLocal工具类.
 *
 * @Author liuyun
 * @Version 1.0
 * @Since 2024-12-19 21:28
 */
public class ThreadLocalUtil {
    private static final ThreadLocal<Integer> totalPagesHolder = new ThreadLocal<>();

    public static void setTotalPages(Integer totalPages) {
        totalPagesHolder.set(totalPages);
    }

    public static Integer getTotalPages() {
        return totalPagesHolder.get();
    }

    public static void clearTotalPages() {
        totalPagesHolder.remove();
    }
}
