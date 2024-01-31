package com.abin.mallchat.redis.service;

/**
 * Description:
 *
 * @author likain
 * @since 2023/8/27 15:37
 */
public class ExpireNotExistException extends RuntimeException {

    public ExpireNotExistException(String message) {
        super(message);
    }

    public ExpireNotExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
