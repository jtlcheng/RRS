package com.cheng.rrs.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.vo.order.OrderCountQueryVo;
import com.cheng.rrs.vo.order.OrderCountVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @package: com.cheng.rrs.order.mapper
 * @Author: cheng
 * @Date: 2022-09-23 10:53
 **/
@Mapper
@Repository
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
    //查询预约统计数据的方法
    List<OrderCountVo> selectOrderCount(@Param("vo") OrderCountQueryVo orderCountQueryVo);
}
