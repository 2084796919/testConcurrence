package com.luguz.Util;

import com.lmax.disruptor.EventFactory;
import com.luguz.vo.SecondKillEvent;

/**
 * @author Guz
 * @create 2022-08--16 16:59
 */
public class SecondKillEventFactory implements EventFactory<SecondKillEvent> {

    @Override
    public SecondKillEvent newInstance() {
        return new SecondKillEvent();
    }
}
