package com.modq.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.modq.reggie.entity.Category;
import org.springframework.stereotype.Service;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
