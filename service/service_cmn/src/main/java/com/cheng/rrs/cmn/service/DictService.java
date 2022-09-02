package com.cheng.rrs.cmn.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.cmn.Dict;
import com.cheng.rrs.model.hosp.HospitalSet;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface DictService extends IService<Dict> {

    //根据父id查询子数据
    List<Dict> findChildData(Long id);

    //导出数据接口
    void exportData(HttpServletResponse response);

    //导入数据
    void importDict(MultipartFile file);
}
