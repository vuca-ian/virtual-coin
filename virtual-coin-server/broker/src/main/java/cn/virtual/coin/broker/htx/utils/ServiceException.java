package cn.virtual.coin.broker.htx.utils;

/**
 *
 * @author gdyang
 * @since  2021/4/11 11:26 上午
 */
public class ServiceException extends RuntimeException implements IErrorMessage{
    private static final long serialVersionUID = 7398234008172186340L;

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
