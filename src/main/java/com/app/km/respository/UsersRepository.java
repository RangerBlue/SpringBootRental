package com.app.km.respository;

import com.app.km.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Kamil-PC on 17.05.2017.
 */
@Repository
public interface UsersRepository extends JpaRepository<UsersEntity,Integer>{

    UsersEntity findByUsername(String username);
}
