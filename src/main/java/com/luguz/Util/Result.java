package com.luguz.Util;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Guz
 * @create 2022-07--19 9:14
 */

@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean success;

    private String message;

    private Integer code;

    private long timestamp = System.currentTimeMillis();

    private T result;
}
