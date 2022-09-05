package com.cheng.rrs.cmn.client;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-cmn")
@Repository
public interface DictFeignClient {
    /**
     * 根据dictcode和value查询
     * @param dictCode
     * @param value
     * @return
     */
    //根据dictcode和value查询
    @GetMapping("/admin/cmn/dict/getName/{dictCode}/{value}")
    @ApiOperation("根据dictcode和value查询")
    public String getName(@PathVariable("dictCode") String dictCode,
                          @PathVariable("value") String value);
    /**
     * 根据value查询
     * @param value
     * @return
     */
    //根据value查询
    @ApiOperation("根据value查询")
    @GetMapping("/admin/cmn/dict/getName/{value}")
    public String getName(@PathVariable("value")String value);
}
