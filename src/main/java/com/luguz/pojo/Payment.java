package com.luguz.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Payment implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer killid;

    private Integer userid;

    private Integer status;

    private String money;

    private LocalDateTime createTime;


}
