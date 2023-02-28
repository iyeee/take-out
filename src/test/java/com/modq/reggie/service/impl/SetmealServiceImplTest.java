package com.modq.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.modq.reggie.entity.Setmeal;
import com.modq.reggie.service.SetmealDishService;
import com.modq.reggie.service.SetmealService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SetmealServiceImplTest {
    @Autowired
    private SetmealService setmealService;

    @Test
    void startSelling() {
        List<Long> ids=new ArrayList<>();
        ids.add(1630449268725850114L);
        ids.add(1415580119015145474L);
        LambdaUpdateWrapper<Setmeal> lambdaUpdateWrapper=new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.in(Setmeal::getId,ids);
        lambdaUpdateWrapper.set(Setmeal::getStatus,1);
        setmealService.update(lambdaUpdateWrapper);
    }
}