package com.makersacademy.acebook.repository;

import com.makersacademy.acebook.model.Friend;
import com.makersacademy.acebook.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FriendRepository extends CrudRepository<Friend, Long>{

    List<Friend> findByUser(User user);

    @Query("SELECT f.friend FROM Friend f WHERE f.user = :user")
    List<User> findFriendsByUser(User user);
}
