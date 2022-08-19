package com.luguz.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.luguz.Util.DisruptorUtil;
import com.luguz.Util.Result;
import com.luguz.Util.ResultUtil;
import com.luguz.Util.SecondKillQueue;
import com.luguz.pojo.Shop;
import com.luguz.pojo.Successkill;
import com.luguz.service.impl.ShopServiceImpl;
import com.luguz.service.impl.SuccesskillServiceImpl;
import com.luguz.vo.SecondKillEvent;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */
@RestController
@RequestMapping("/shop")
public class ShopController {
    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ShopServiceImpl shopService;
    @Autowired
    private SuccesskillServiceImpl successkillService;

//    加锁
    ReentrantLock lock = new ReentrantLock();

    @Autowired
    private StringRedisTemplate redisTemplate;

    @RequestMapping("/getAll")
    public Map<String, Object> getall(){
        return shopService.getMap(new QueryWrapper<Shop>());
    }

//    普通
//    @RequestMapping("/kill/{shopid}")
    public Result kill(@PathVariable Integer shopid){
        //          随机生成一个userid
        final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;
        try{
//          判断库存是否足够 并且用户只能购买一次
            Successkill serviceById = successkillService.getById(userId);
            Shop shop = shopService.getById(shopid);
            if (shop.getNumber() >= 1 && serviceById == null){
//            库存足够 消费
                shopService.kill(userId,shopid);
            }else {
                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
                return ResultUtil.error(String.format("用户%s秒杀失败", userId));
            }
            return ResultUtil.success(String.format("用户%s秒杀成功", userId));
        }catch (Exception e){
            logger.info("商品{}暂无或用户 {} 秒杀失败",shopid ,userId);
            return ResultUtil.error(String.format("用户%s秒杀失败", userId));
        }finally {

        }
    }

//    加锁
//    @RequestMapping("/kill/{shopid}")
//    public Result lockKill(@PathVariable Integer shopid){
//        //          随机生成一个userid
//        lock.lock();
//        final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;
//        try{
//            return shopService.kill(userId, shopid);
//        }catch (Exception e){
//            logger.info("商品{}暂无或用户 {} 秒杀失败",shopid ,userId);
//            return ResultUtil.error(String.format("用户%s秒杀失败", userId));
//        }finally {
//            lock.unlock();
//        }
//    }

////  数据库悲观锁
//    @RequestMapping("/kill/{shopid}")
//    public Result lockKill(@PathVariable Integer shopid){
//        //          随机生成一个userid
//
//        final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;
//        try{
//            return shopService.kill(userId, shopid);
//        }catch (Exception e){
//            logger.info("商品{}暂无或用户 {} 秒杀失败",shopid ,userId);
//            return ResultUtil.error(String.format("用户%s秒杀失败", userId));
//        }
//    }

//   "秒杀实现方式——消息队列"
//    @RequestMapping("/kill/{shopid}")
//    public Result lockKill(@PathVariable Integer shopid){
//        Successkill kill = new Successkill();
//        try {
//            final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;
//            kill.setKillid(shopid);
//            kill.setUserid(userId);
//            Boolean flag = SecondKillQueue.getSkillQueue().produce(kill);
//            // 虽然进入了队列，但是不一定能秒杀成功 进队出队有时间间隙
//            if(flag){
//                logger.info("用户:{}{}",kill.getUserid(),"秒杀成功");
//            }else{
//                logger.info("用户:{}{}",userId,"秒杀失败");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", kill.getUserid()));
//    }

//    //   "秒杀实现方式——消息队列"
//    @RequestMapping("/kill/{shopid}")
//    public Result lockKill(@PathVariable Integer shopid){
//        final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;
//        try {
//            logger.info("开始秒杀方式七...");
//            SecondKillEvent kill = new SecondKillEvent();
//            kill.setSeckillId(userId);
//            kill.setUserId(shopid);
//            DisruptorUtil.producer(kill);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }

    //   "秒杀实现方式——redis"
    @RequestMapping("/kill/{shopid}")
    public Result lockKill(@PathVariable Integer shopid){
        final Integer userId = (int) (new Random().nextDouble() * (99999 - 10000 + 1)) + 10000;

//        很多时候我们都是使用自定义的超时时间释放锁，所以看门狗一般也用不上，
//        为防止释放了非本线程的锁，每次释放前调用isHeldByCurrentThread判断下是否为本线程的锁，非本线程的锁不允许释放
//       获取锁 商品id作为锁  并且增加过期时间
        Result result = ResultUtil.success(String.format("用户%s秒杀成功", userId));
//        不重复秒杀 没有获取到锁就秒杀失败
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", String.valueOf(shopid),30, TimeUnit.SECONDS);
//        if (lock) {
//            logger.info("成功获取锁");
//            //以下是业务代码
//            result = shopService.kill(userId, shopid);
//            //释放锁

//            redisTemplate.delete("lock");
//        } else {
//            logger.info("未获取到锁");
//            return ResultUtil.success(String.format("用户%s秒杀失败", userId));
//        }

//      重复秒杀 循环获取锁
        while( !redisTemplate.opsForValue().setIfAbsent("lock", String.valueOf(shopid),3, TimeUnit.SECONDS) ){
            logger.info("成功获取锁");
            //以下是业务代码
            result = shopService.kill(userId, shopid);
            //释放锁
            //使用LUA脚本执行原子操作，避免锁误删
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(script);
            // Redis中的integer对应Java中的Long 需要设置一下返回值类型 为Long
            // 因为删除判断的时候，返回的0,给其封装为数据类型。如果不封装那么默认返回String 类型，
            // 那么返回字符串与 0 shopid 为 Integer 会有发生错误。
            redisScript.setResultType(Long.class);
            // 第一个要是script 脚本 ，第二个需要判断的key，第三个就是key所对应的值。
            redisTemplate.execute(redisScript, Arrays.asList("lock"), String.valueOf(shopid));

//            redisTemplate.delete("lock");
        }

        return result;
    }


}
