package com.exa.spit.pojo;


import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

@Data
public class Spit implements Serializable {
    @Id
    private String _id;//主键
    private String content;//吐槽内容
    private Date publishtime;//发布日期
    private String userid;//发布人ID
    private String nickname;//昵称
    private Integer visits;//浏览量
    private Integer thumbup;//点赞数
    private Integer share;//分享数
    private Integer comment;//回复数
    private String state;//状态
    private String parentid;//上级ID

    public Spit() {
    }
}
