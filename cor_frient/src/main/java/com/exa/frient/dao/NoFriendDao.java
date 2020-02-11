package com.exa.frient.dao;


import com.exa.frient.pojo.NoFriend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoFriendDao extends JpaRepository<NoFriend, String> {
    NoFriend findByUserIdAndFriendId(String userId, String friendId);
}
