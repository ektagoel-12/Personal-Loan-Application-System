package tech.zetapioneers.loan_application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.zetapioneers.loan_application.dto.UpdateUserRequest;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.concreteservice.UserServiceImpl;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    UserServiceImpl userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers(){
        return new ResponseEntity<>(userService.getAllUser(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id){
        Optional<User> targetUser = userService.getUserById(id);
        if(targetUser.isEmpty()){
            return new ResponseEntity<>("No user found with id : " + id,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(targetUser.get(),HttpStatus.OK);
    }

    @GetMapping("/paid/{id}")
    public ResponseEntity<?> getTotalPaidByUserById(@PathVariable Long id){
        Double totalPaid = userService.getTotalPaidAmountById(id);
        totalPaid = Math.round(totalPaid * 100.0)/100.0;
        return new ResponseEntity<>(totalPaid,HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUserById(@PathVariable Long id,@RequestBody UpdateUserRequest body){
        Optional<User> targetUser = userService.updateUserById(id,body);
        if(targetUser.isEmpty()){
            return new ResponseEntity<>("No user found with id : " + id,HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(targetUser.get(),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id){
        userService.deleteUserById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

