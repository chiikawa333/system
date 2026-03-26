package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.dto.FeeConfigDTO;
import com.intelligent.driver.entity.FeeConfig;

public interface FeeConfigService extends IService<FeeConfig> {

    FeeConfig getByParkingLotId(Long parkingLotId);

    boolean saveOrUpdateConfig(Long parkingLotId, FeeConfigDTO configDTO);
}
