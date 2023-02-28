package com.modq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.modq.reggie.dto.SetmealDto;
import com.modq.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /**
     * 新增套餐，同时保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    void saveWithDish(SetmealDto setmealDto);

    void removeWithDish(List<Long> ids);
    void updateWithDish(SetmealDto setmealDto);
    @Deprecated
    void startSelling(List<Long> ids);
    @Deprecated
    void stopSelling(List<Long> ids);
    void updateStat(Integer stat,List<Long> ids);

    void updateSetmealStatusById(Integer status, List<Long> ids);

    SetmealDto getDate(Long id);
}