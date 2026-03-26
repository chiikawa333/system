package com.intelligent.driver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.intelligent.driver.entity.FeeConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FeeConfigMapper extends BaseMapper<FeeConfig> {

    @Select("SELECT * FROM fee_config WHERE parking_lot_id = #{parkingLotId}")
    FeeConfig selectByParkingLotId(Long parkingLotId);
}
