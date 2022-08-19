package com.luguz.service.impl;

import com.luguz.Util.Result;
import com.luguz.Util.ResultUtil;
import com.luguz.aspect.ServiceLock;
import com.luguz.pojo.Payment;
import com.luguz.pojo.Shop;
import com.luguz.mapper.ShopMapper;
import com.luguz.pojo.Successkill;
import com.luguz.service.IShopService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */

@Service
@Transactional(rollbackFor = Exception.class)
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService {
    @Autowired
    private SuccesskillServiceImpl successkillService;

    private Integer shopnum = 0;

    Logger logger = LoggerFactory.getLogger(getClass());


    //    加锁
    ReentrantLock lock = new ReentrantLock();

//    记录商品秒杀完毕信息
    private  ConcurrentHashMap<Integer,Boolean> shopMap = new ConcurrentHashMap<>();

    @Autowired
    private ShopServiceImpl shopService;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private PaymentServiceImpl paymentService;

//    正常锁
//    @Override
//    @ServiceLock
//    public Result kill(Integer userId, Integer shopid) throws Exception {
//        lock.lock();
//        try{
//            //          判断库存是否足够 并且用户只能购买一次
//            Successkill serviceById = successkillService.getById(userId);
//            Shop shop = shopService.getById(shopid);
//            if (shop.getNumber() >= 1 && serviceById == null){
//                //        库存足够 消费
//                //          减少库存
//                shop.setNumber(shop.getNumber()-1);
//                boolean update = shopService.updateById(shop);
//                if (!update){
//                    throw  new Exception("库存修改失败");
//                }
//                //  模拟订单
//                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//                if (!save){
//                    throw new Exception("秒杀失败");
//                }
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//                throw new Exception("秒杀失败");
//            }
//        }catch (Exception e){
//            throw new Exception("秒杀失败");
//        }finally {
//            lock.unlock();
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }

//    aop锁
//    @Override
//    @ServiceLock
//    public Result kill(Integer userId, Integer shopid) throws Exception {
//
//        try{
//            //          判断库存是否足够 并且用户只能购买一次
//            Successkill serviceById = successkillService.getById(userId);
//            Shop shop = shopService.getById(shopid);
//            if (shop.getNumber() >= 1 && serviceById == null){
//                //        库存足够 消费
//                //          减少库存
//                shop.setNumber(shop.getNumber()-1);
//                boolean update = shopService.updateById(shop);
//                if (!update){
//                    throw  new Exception("库存修改失败");
//                }
//                //  模拟订单
//                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//                if (!save){
//                    throw new Exception("秒杀失败");
//                }
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//                throw new Exception("秒杀失败");
//            }
//        }catch (Exception e){
//            throw new Exception("秒杀失败");
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }


//    @Override
//    public Result kill(Integer userId, Integer shopid) throws Exception {
//
//        try{
//            //          判断库存是否足够 并且用户只能购买一次
//            Successkill serviceById = successkillService.getById(userId);
////            使用for update 获取以后就是数据库加锁状态了
//            Shop shop = shopMapper.querySecondKillForUpdate(shopid);
//            if (shop.getNumber() >= 1 && serviceById == null){
//                //        库存足够 消费
//                //          减少库存
//                shop.setNumber(shop.getNumber()-1);
//                boolean update = shopService.updateById(shop);
//                if (!update){
//                    throw  new Exception("库存修改失败");
//                }
//                //  模拟订单
//                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//                if (!save){
//                    throw new Exception("秒杀失败");
//                }
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//                throw new Exception("秒杀失败");
//            }
//        }catch (Exception e){
//            throw new Exception("秒杀失败");
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }


//    @Override
//    public Result kill(Integer userId, Integer shopid) throws Exception {
//
//        try{
//            //          使用update  不校验，直接扣库存更新
////            使用for update 获取以后就是数据库加锁状态了
////              直接减少库存 update语句的时候，实际上会对记录加独占锁（X锁）另外其他事务对持有独占锁的记录进行修改的时候会被阻塞。这个锁并不是执行完update语句才会释放，而是会等事务结束时才会释放。
//            int shop = shopMapper.updateSecondKillById(shopid);
//            Successkill serviceById = successkillService.getById(userId);
//            if (shop > 0 && serviceById == null){
//
//                //  模拟订单
//                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//                if (!save){
//                    throw new Exception("秒杀失败");
//                }
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//                throw new Exception("秒杀失败");
//            }
//        }catch (Exception e){
//            throw new Exception("秒杀失败");
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }

//    @Override
//    public Result kill(Integer userId, Integer shopid) throws Exception {
//
//        try{
//            // 乐观锁，不进行库存数量的校验，直接减库存
//            Successkill serviceById = successkillService.getById(userId);
////            使用for update 获取以后就是数据库加锁状态了
//            Shop oldShop = shopService.getById(shopid);
//
//            if (oldShop.getNumber() > 0 && serviceById == null){
////                减少库存 Update表锁
//                int res = shopMapper.updateSecondKillByVersion(shopid, oldShop.getVersion());
//                if(res > 0){
//                    //  模拟订单
//                    boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//                    if (!save){
//                        throw new Exception("秒杀失败");
//                    }
//                }else {
//                    return ResultUtil.error("用户秒杀失败");
//                }
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//                throw new Exception("秒杀失败");
//            }
//        }catch (Exception e){
//            throw new Exception("秒杀失败");
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }


//  队列 SecondKillQueue 跟  Disruptor
//    @Override
//    @ServiceLock
//    public Result kill(Integer userId, Integer shopid) {
//        try{
//            //          判断库存是否足够 并且用户只能购买一次
//            Successkill serviceById = successkillService.getById(userId);
//            Shop shop = shopService.getById(shopid);
//            if (shop.getNumber() >= 1 && serviceById == null){
//                //        库存足够 消费
//                //          减少库存
//                shop.setNumber(shop.getNumber()-1);
//                boolean update = shopService.updateById(shop);
//                //  模拟订单
//                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
//
//            }else {
//                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
//            }
//        }catch (Exception e){
//            System.out.println("e:"+ e);
//        }
//        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
//    }

//    redis锁
    @Override
    @ServiceLock
    public Result kill(Integer userId, Integer shopid) {
        try{
//            改进 添加一个并发安全的map 记录商品状态 如果为false表示秒杀空了直接返回
            if(shopMap.get(shopid)!= null && !shopMap.get(shopid)){
                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
                return ResultUtil.error(String.format("商品 %s 暂无或用户 %s 已秒杀",shopid ,userId));
            }
            //          判断库存是否足够 并且用户只能购买一次
            Successkill serviceById = successkillService.getById(userId);
            Shop shop = shopService.getById(shopid);
            if (shop.getNumber() >= 1 && serviceById == null){
                //        库存足够 消费
                //          减少库存
                shop.setNumber(shop.getNumber()-1);
                boolean update = shopService.updateById(shop);
                if (!update){
                    throw  new Exception("库存修改失败");
                }
                //  模拟订单
                boolean save = successkillService.save(new Successkill().setKillid(shopid).setUserid(userId).setCreate_time(LocalDateTime.now()).setStatus(1).setShopnum(this.shopnum++));
                if (!save){
                    throw new Exception("秒杀失败");
                }
            }else {
//                记录商品库存状态
                shopMap.put(shopid, false);
                logger.info("商品{}暂无或用户 {} 已秒杀",shopid ,userId);
                throw new Exception("秒杀失败");
            }
        }catch (Exception e){
            return ResultUtil.error(String.format("用户%s秒杀失败", userId));
        }
        return ResultUtil.success(String.format("用户%s秒杀成功", userId));
    }

}
