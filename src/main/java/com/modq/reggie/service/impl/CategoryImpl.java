package com.modq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.modq.reggie.common.CustomException;
import com.modq.reggie.entity.Category;
import com.modq.reggie.entity.Dish;
import com.modq.reggie.entity.Setmeal;
import com.modq.reggie.mapper.CategoryMapper;
import com.modq.reggie.service.CategoryService;
import com.modq.reggie.service.DishService;
import com.modq.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Override
    public void remove(Long id) {
        //分类是否关联菜品或者套餐
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper=new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int dishCount = dishService.count(dishLambdaQueryWrapper);
        if(dishCount>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");

        }

        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper=new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int setmealCount=setmealService.count(setmealLambdaQueryWrapper);
        if(setmealCount>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }


        super.removeById(id);
    }
}
