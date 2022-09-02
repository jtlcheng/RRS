package com.cheng.rrs.cmn.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.cheng.rrs.cmn.mapper.DictMapper;
import com.cheng.rrs.model.cmn.Dict;
import com.cheng.rrs.vo.cmn.DictEeVo;
import org.springframework.beans.BeanUtils;

public class DictListener extends AnalysisEventListener<DictEeVo> {

    private DictMapper dictMapper;

    public DictListener(DictMapper dictMapper) {
        this.dictMapper = dictMapper;
    }

    @Override
    public void invoke(DictEeVo dictEeVo, AnalysisContext context) {
        //调用方法添加数据库
        Dict dict=new Dict();
        BeanUtils.copyProperties(dictEeVo,dict);
        dict.setIsDeleted(0);
        dictMapper.insert(dict);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {

    }
}
