package com.exa.spit.service;

import com.exa.spit.dao.SpitDao;
import com.exa.spit.pojo.Spit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SpitService {

    @Autowired
    private SpitDao spitDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private MongoTemplate mongoTemplate;

    /***
     * 查询全部数据
     * @return
     */
    public List<Spit> findAll(){
        return spitDao.findAll();
    }

    /**
     * 根据ID查询数据
     * @param id
     * @return
     */
    public Spit findById(String id){
        return spitDao.findById(id).get();
    }

    /**
     * 增加吐槽
     * @param spit
     */
//    public void add(Spit spit){
//        spit.set_id(idWorker.nextId()+"");
//        spitDao.save(spit);
//    }

    /**
     * 修改吐槽
     * @param spit
     */
    public void update(Spit spit){
        spitDao.save(spit);
    }

    /**
     *  删除吐槽
     * @param id
     */
    public void deleteById(String id){
        spitDao.deleteById(id);
    }

    /**
     * 根据上级ID查询吐槽列表
     *
     * @param parentId
     * @param page
     * @param size
     * @return
     */
    public Page<Spit> findByParentid(String parentId, int page, int size) {

        PageRequest pageRequest = PageRequest.of(page - 1, size);
        return spitDao.findByParentid(parentId, pageRequest);
    }



    /**
     * 吐槽点赞功能
     * 用原生的增加效率，不用先查询该值是否为空
     *
     * @param id
     */
    public void updateThumbup(String id) {
        //创建查询对象
        Query query = new Query();
        //封装查询条件
        query.addCriteria(Criteria.where("_id").is(id));
        //创建修改对象
        Update update = new Update();
        //设置修改内容
        update.inc("thumbup", 1);
        //执行修改操作
        mongoTemplate.updateFirst(query, update, "spit");
    }

    /**
     * 发布吐槽
     *
     * @param spit
     */
    public void add(Spit spit) {
        spit.set_id(idWorker.nextId() + "");
        spit.setPublishtime(new Date());
        spit.setVisits(0);  //浏览量
        spit.setShare(0);   //分享数
        spit.setThumbup(0); //点赞数
        spit.setComment(0); //回复数
        spit.setState("1"); //状态
        //如果当前添加的吐槽有父节点，那么父节点的吐槽回复加1
        if(spit.getParentid() != null && !"".equals(spit.getParentid())){
            //如果存在上级，设置上级回复数 +1
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(spit.getParentid()));
            Update update = new Update();
            update.inc("comment", 1);
            mongoTemplate.updateFirst(query, update, "spit");
        }
        spitDao.save(spit);
    }
}
