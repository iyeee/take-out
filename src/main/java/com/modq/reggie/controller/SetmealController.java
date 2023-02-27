package com.modq.reggie.controller;

import com.modq.reggie.common.R;
import com.modq.reggie.dto.SetmealDto;
import com.modq.reggie.entity.Setmeal;
import com.modq.reggie.service.SetmealDishService;
import com.modq.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     *
     * @param setmealDto
     * @return
     */
    @PostMapping("/")
    public R<String> save(SetmealDto setmealDto){

    }
}
