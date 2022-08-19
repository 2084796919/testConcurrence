package com.luguz.Util;

import com.lmax.disruptor.EventHandler;
import com.luguz.service.impl.ShopServiceImpl;
import com.luguz.vo.SecondKillEvent;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author Guz
 * @create 2022-08--16 17:09
 */
// 消费者(秒杀处理器)
@Slf4j
public class SecondKillEventConsumer implements EventHandler<SecondKillEvent> {


//    @Autowired
//    private ShopServiceImpl shopService;

//  使用工具类获取 或者将其注册为一个组件 在消费者使用的时候注入
    private ShopServiceImpl shopService = (ShopServiceImpl) SpringUtil.getBean("shopServiceImpl");

    @Override
    public void onEvent(SecondKillEvent seckillEvent, long seq, boolean bool) {
        Result result = shopService.kill(seckillEvent.getUserId(), seckillEvent.getSeckillId());
        if(result.equals(ResultUtil.success(String.format("用户%s秒杀成功", seckillEvent.getSeckillId())))){
            log.info("用户:{}{}",seckillEvent.getUserId(),"秒杀成功");
        }
    }
}

