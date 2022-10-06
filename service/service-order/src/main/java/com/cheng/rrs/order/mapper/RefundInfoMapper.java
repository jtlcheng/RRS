package com.cheng.rrs.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.order.RefundInfo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @package: com.cheng.rrs.order.mapper
 * @Author: cheng
 * @Date: 2022-10-02 11:35
 **/
@Mapper
@Repository
public interface RefundInfoMapper extends BaseMapper<RefundInfo> {
}
