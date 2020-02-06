package com.exa.database.service;

import com.exa.database.dao.LabelDao;
import com.exa.database.pojo.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.IdWorker;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LabelService {
    @Autowired
    private LabelDao labelDao;

    @Autowired
    private IdWorker idWorker;


    public List<Label> findAll(){
        return labelDao.findAll();
    }

    public Label findById(String id){
        return labelDao.findById(id).get();
    }

    /**
     * 添加标签
     * @param label
     */
    public void addLabel(Label label){
        label.setId( idWorker.nextId()+ "");
        labelDao.save(label);
    }

    /**
     * 更新标签
     * @param label
     */
    public void updateLabel(Label label){
        labelDao.save(label);
    }

    /**
     * 根据id删除标签
     * @param id
     */
    public void delLabelById(String id){
        labelDao.deleteById(id);
    }

    /**
     *
     */
    public List<Label> findSearch(Label label){
        return labelDao.findAll(new Specification<Label>() {
            /**
             *
             * @param root 根对象，也就是要把条件封装到那个对象中，eg: where
             * @param criteriaQuery 封装的都是查询关键字，比如group by order by
             * @param criteriaBuilder  用来封装条件对象的
             * @return 如果直接返回null，表示不需要任何条件
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                ArrayList<Predicate> list=new ArrayList();
                if(label.getLabelname()!=null&&"".equals(label.getLabelname())){
                    Predicate predicate1=criteriaBuilder.like(root.get("labelname").as(String.class),"%"+label.getLabelname()+"%"); //相当于 where labelname like "%""%"
                    list.add(predicate1);
                }

                if(label.getState()!=null&&"".equals(label.getState())){
                    Predicate predicate1=criteriaBuilder.equal(root.get("state").as(String.class),label.getState());
                    list.add(predicate1);
                }

                //new一个数组作为最终返回值的条件
                Predicate[] parr=new Predicate[list.size()];
                //把list直接转化为数组
                parr=list.toArray(parr);
                return criteriaBuilder.and(parr);
            }
        });
    }

    public Page<Label> pageQuery(Label label, int page, int size) {
        //因为默认从0开始，所以需要-1，变成从1开始
        Pageable pageable = PageRequest.of(page-1,size);
        return labelDao.findAll(new Specification<Label>() {
            /**
             *
             * @param root 根对象，也就是要把条件封装到那个对象中，eg: where
             * @param criteriaQuery 封装的都是查询关键字，比如group by order by
             * @param criteriaBuilder  用来封装条件对象的
             * @return 如果直接返回null，表示不需要任何条件
             */
            @Override
            public Predicate toPredicate(Root<Label> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                ArrayList<Predicate> list=new ArrayList();
                if(label.getLabelname()!=null&&"".equals(label.getLabelname())){
                    Predicate predicate1=criteriaBuilder.like(root.get("labelname").as(String.class),"%"+label.getLabelname()+"%"); //相当于 where labelname like "%""%"
                    list.add(predicate1);
                }

                if(label.getState()!=null&&"".equals(label.getState())){
                    Predicate predicate1=criteriaBuilder.equal(root.get("state").as(String.class),label.getState());
                    list.add(predicate1);
                }

                //new一个数组作为最终返回值的条件
                Predicate[] parr=new Predicate[list.size()];
                //把list直接转化为数组
                parr=list.toArray(parr);
                return criteriaBuilder.and(parr);
            }
        },pageable);
    }
}
