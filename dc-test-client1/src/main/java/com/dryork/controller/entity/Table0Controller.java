package com.dryork.controller.entity;


import com.dryork.service.Table0Service;
import com.dryork.utils.ResponseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jsen
 * @since 2018-11-20
 */
@RestController
@RequestMapping("/table0")
public class Table0Controller {
    @Autowired
    Table0Service table0Service;

    @RequestMapping("/add")
    public ResponseBase add() {
        return table0Service.add();
    }

    @RequestMapping("/delete/{id}")
    public ResponseBase del(@PathVariable Integer id) {
        return table0Service.del(id);
    }

    @RequestMapping("/update/{id}")
    public ResponseBase update(@PathVariable Integer id) {
        return table0Service.update(id);
    }
}

