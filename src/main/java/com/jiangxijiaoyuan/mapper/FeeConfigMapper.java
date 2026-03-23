package com.jiangxijiaoyuan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiangxijiaoyuan.entity.FeeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeeConfigMapper extends BaseMapper<FeeConfig> {

    @Select("SELECT * FROM fee_config WHERE parking_lot_id = #{parkingLotId}")
    FeeConfig selectByParkingLotId(Long parkingLotId);
}
