package com.cheng.rrs.oos.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.oos.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @package: com.cheng.rrs.oos.controller
 * @Author: cheng
 * @Date: 2022-09-18 14:49
 **/
@RestController
@RequestMapping("api/oss/file")
@Api(tags = "oss上传")
public class FileApiController {
    @Autowired
    private FileService fileService;

    /**
    * @Description: 文件上传
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/9/18 14:52
    */
    @PostMapping("uploadFiles")
    @ApiOperation("文件上传到阿里云oss")
    public Result uploadFiles(MultipartFile file){
        //获取上传到文件
        String fileUrl=fileService.upload(file);
        return Result.ok(fileUrl);
    }
}
