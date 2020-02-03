package com.exa.database.controller;

import com.exa.database.pojo.Label;
import com.exa.database.service.LabelService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//跨域
@CrossOrigin
@RequestMapping("/label")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<Label> labelList=labelService.findAll();
        int i=1/0;
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",labelList);
    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.GET)
    public Result findById(@PathVariable String labelId){
        Label label=labelService.findById(labelId);
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",label);
    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.PUT)
    public Result update(@PathVariable String labelId,@RequestBody Label label){
        label.setId(labelId);
        labelService.updateLabel(label);
        return new Result(true,StatusCode.SUCCESS.getCode(),"修改成功");
    }

    @RequestMapping(method = RequestMethod.POST)
    public Result add(@RequestBody Label label){
        labelService.addLabel(label);
        return new Result(true,StatusCode.SUCCESS.getCode(),"添加成功");
    }

    @RequestMapping(value = "/{labelId}",method = RequestMethod.DELETE)
    public Result deleteById(@PathVariable String labelId){
        labelService.delLabelById(labelId);
        return new Result(true, StatusCode.SUCCESS.getCode(),"删除成功");
    }

    @RequestMapping(value = "/search",method = RequestMethod.POST)
    public Result findSearch(@RequestBody Label label){
        List<Label> labelList=labelService.findSearch(label);
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",labelList);
    }

    @RequestMapping(value = "/search/{page}/{size}",method = RequestMethod.POST)
    public Result pageQuery(@RequestBody Label label,@PathVariable int page,@PathVariable int size){
        Page<Label> pageData=labelService.pageQuery(label,page,size);
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",new PageResult<Label>(pageData.getTotalElements(),pageData.getContent()));
    }




}
