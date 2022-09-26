package com.cheng.rrs.oos.service.impl;

import com.cheng.rrs.oos.service.FileService;
import com.cheng.rrs.oos.utils.ConstantOssPropertiesUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @package: com.cheng.rrs.oos.service.impl
 * @Author: cheng
 * @Date: 2022-09-18 14:54
 **/
@Service
public class FileServiceImpl implements FileService {
    //文件上传
    @Override
    public String upload(MultipartFile file) {
        // Endpoint以华东1（杭州）为例，其它Region请按实际情况填写。
        String endpoint = ConstantOssPropertiesUtils.ENDPOINT_URL;
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
        String accessKeyId = ConstantOssPropertiesUtils.ACCESS_KEY_ID;
        String accessKeySecret = ConstantOssPropertiesUtils.SECRET;
        // 填写Bucket名称，例如examplebucket。
        String bucketName = ConstantOssPropertiesUtils.BUCKET;
        // 填写Object完整路径，完整路径中不能包含Bucket名称，例如exampledir/exampleobject.txt。
        String objectName = "exampledir/exampleobject.txt";
        // 填写本地文件的完整路径，例如D:\\localpath\\examplefile.txt。
        // 如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        String filePath = "D:\\localpath\\examplefile.txt";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        InputStream inputStream = null;
        String filename = null;
        try {
//            new FileInputStream(filePath);
            //上传文件流
            inputStream = file.getInputStream();
            filename = file.getOriginalFilename();
            //按照当前日期 创建文件夹 上传到上传文件夹里面
            String timeUrl = new DateTime().toString("yyyy/MM/dd");
            filename=timeUrl+"/"+System.currentTimeMillis()+"->"+filename;
            // 创建PutObject请求。
            ossClient.putObject(bucketName, filename  , inputStream);
            //关闭 ossClient
            ossClient.shutdown();
            //上传之后文件路径
            String url = "https://" + bucketName + "." + endpoint + "/" + filename;
            return url;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
