package com.dryork.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dryork.dc.client.service.logical.DcClientCoreService;
import com.dryork.dc.core.utils.ActionType;
import com.dryork.dc.core.utils.DcRequest;
import com.dryork.entity.Table0;
import com.dryork.mapper.Table0Mapper;
import com.dryork.service.Table0Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dryork.utils.ResponseBase;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jsen
 * @since 2018-11-20
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class Table0ServiceImpl extends ServiceImpl<Table0Mapper, Table0> implements Table0Service {

    @Autowired
    DcClientCoreService dcClientCoreService;

    @Override
    public ResponseBase add() {
        Table0 table0 = new Table0().setName("luvy" + System.currentTimeMillis());


        baseMapper.insert(table0);
        String ech = dcClientCoreService.echo("jsen");

        DcRequest dcRequest = new DcRequest().setActionType(ActionType.BATCH_INSERT);
        Map<String, Object> record = Maps.newHashMap();
        JSONObject jsonObject = (JSONObject)JSON.toJSON(table0);
        record.putAll(jsonObject);
        Map<String, Map<String, Object>> params = Maps.newHashMap();
        params.put("table0", record);
        dcRequest.setParams(params);
        dcClientCoreService.exec(dcRequest, false);

        return ResponseBase.create().code(0);
    }

    @Override
    public ResponseBase del(Integer id) {
        return ResponseBase.create().code(0).data(baseMapper.deleteById(id));
    }

    @Override
    public ResponseBase update(Integer id) {
        Table0 table0 = baseMapper.selectById(id);
        table0.setName("luvy" + System.currentTimeMillis());
        return ResponseBase.create().code(0).data(baseMapper.update(table0, new QueryWrapper<>(new Table0().setId(id))));
    }
}
