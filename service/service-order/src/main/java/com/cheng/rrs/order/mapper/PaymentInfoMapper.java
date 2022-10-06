package com.cheng.rrs.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.model.order.PaymentInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @package: com.cheng.rrs.order.mapper
 * @Author: cheng
 * @Date: 2022-09-30 10:15
 **/
@Mapper
@Repository
public interface PaymentInfoMapper extends BaseMapper<PaymentInfo> {
}
