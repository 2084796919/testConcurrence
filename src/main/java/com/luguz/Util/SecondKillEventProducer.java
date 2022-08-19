package com.luguz.Util;

import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;
import com.luguz.vo.SecondKillEvent;

/**
 * @author Guz
 * @create 2022-08--16 17:07
 */
//生产者 将 KillEvent对象放入队列当中
public class SecondKillEventProducer {

    private final static EventTranslatorVararg<SecondKillEvent> translator = (seckillEvent, seq, objs) -> {
        seckillEvent.setSeckillId((Integer) objs[0]);
        seckillEvent.setUserId((Integer) objs[1]);
    };

    private final RingBuffer<SecondKillEvent> ringBuffer;

    public SecondKillEventProducer(RingBuffer<SecondKillEvent> ringBuffer){
        this.ringBuffer = ringBuffer;
    }

    public void secondKill(Integer userId, Integer seckillId){
        this.ringBuffer.publishEvent(translator, userId, seckillId);
    }

}
