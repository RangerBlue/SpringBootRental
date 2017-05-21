package com.app.km.controller;

import com.app.km.entity.UsersEntity;
import com.app.km.request.UserRequest;
import com.app.km.respository.RoleRepository;
import com.app.km.respository.UsersRepository;
import com.app.km.util.CustomErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Kamil-PC on 17.05.2017.
 */

@RestController(value = "users")
@RequestMapping("api/users")
public class UsersController {
    private UsersRepository usersRepository;
    private RoleRepository roleRepository;
    private final int ROLE_USER = 2;
    private final int ROLE_ADMIN = 1;

    @Autowired
    public UsersController(UsersRepository usersRepository, RoleRepository roleRepository) {
        this.usersRepository = usersRepository;
        this.roleRepository = roleRepository;
    }


    //select *
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UsersEntity>> findAllUsers() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (usersRepository.findByUsername(auth.getName()).getRoleEntity().getId() != ROLE_ADMIN)
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        List<UsersEntity> users = usersRepository.findAll();
        if (users.isEmpty())
            return new ResponseEntity(new CustomErrorType("No users found"), HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity(users, HttpStatus.OK);
    }

    //select where id
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getUserWhereId(@PathVariable int id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (usersRepository.findByUsername(auth.getName()).getIdusers() != id)
            return new ResponseEntity(HttpStatus.FORBIDDEN);
        UsersEntity user = usersRepository.findOne(id);
        if (user == null)
            return new ResponseEntity(new CustomErrorType("User with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(user, HttpStatus.OK);
    }

    //insert
    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity<?> addUsers(@RequestBody UserRequest addUserRequest) {
        if (usersRepository.findByUsername(addUserRequest.getUsername()) != null)
            return new ResponseEntity(new CustomErrorType("Unable to create user, user with username " + addUserRequest.getUsername() + " already exists"), HttpStatus.CONFLICT);
        UsersEntity user = new UsersEntity();
        user.setName(addUserRequest.getName());
        user.setLastname(addUserRequest.getLastname());
        user.setUsername(addUserRequest.getUsername());
        user.setPassword(addUserRequest.getPassword());
        user.setEmail(addUserRequest.getEmail());
        user.setEnabled(true);
        user.setRoleEntity(roleRepository.findOne(ROLE_USER));
        usersRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    //update
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public ResponseEntity<?> updateUser(@PathVariable int id, @RequestBody UserRequest user) {
        UsersEntity currentUser = usersRepository.findOne(id);
        if (currentUser == null)
            return new ResponseEntity(new CustomErrorType("Unable to update user with id " + id), HttpStatus.NOT_FOUND);
        else if (usersRepository.findByUsername(user.getUsername()) != null)
            return new ResponseEntity(new CustomErrorType("Unable to update user, user with username " + user.getUsername() + " already exists"), HttpStatus.CONFLICT);
        else {
            currentUser.setName(user.getName());
            currentUser.setLastname(user.getLastname());
            currentUser.setUsername(user.getUsername());
            usersRepository.save(currentUser);
            return new ResponseEntity<>(currentUser, HttpStatus.OK);
        }

    }

    //delete
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteUser(@PathVariable int id) {
        UsersEntity user = usersRepository.findOne(id);
        if (user == null)
            return new ResponseEntity(new CustomErrorType("Unable to delete user with id " + id), HttpStatus.NOT_FOUND);
        else {
            usersRepository.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

    }

    //delete all
    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteAllUsers() {
        usersRepository.deleteAll();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
