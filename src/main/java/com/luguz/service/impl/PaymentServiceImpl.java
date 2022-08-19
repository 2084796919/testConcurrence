package com.luguz.service.impl;

import com.luguz.pojo.Payment;
import com.luguz.mapper.PaymentMapper;
import com.luguz.service.IPaymentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author guz
 * @since 2022-08-15
 */
@Service
public class PaymentServiceImpl extends ServiceImpl<PaymentMapper, Payment> implements IPaymentService {

}
