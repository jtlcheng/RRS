package com.cheng.rrs.cmn.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.cmn.Dict;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 数据词典实现mybatisplus
 * @author cheng
 * @version 1.0
 */
@Mapper
@Repository
public interface DictMapper extends BaseMapper<Dict> {

}
