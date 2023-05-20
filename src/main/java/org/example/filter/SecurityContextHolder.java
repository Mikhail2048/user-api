package org.example.filter;

/**
 *  Yes, I just do not want to include spring security in here to
 *  save some time to disable autoconfiguration for it e.t.c
 */
public class SecurityContextHolder {

    private static final ThreadLocal<Long> userIdThreadLocal = new ThreadLocal<>();

    public static void setUserId(Long userId) {
        SecurityContextHolder.userIdThreadLocal.set(userId);
    }

    public static Long getUserId() {
        return SecurityContextHolder.userIdThreadLocal.get();
    }
}