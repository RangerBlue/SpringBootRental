package com.app.km.respository;

import com.app.km.entity.RentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Kamil-PC on 18.05.2017.
 */
public interface RentRepository extends JpaRepository<RentEntity, Integer> {
}
