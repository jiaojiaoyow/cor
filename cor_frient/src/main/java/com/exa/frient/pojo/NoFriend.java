package com.exa.frient.pojo;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "tb_nofriend")
@IdClass(NoFriend.class)
public class NoFriend {

    @Id
    private String userId;

    @Id
    private String friendId;


}
