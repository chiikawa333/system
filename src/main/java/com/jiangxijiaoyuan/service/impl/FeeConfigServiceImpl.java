package com.jiangxijiaoyuan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangxijiaoyuan.dto.FeeConfigDTO;
import com.jiangxijiaoyuan.entity.FeeConfig;
import com.jiangxijiaoyuan.mapper.FeeConfigMapper;
import com.jiangxijiaoyuan.service.FeeConfigService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeeConfigServiceImpl extends ServiceImpl<FeeConfigMapper, FeeConfig>
        implements FeeConfigService {

    @Resource
    private FeeConfigMapper feeConfigMapper;

    @Override
    public FeeConfig getByParkingLotId(Long parkingLotId) {
        return feeConfigMapper.selectByParkingLotId(parkingLotId);
    }

    @Override
    public boolean saveOrUpdateConfig(Long parkingLotId, FeeConfigDTO configDTO) {
        FeeConfig feeConfig = feeConfigMapper.selectByParkingLotId(parkingLotId);

        if (feeConfig == null) {
            feeConfig = new FeeConfig();
            feeConfig.setParkingLotId(parkingLotId);
        }

        feeConfig.setPricePerHour(configDTO.getPricePerHour());
        feeConfig.setMinFee(configDTO.getMinFee());
        feeConfig.setMaxFee(configDTO.getMaxFee());
        feeConfig.setFreeMinutes(configDTO.getFreeMinutes());
        feeConfig.setUpdatedAt(LocalDateTime.now());

        if (feeConfig.getId() == null) {
            return save(feeConfig);
        } else {
            return updateById(feeConfig);
        }
    }
}
