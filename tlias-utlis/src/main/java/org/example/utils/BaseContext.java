package org.example.utils;

/**
 * 基于 ThreadLocal 的用户上下文工具类
 * <p>
 * 在请求进入时由拦截器存入当前用户信息，
 * 在同一线程内的任意位置（Controller、Service、AOP等）均可直接获取，
 * 请求结束后由拦截器清理，防止内存泄漏。
 * </p>
 *
 * <pre>
 * // 获取当前用户ID
 * Integer currentId = BaseContext.getCurrentId();
 *
 * // 获取当前用户名
 * String currentUsername = BaseContext.getCurrentUsername();
 * </pre>
 */
public class BaseContext {

    private static final ThreadLocal<Integer> currentId = new ThreadLocal<>();
    private static final ThreadLocal<String> currentUsername = new ThreadLocal<>();

    private BaseContext() {
        // 工具类禁止实例化
    }

    /**
     * 保存当前用户ID
     *
     * @param id 用户ID
     */
    public static void
    setCurrentId(Integer id) {
        currentId.set(id);
    }

    /**
     * 获取当前用户ID
     *
     * @return 当前用户ID，未设置时返回 null
     */
    public static Integer getCurrentId() {
        return currentId.get();
    }

    /**
     * 保存当前用户名
     *
     * @param username 用户名
     */
    public static void setCurrentUsername(String username) {
        currentUsername.set(username);
    }

    /**
     * 获取当前用户名
     *
     * @return 当前用户名，未设置时返回 null
     */
    public static String getCurrentUsername() {
        return currentUsername.get();
    }

    /**
     * 清理当前线程的所有上下文数据
     * <p>
     * 必须在请求结束后调用（通常在拦截器的 afterCompletion 中），
     * 防止内存泄漏，也防止线程池复用时的数据串用。
     * </p>
     */
    public static void clear() {
        currentId.remove();
        currentUsername.remove();
    }

    /**
     * 判断当前线程是否已设置用户ID
     *
     * @return true 如果已设置用户ID
     */
    public static boolean isCurrentIdSet() {
        return currentId.get() != null;
    }

    /**
     * 判断当前线程是否已设置用户名
     *
     * @return true 如果已设置用户名
     */
    public static boolean isCurrentUsernameSet() {
        return currentUsername.get() != null;
    }

    /**
     * 获取当前上下文状态的快照
     * <p>
     * 快照是不可变的，可以在 {@link #clear()} 之后继续持有。
     * 适用于需要在请求结束后记录上下文信息的场景（如异常处理、审计日志）。
     * </p>
     *
     * @return 当前上下文快照，永不为 null
     */
    public static ContextSnapshot getSnapshot() {
        return new ContextSnapshot(currentId.get(), currentUsername.get());
    }

    /**
     * 以字符串形式输出当前 ThreadLocal 状态，便于日志调试
     * <p>
     * 输出格式：{@code BaseContext{currentId=1, currentUsername=admin}}
     * </p>
     *
     * @return 当前状态的字符串表示
     */
    public static String dump() {
        return "BaseContext{currentId=" + currentId.get()
                + ", currentUsername=" + currentUsername.get() + "}";
    }

    /**
     * 用户上下文不可变快照
     * <p>
     * 在某个时间点捕获 BaseContext 的 ThreadLocal 状态，
     * 之后即使 ThreadLocal 被清理，快照仍然可用。
     * </p>
     */
    public static class ContextSnapshot {
        private final Integer id;
        private final String username;

        ContextSnapshot(Integer id, String username) {
            this.id = id;
            this.username = username;
        }

        /**
         * @return 用户ID，未设置时为 null
         */
        public Integer getId() {
            return id;
        }

        /**
         * @return 用户名，未设置时为 null
         */
        public String getUsername() {
            return username;
        }

        @Override
        public String toString() {
            return "ContextSnapshot{id=" + id + ", username=" + username + "}";
        }
    }
}
