package com.dryork.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dryork.entity.SysFilterChain;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jsen
 * @since 2018-04-08
 */
@Service
public interface SysFilterChainMapper extends BaseMapper<SysFilterChain> {

    List<SysFilterChain> listAll();

    int deleteByUrl(@Param("url") String url);

    int insertFilter(SysFilterChain sysFilterChain);

    SysFilterChain getFilterByUrl(@Param("url") String url);

}
