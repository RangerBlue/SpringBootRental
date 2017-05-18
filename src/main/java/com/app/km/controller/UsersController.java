package com.app.km.controller;

import com.app.km.entity.UsersEntity;
import com.app.km.respository.UsersRepository;
import com.app.km.util.CustomErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Kamil-PC on 17.05.2017.
 */

@RestController(value="users")
@RequestMapping("api/users")
public class UsersController {
    private UsersRepository usersRepository;

    @Autowired
    public UsersController(UsersRepository usersRepository)
    {
        this.usersRepository = usersRepository;
    }

    //select *
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UsersEntity>> findAllUsers(){
        List<UsersEntity> users = usersRepository.findAll();
        if(users.isEmpty())
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity(users, HttpStatus.OK);
    }

    //insert
    @RequestMapping(method = RequestMethod.POST)
    public void addUsers(@RequestBody UsersEntity addUserRequest){
        UsersEntity user = new UsersEntity();
        user.setName(addUserRequest.getName());
        user.setLastname(addUserRequest.getLastname());
        user.setUsername(addUserRequest.getUsername());
        usersRepository.save(user);
    }

    //select where id
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWhereId(@PathVariable int id){
        UsersEntity user = usersRepository.findOne(id);
        if(user == null)
            return new ResponseEntity(new CustomErrorType("User with id "+id+" not found"),
                    HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //update
    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UsersEntity user){
        UsersEntity currentUser = usersRepository.findOne(id);
        if(currentUser == null)
            return new ResponseEntity(new CustomErrorType("Unable to update user with id "+id), HttpStatus.NOT_FOUND);
        else{
            currentUser.setName(user.getName());
            currentUser.setLastname(user.getLastname());
            currentUser.setUsername(user.getUsername());
            usersRepository.save(currentUser);
            return new ResponseEntity<>(currentUser, HttpStatus.OK);
        }

    }
    //delete
    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id){
        UsersEntity user = usersRepository.findOne(id);
        if(user == null)
            return new ResponseEntity(new CustomErrorType("Unable to delete user with id "+id), HttpStatus.NOT_FOUND);
        else{
            usersRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    //delete all
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllUsers(){
            usersRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
