package com.luguz.service;

import com.luguz.Util.Result;
import com.luguz.pojo.Shop;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */
public interface IShopService extends IService<Shop> {

    Result kill(Integer userId, Integer shopid) throws Exception;
}
