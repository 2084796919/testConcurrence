package com.luguz.Util;

import com.luguz.pojo.Successkill;
import com.luguz.service.impl.ShopServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author Guz
 * @create 2022-08--16 16:08
 */
@Slf4j
//@Component
class TaskRunner implements ApplicationRunner {

    @Autowired
    private ShopServiceImpl shopService;

    @Override
    public void run(ApplicationArguments var){
        new Thread(() -> {
            log.info("队列启动成功");
            while(true){
                try {
                    // 进程内队列
                    Successkill kill = SecondKillQueue.getSkillQueue().consume();
                    if(kill != null){
                        Result result = shopService.kill( kill.getUserid(),kill.getKillid());
                        if(result != null && result.equals(ResultUtil.success(String.format("用户%s秒杀成功", kill.getUserid())))){
                            log.info("TaskRunner,result:{}",result);
                            log.info("TaskRunner从消息队列取出用户，用户:{}{}",kill.getUserid(),"秒杀成功");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
