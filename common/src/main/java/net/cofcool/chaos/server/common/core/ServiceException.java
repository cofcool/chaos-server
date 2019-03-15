package net.cofcool.chaos.server.common.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义异常, Service层异常
 *
 * @author CofCool
 */
public class ServiceException extends RuntimeException implements ExceptionLevel {

    public static final Logger log = LoggerFactory.getLogger(ServiceException.class);

    private static final long serialVersionUID = 3055975161234192313L;

    private String code;

    private int level;

    public ServiceException(String message) {
        this(message, null, HIGHEST_LEVEL);
    }

    public ServiceException(String message, String code) {
        this(message, code, HIGHEST_LEVEL);
    }

    public ServiceException(String message, String code, int level) {
        super(message);
        this.code = code;
        this.level = level;
    }

    public String getCode() {
        return code;
    }

    @Override
    public int getLevel() {
        return level;
    }
}
