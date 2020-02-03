package com.exa.qa.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.exa.qa.pojo.Problem;
import org.springframework.data.jpa.repository.Query;
import sun.awt.SunHints;

import java.util.List;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface ProblemDao extends JpaRepository<Problem,String>,JpaSpecificationExecutor<Problem>{

    /**
     * 查询
     * @param labelid
     * @param pageable
     * @return
     */
    @Query(value = "select * from tb_problem , tb_pl " +
            "where id=problemid AND labelid=? ORDER BY replytime DESC",nativeQuery = true)
    public Page<Problem> newlist(String labelid, Pageable pageable);




    @Query(value = "select * from tb_problem , tb_pl " +
            "where id=problemid AND labelid=? ORDER BY reply DESC",nativeQuery = true)
    public Page<Problem> hotlist(String labelid, Pageable pageable);



    @Query(value = "select * from tb_problem , tb_pl " +
            "where id=problemid AND labelid=? and reply=0 ORDER BY reply DESC",nativeQuery = true)
    public Page<Problem> waitlist(String labelid, Pageable pageable);
}
