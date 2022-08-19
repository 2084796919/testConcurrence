package com.luguz.aspect;

import java.lang.annotation.*;

/**
 * @author Guz
 * @create 2022-08--16 12:04
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public  @interface ServiceLock {
    String description()  default "";
}
