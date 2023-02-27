package com.modq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.modq.reggie.dto.DishDto;
import com.modq.reggie.entity.Dish;

public interface DishService extends IService<Dish> {
    // 新增菜品，同时插入菜品对应的口味数据，需要操作俩张表，dish，dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithFlavor(DishDto dishDto);
}
