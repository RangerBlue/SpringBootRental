package com.app.km.controller;

import com.app.km.entity.CarEntity;
import com.app.km.entity.RentEntity;
import com.app.km.request.RentRequest;
import com.app.km.respository.CarRepository;
import com.app.km.respository.RentRepository;
import com.app.km.respository.UsersRepository;
import com.app.km.util.CustomErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    //select where id
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getRentWhereId(@PathVariable int id){
        RentEntity user = rentRepository.findOne(id);
        if(user == null)
            return new ResponseEntity(new CustomErrorType("Rent with id "+id+" not found"),
                    HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //update
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateRent(@PathVariable int id, @RequestBody RentRequest rent){
        RentEntity currentRent = rentRepository.findOne(id);
        if(currentRent == null)
            return new ResponseEntity(new CustomErrorType("Unable to update rent with id "+id), HttpStatus.NOT_FOUND);
        else{
            CarEntity car = carRepository.findOne(rent.getCar_idcar());
            if(!car.isAvailable())
                return new ResponseEntity(new CustomErrorType("Unable to create rent, car with id "+rent.getCar_idcar()+" isnt't available"), HttpStatus.CONFLICT);
            CarEntity currentCar = currentRent.getCarEntity();
            currentCar.setAvailable(true);
            car.setAvailable(false);
            carRepository.save(car);
            carRepository.save(currentCar);
            currentRent.setUserEntity(userRepository.findOne(rent.getUsers_iduser()));
            currentRent.setCarEntity(car);
            rentRepository.save(currentRent);
            return new ResponseEntity<>(currentRent, HttpStatus.OK);
        }

    }

    //update, end of rent
    @RequestMapping(value="finish/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> finishRent(@PathVariable int id){
        RentEntity currentRent = rentRepository.findOne(id);
        if(currentRent == null)
            return new ResponseEntity(new CustomErrorType("Unable to finish rent with id "+id), HttpStatus.NOT_FOUND);
        else{
            currentRent.setEnd(new Timestamp(System.currentTimeMillis()));
            CarEntity car = carRepository.findOne(currentRent.getCarEntity().getIdcar());
            car.setAvailable(true);
            carRepository.save(car);
            rentRepository.save(currentRent);
            return new ResponseEntity<>(currentRent, HttpStatus.OK);
        }

    }

    //insert
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addRent(@RequestBody RentRequest addRentRequest){
        RentEntity rent = new RentEntity();
        rent.setStart(new Timestamp(System.currentTimeMillis()));
        rent.setUserEntity(userRepository.findOne(addRentRequest.getUsers_iduser()));
        CarEntity car = carRepository.findOne(addRentRequest.getCar_idcar());
        if(!car.isAvailable())
            return new ResponseEntity(new CustomErrorType("Unable to create rent, car with id "+addRentRequest.getCar_idcar()+" isnt't available"), HttpStatus.CONFLICT);
        rent.setCarEntity(car);
        car.setAvailable(false);
        carRepository.save(car);
        rentRepository.save(rent);
        return new ResponseEntity<>(rent, HttpStatus.CREATED);
    }

    //delete
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRent(@PathVariable int id){
        RentEntity rent = rentRepository.findOne(id);
        if(rent == null)
            return new ResponseEntity(new CustomErrorType("Unable to delete rent with id "+id), HttpStatus.NOT_FOUND);
        else{
            rentRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    //delete all
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllRents(){
        rentRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
