package com.modq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.modq.reggie.common.R;
import com.modq.reggie.dto.DishDto;
import com.modq.reggie.entity.Category;
import com.modq.reggie.entity.Dish;
import com.modq.reggie.entity.DishFlavor;
import com.modq.reggie.service.CategoryService;
import com.modq.reggie.service.DishFlavorService;
import com.modq.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;
    @PostMapping()
    private R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        // 清理所有菜品缓存
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理某个分类下的缓存
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page<DishDto>> page(int page,int pageSize,String name){
        Page<Dish> dishPage=new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(dishPage,queryWrapper);

        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");
        List<Dish> records = dishPage.getRecords();
        List<DishDto> list = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);
        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和口味数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }
    @PutMapping()
    private R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        // 清理所有菜品缓存
        // Set keys = redisTemplate.keys("dish_*");
        // redisTemplate.delete(keys);

        // 清理某个分类下的缓存
        String key="dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
    // @GetMapping("/list")
    // //  http://localhost:8080/dish/list?categoryId=1397844303408574465
    // public R<List<Dish>> list(Dish dish){
    //     log.info(dish.toString());
    //     LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
    //     queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
    //     queryWrapper.eq(Dish::getStatus,1);
    //     queryWrapper.orderByAsc(Dish::getUpdateTime);
    //
    //     List<Dish> list = dishService.list(queryWrapper);
    //     return R.success(list);
    // }

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList=null;
        // 动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        // 先从redis获取缓存数据
        dishDtoList= (List<DishDto>) redisTemplate.opsForValue().get(key);

        // 如果存在，直接返回，无需查询数据库
        if(dishDtoList!=null){
            return R.success(dishDtoList);
        }

        // 如果不存在，需要查询数据库，将查询到的菜品数据缓存到redis


        log.info(dish.toString());
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if(category!=null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId=item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());


        //如果不存在，需要查询数据库，将查询到的菜品数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }



}

