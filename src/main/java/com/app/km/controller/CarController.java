package com.app.km.controller;

import com.app.km.entity.CarEntity;
import com.app.km.respository.CarRepository;
import com.app.km.util.CustomErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Kamil-PC on 18.05.2017.
 */

@RestController("car")
@RequestMapping("api/car")
public class CarController {
    private CarRepository carRepository;

    @Autowired
    public CarController(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    //select *
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<CarEntity>> findAllCars(){
        List<CarEntity> cars = carRepository.findAll();
        if(cars.isEmpty())
            return new ResponseEntity(new CustomErrorType("No cars found"),HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity(cars, HttpStatus.OK);
    }

    //select where id
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getCarWhereId(@PathVariable int id){
        CarEntity user = carRepository.findOne(id);
        if(user == null)
            return new ResponseEntity(new CustomErrorType("Car with id "+id+" not found"),HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //insert
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> addCar(@RequestBody CarEntity addCarRequest){
        CarEntity car = new CarEntity();
        car.setBrand(addCarRequest.getBrand());
        car.setModel(addCarRequest.getModel());
        car.setAvailable(addCarRequest.isAvailable());
        carRepository.save(car);
        return new ResponseEntity<>(car, HttpStatus.CREATED);
    }

    //update
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateCar(@PathVariable int id, @RequestBody CarEntity car){
        CarEntity currentCar = carRepository.findOne(id);
        if(currentCar == null)
            return new ResponseEntity(new CustomErrorType("Unable to update car with id "+id), HttpStatus.NOT_FOUND);
        else{
            currentCar.setBrand(car.getBrand());
            currentCar.setModel(car.getModel());
            currentCar.setAvailable(car.isAvailable());
            carRepository.save(currentCar);
            return new ResponseEntity<>(currentCar, HttpStatus.OK);
        }
    }

    //delete
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteCar(@PathVariable int id){
        CarEntity car = carRepository.findOne(id);
        if(car == null)
            return new ResponseEntity(new CustomErrorType("Unable to delete car with id "+id), HttpStatus.NOT_FOUND);
        else{
            carRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }

    //delete all
    @RequestMapping( method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllCars(){
        carRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
