package com.kim.kimipc.factory;

import com.yanantec.annotation.AbstractFactory;
import com.yanantec.annotation.Produce;

/**
 * @author : wengliuhu
 * @version : 0.1
 * @since : 2021/7/9 10:19
 * Describe：
 */
@AbstractFactory(name = "PeopleFactory")
public interface Mum {
    @Produce
    People createPeople(String name);
}
