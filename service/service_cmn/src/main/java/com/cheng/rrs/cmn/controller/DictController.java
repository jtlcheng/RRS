package com.cheng.rrs.cmn.controller;

import com.cheng.rrs.cmn.service.DictService;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.model.cmn.Dict;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * 数据词典接口
 * @author cheng
 * @version 1.0
 */
@Api(tags = "数据词典")
@RestController
@RequestMapping("/admin/cmn/dict")
public class DictController {

    @Autowired
    DictService dictService;

    /**
     * 导入数据
     * @param file
     * @return
     */
    @PostMapping("importDict")
    @ApiOperation("导入数据接口")
    public Result importDict(MultipartFile file){
        dictService.importDict(file);
        return Result.ok();
    }

    /**
     * 导出数据接口
     * @param response
     * @return
     */
    @GetMapping("exportData")
    @ApiOperation("导出数据接口")
    public void exportData(HttpServletResponse response){
        dictService.exportData(response);
    }

    /**
     * 根据父id查询子数据
     * @param id
     * @return
     */
    @ApiOperation("根据父id查询子数据列表")
    @GetMapping("findChildData/{id}")
    public Result findChildData(@PathVariable Long id){
        List<Dict> list = dictService.findChildData(id);
        return Result.ok(list);
    }

    /**
     * 根据dictcode和value查询
     * @param dictCode
     * @param value
     * @return
     */
    //根据dictcode和value查询
    @GetMapping("getName/{dictCode}/{value}")
    @ApiOperation("根据dictcode和value查询")
    public String getName(@PathVariable String dictCode,
                          @PathVariable String value){
        String dictName=dictService.getDictName(dictCode,value);
        return dictName;
    }

    /**
     * 根据value查询
     * @param value
     * @return
     */
    //根据value查询
    @ApiOperation("根据value查询")
    @GetMapping("getName/{value}")
    public String getName(@PathVariable String value){
        String dictName=dictService.getDictName("",value);
        return dictName;
    }

    //根据dictCode获取下级节点
    /**
     * //根据dictCode获取下级节点
     * @param dictCode
     * @return
     */
    @ApiOperation("根据dictCode获取下级节点")
    @GetMapping("findByDictCode/{dictCode}")
    public Result<List<Dict>> findByDictCode(@PathVariable String dictCode){
        List<Dict> list=dictService.findByDictCode(dictCode);
        return Result.ok(list);
    }
}
