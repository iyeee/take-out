package com.modq.reggie.dto;


import com.modq.reggie.entity.Setmeal;
import com.modq.reggie.entity.SetmealDish;
import com.modq.reggie.service.SetmealDishService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
