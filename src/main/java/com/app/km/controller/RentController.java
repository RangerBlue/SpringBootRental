package com.app.km.controller;

import com.app.km.entity.RentEntity;
import com.app.km.request.RentRequest;
import com.app.km.respository.CarRepository;
import com.app.km.respository.RentRepository;
import com.app.km.respository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Kamil-PC on 18.05.2017.
 */

@RestController(value="rent")
@RequestMapping("api/rent")
public class RentController {
    private RentRepository rentRepository;
    private UsersRepository userRepository;
    private CarRepository carRepository;

    @Autowired
    public RentController(RentRepository rentRepository, UsersRepository userRepository, CarRepository carRepository) {
        this.rentRepository = rentRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
    }

    //select *
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<RentEntity>> findAllRents(){
        List<RentEntity> rents = rentRepository.findAll();
        if(rents.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity(rents, HttpStatus.OK);
    }

    //insert
    @RequestMapping(method = RequestMethod.POST)
    public void addUsers(@RequestBody RentRequest addRentRequest){
        RentEntity rent = new RentEntity();
        rent.setStart(new Timestamp(System.currentTimeMillis()));
        rent.setUserEntity(userRepository.findOne(addRentRequest.getUsers_iduser()));
        rent.setCarEntity(carRepository.findOne(addRentRequest.getCar_idcar()));
        rentRepository.save(rent);
    }

}
