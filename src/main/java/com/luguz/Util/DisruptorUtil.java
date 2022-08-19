package com.luguz.Util;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.luguz.vo.SecondKillEvent;

import java.util.concurrent.ThreadFactory;

/**
 * @author Guz
 * @create 2022-08--16 17:09
 */
public class DisruptorUtil {
    static Disruptor<SecondKillEvent> disruptor;

    static{
        SecondKillEventFactory factory = new SecondKillEventFactory();
        int ringBufferSize = 1024;
        ThreadFactory threadFactory = runnable -> new Thread(runnable);
        disruptor = new Disruptor<>(factory, ringBufferSize, threadFactory);
//        由于是new 方式创建的 SecondKillEventConsumer 所以在 SecondKillEventConsumer 中使用 @Autowried注解就无效了
//        两种方式 将SecondKillEventConsumer 作为 Conpoment注入 或者使用 SpringUtil普通方法获取
        disruptor.handleEventsWith(new SecondKillEventConsumer());
        disruptor.start();
    }

    public static void producer(SecondKillEvent kill){
        RingBuffer<SecondKillEvent> ringBuffer = disruptor.getRingBuffer();
        SecondKillEventProducer producer = new SecondKillEventProducer(ringBuffer);
        producer.secondKill(kill.getUserId(),kill.getSeckillId());
    }
}
