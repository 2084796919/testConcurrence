package com.luguz.pojo;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class Successkill implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer killid;

    private Integer userid;

    private Integer status;

    @TableField("create_time")
    private LocalDateTime create_time;

    private Integer shopnum;

}
