package com.modq.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.modq.reggie.common.CustomException;
import com.modq.reggie.common.R;
import com.modq.reggie.dto.DishDto;
import com.modq.reggie.dto.SetmealDto;
import com.modq.reggie.entity.Category;
import com.modq.reggie.entity.Setmeal;
import com.modq.reggie.service.CategoryService;
import com.modq.reggie.service.SetmealDishService;
import com.modq.reggie.service.SetmealService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     *新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("新增套餐接口")
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page",value = "页码",readOnly = true),
            @ApiImplicitParam(name = "pageSize",value = "每页记录数",readOnly = true),
            @ApiImplicitParam(name = "name",value = "套餐名称",readOnly = false)
    })
    public R<Page<SetmealDto>> page(int page,int pageSize,String name){
        Page<Setmeal> setmealPage=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Setmeal::getName,name);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList= records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(setmealDtoList);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("套餐删除接口")
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("ids:{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }
    // @GetMapping("/{id}")
    // public R<String> get(@PathVariable Long id){
    //     se
    // }

    // @PostMapping("/status/1")
    // public R<String> startSelling(@RequestParam List<Long> ids){
    //     log.info("ids:{}",ids);
    //     setmealService.startSelling(ids);
    //     return R.success("套餐起售成功");
    // }
    //
    // @PostMapping("/status/0")
    // public R<String> stopSelling(@RequestParam List<Long> ids){
    //     log.info("ids:{}",ids);
    //     setmealService.stopSelling(ids);
    //     return R.success("套餐停售成功");
    // }

    @PostMapping("/status/{stat}")
    @ApiOperation("套餐状态改变接口")
    public R<String> updateStatus(@PathVariable Integer stat,@RequestParam  List<Long> ids){
        setmealService.updateStat(stat,ids);
        return R.success("改变套餐状态成功");
    }

    @GetMapping("/{id}")
    @ApiOperation("套餐获取接口")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto=setmealService.getDate(id);
        return R.success(setmealDto);
    }

    @PutMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    @ApiOperation("套餐修改接口")
    public R<String> update(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return R.success("套餐修改成功");
    }

    /**
     * 根据条件查询套餐
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    @Cacheable(value = "setmealCache",key = "#setmeal.categoryId+'_'+#setmeal.status")
    @ApiOperation("套餐查询接口")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        lambdaQueryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        lambdaQueryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
