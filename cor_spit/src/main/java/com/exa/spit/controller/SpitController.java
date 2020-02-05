package com.exa.spit.controller;

import com.exa.spit.pojo.Spit;
import com.exa.spit.service.SpitService;
import entity.PageResult;
import entity.Result;
import entity.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin //跨域注解
@RequestMapping("/spit")
public class SpitController {

    @Autowired
    private SpitService spitService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部记录
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public Result findAll(){
        List<Spit> list = spitService.findAll();
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",list);
    }

    /**
     *  根据ID查询吐槽
     * @param id
     * @return
     */
    @RequestMapping( value="/{id}" ,method = RequestMethod.GET)
    public Result findById(@PathVariable String id){
        Spit spit = spitService.findById(id);
        return new Result(true, StatusCode.SUCCESS.getCode(),"查询成功",spit);
}

    /**
     * 增加吐槽
     * @param spit
     * @return
     */
    @RequestMapping( method = RequestMethod.POST)
    public  Result add( @RequestBody Spit spit ){
        spitService.add(spit);
        return new Result(true,StatusCode.SUCCESS.getCode(),"增加成功");
    }

    /**
     * 修改吐槽
     * @param spit
     * @param id
     * @return
     */
    @RequestMapping( value ="/{id}" ,method = RequestMethod.PUT)
    public  Result update( @RequestBody Spit spit ,@PathVariable String id){
        spit.set_id(id);
        spitService.update(spit);
        return new Result(true,StatusCode.SUCCESS.getCode(),"修改成功");
    }

    /**
     *  删除吐槽
     * @param id
     * @return
     */
    @RequestMapping( value ="/{id}" ,method = RequestMethod.DELETE)
    public  Result deleteById(@PathVariable  String id){
        spitService.deleteById(id);
        return new Result(true,StatusCode.SUCCESS.getCode(),"删除成功");
    }

    /**
     * 根据上级ID查询吐槽列表
     * @param parentId
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/comment/{parentId}/{page}/{size}", method = RequestMethod.GET)
    public Result findByParentId(@PathVariable String parentId, @PathVariable int page, @PathVariable int size) {
        Page<Spit> pageList = spitService.findByParentid(parentId, page, size);
        PageResult<Spit> pageResult = new PageResult<>(pageList.getTotalElements(), pageList.getContent());
        return new Result(true, StatusCode.SUCCESS.getCode(), "查询成功", pageResult);
    }

    /**
     * 吐槽点赞
     * @param id
     * @return
     */
    @RequestMapping(value = "/thumbup/{id}", method = RequestMethod.PUT)
    public Result updateThumbup(@PathVariable String id){
        //先假定用一个作为用户id
        String userid="1";
        //先判断当前用户是否已经点赞（这种判断用户的，由于我们的用户在缓存中已经存在，最好在缓存中判断）
        if(redisTemplate.opsForValue().get("thumbup"+userid)!=null){
         return new Result(false,StatusCode.OPERATION_FAIL.getCode(),"不能重复点赞");
        }
        spitService.updateThumbup(id);

        return new Result(true, StatusCode.SUCCESS.getCode(), "点赞成功");
    }
}