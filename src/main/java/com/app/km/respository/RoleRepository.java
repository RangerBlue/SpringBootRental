package com.app.km.respository;

import com.app.km.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Kamil-PC on 19.05.2017.
 */
public interface RoleRepository extends JpaRepository<RoleEntity,Integer> {
}
