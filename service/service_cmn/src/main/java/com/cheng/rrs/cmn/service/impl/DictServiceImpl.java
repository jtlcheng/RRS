package com.cheng.rrs.cmn.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.cmn.listener.DictListener;
import com.cheng.rrs.cmn.mapper.DictMapper;
import com.cheng.rrs.cmn.service.DictService;
import com.cheng.rrs.model.cmn.Dict;
import com.cheng.rrs.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据词典接口实现方法
 * @author cheng
 * @version 1.0
 */
@Service
public class DictServiceImpl extends ServiceImpl<DictMapper, Dict> implements DictService {

    //根据数据id查询了数据列表
    @Override
    @Cacheable(value = "dict",keyGenerator = "keyGenerator")
    @Transactional(propagation= Propagation.REQUIRED)
    public List<Dict> findChildData(Long id) {
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        List<Dict> dicts = baseMapper.selectList(wrapper);
        for (Dict dict : dicts) {
            Long dictId = dict.getId();
            boolean isChild = this.isChildren(dictId);
            dict.setHasChildren(isChild);
        }
        return dicts;
    }
    //判断id下面是否有子节点
    private boolean isChildren(Long id){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("parent_id",id);
        Long count = baseMapper.selectCount(wrapper);
        return count>0;
    }
    //导出数据接口
    @Override
    public void exportData(HttpServletResponse response) {
        //设置下载信息
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");

        //这里URLEncoder.encode可以防止中文乱码，当然和easyExcel没关系
        String fileName= "dict";
        response.setHeader("Content-disposition","attachment;filename="+fileName+".xlsx");

        //查询数据库
        List<Dict> dictList = baseMapper.selectList(null);
        //Dict--DictEeVo
        List<DictEeVo> dictEeVos=new ArrayList<>();
        for (Dict dict : dictList) {
            DictEeVo dictEeVo=new DictEeVo();
            //把Dict转换为dictEeVo
            BeanUtils.copyProperties(dict,dictEeVo);
            dictEeVos.add(dictEeVo);
        }

        //调用方法进行写操作
        try {
            EasyExcel.write(response.getOutputStream(), DictEeVo.class).sheet("dict")
                    .doWrite(dictEeVos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //导入数据实现方法
    @Override
    @CacheEvict(value = "dict",allEntries = true)
    public void importDict(MultipartFile file) {
        try {
            EasyExcel.read(file.getInputStream(),DictEeVo.class,new DictListener(baseMapper)).sheet().doRead();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //根据dictCode和value查询
    @Override
    public String getDictName(String dictCode, String value) {
        //如果dictCode为空，根据value查询
        if (StringUtils.isEmpty(dictCode)){
            //直接根据value查询
            QueryWrapper<Dict> wrapper=new QueryWrapper<>();
            wrapper.eq("value",value);
            Dict dict = baseMapper.selectOne(wrapper);
            return dict.getName();
        }else {//如果dictCode为空，根据dictCode和value查询
            //根据dictCode查询dict对象，得到dict的id值
            Dict codeDict = this.getDictByDictCode(dictCode);
            Long parentId = codeDict.getId();
            //根据parent_id和value进行查询
            Dict finalDict = baseMapper.selectOne(new QueryWrapper<Dict>()
                    .eq("parent_id", parentId)
                    .eq("value", value));
            return finalDict.getName();
        }
    }

    ////根据dictCode获取下级节点
    @Override
    public List<Dict> findByDictCode(String dictCode) {
        //根据dictCode获取对应id
        Dict dict = this.getDictByDictCode(dictCode);
        //根据id获取子节点
        List<Dict> childData = this.findChildData(dict.getId());
        return childData;
    }

    private Dict getDictByDictCode(String dictCode){
        QueryWrapper<Dict> wrapper=new QueryWrapper<>();
        wrapper.eq("dict_code",dictCode);
        Dict codeDict = baseMapper.selectOne(wrapper);
        return codeDict;
    }
}
