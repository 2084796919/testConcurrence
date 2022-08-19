package com.luguz.vo;

import lombok.*;

/**
 * @author Guz
 * @create 2022-08--16 16:59
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// 事件对象（秒杀事件）
public class SecondKillEvent {
    private static final long serialVersionUID = 1L;
    private Integer seckillId;
    private Integer userId;
}
