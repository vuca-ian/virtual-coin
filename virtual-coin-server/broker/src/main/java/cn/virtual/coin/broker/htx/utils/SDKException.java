package cn.virtual.coin.broker.htx.utils;

import lombok.Getter;

/**
 * @author gdyang
 * @since 2025/2/25 20:55
 */
@Getter
public class SDKException extends RuntimeException {

    public static final String INPUT_ERROR = "InputError";

    public static final String RUNTIME_ERROR = "RuntimeError";

    public static final String ENV_ERROR = "EnvironmentError";

    public static final String EXEC_ERROR = "ExecuteError";

    public static final String KEY_MISSING = "KeyMissingError";

    public static final String SYS_ERROR = "SystemError";

    private final String errCode;

    public SDKException(String errCode, String errMsg) {
        super(errMsg);
        this.errCode = errCode;
    }
}
