package com.intelligent.driver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.intelligent.driver.entity.SignalCar;

import java.util.List;

public interface SignalCarService extends IService<SignalCar> {

    boolean saveSignalCar(SignalCar signalCar);

    boolean batchSaveSignalCar(List<SignalCar> signalCarList);

    Page<SignalCar> getAllRecords(int pageNum, int pageSize);

    List<SignalCar> getByCarId(String carId);

    List<SignalCar> getByLightColor(String lightColor);

    boolean deleteById(String id);

    boolean deleteBatch(List<String> ids);

    void deleteAll();
}
