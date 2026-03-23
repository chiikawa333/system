package com.jiangxijiaoyuan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangxijiaoyuan.dto.FeeConfigDTO;
import com.jiangxijiaoyuan.entity.FeeConfig;

public interface FeeConfigService extends IService<FeeConfig> {

    FeeConfig getByParkingLotId(Long parkingLotId);

    boolean saveOrUpdateConfig(Long parkingLotId, FeeConfigDTO configDTO);
}
