package com.cheng.rrs.oos.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @package: com.cheng.rrs.oos.service
 * @Author: cheng
 * @Date: 2022-09-18 14:54
 **/
public interface FileService {
    //文件上传
    String upload(MultipartFile file);


}
