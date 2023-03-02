package com.modq.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.modq.reggie.common.BaseContext;
import com.modq.reggie.common.R;
import com.modq.reggie.entity.ShoppingCart;
import com.modq.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


/**
 * 购物车
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 添加购物车
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        log.info("购物车数据:{}", shoppingCart);

        // 设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        // 查询当前菜品或者套餐是否存在
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, currentId);

        if (dishId != null) {
            //添加的购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId, dishId);

        } else {
            // 添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());

        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        // 如果已经存在，就在原来的数量基础上加一
        if (cartServiceOne != null) {
            Integer cartServiceOneNumber = cartServiceOne.getNumber();
            cartServiceOne.setNumber(cartServiceOneNumber + 1);
            shoppingCartService.updateById(cartServiceOne);
        } else {
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }

        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     *
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list() {
        log.info("查看购物车...");
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clean")
    public R<String> clean() {
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("清空购物车成功");
    }

    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart) {
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        if (dishId != null) {
            queryWrapper.eq(ShoppingCart::getDishId, dishId);
            ShoppingCart shoppingCart1 = shoppingCartService.getOne(queryWrapper);
            shoppingCart1.setNumber(shoppingCart1.getNumber() - 1);
            Integer LatestNUmber = shoppingCart1.getNumber();
            if (LatestNUmber > 0) {
                shoppingCartService.updateById(shoppingCart1);
            } else if (LatestNUmber==0) {
                shoppingCartService.removeById(shoppingCart1.getId());
            } else {
                return R.error("操作异常");
            }
            return R.success(shoppingCart);
        }
        Long setmealId = shoppingCart.getSetmealId();
        if (setmealId != null) {
            queryWrapper.eq(ShoppingCart::getSetmealId, setmealId).eq(ShoppingCart::getUserId, BaseContext.getCurrentId());
            ShoppingCart shoppingCart2 = shoppingCartService.getOne(queryWrapper);
            shoppingCart2.setNumber(shoppingCart2.getNumber() - 1);
            Integer LatestNumber = shoppingCart2.getNumber();
            if (LatestNumber > 0) {
                shoppingCartService.updateById(shoppingCart2);
            } else if (LatestNumber == 0) {
                shoppingCartService.removeById(shoppingCart.getId());
            } else {
                return R.error("操作异常");

            }
            return R.success(shoppingCart2);
        }
        return R.error("操作异常");

    }
}