package tech.zetapioneers.loan_application.concreteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.dto.UpdateUserRequest;
import tech.zetapioneers.loan_application.entities.User;
import tech.zetapioneers.loan_application.enums.Role;
import tech.zetapioneers.loan_application.exceptions.NotAllowedException;
import tech.zetapioneers.loan_application.exceptions.UserNotFoundException;
import tech.zetapioneers.loan_application.repositories.UserRepository;
import tech.zetapioneers.loan_application.services.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        Optional<User> user =  userRepository.findById(id);
        if(user.isEmpty()){
            throw new UserNotFoundException("No user with id " + id + " found");
        }
        return user;
    }

    @Override
    public Optional<User> updateUserById(Long id, UpdateUserRequest body) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currId = Long.parseLong(authentication.getPrincipal().toString());

        Optional<User> requestInitiator = getUserById(currId);
        if(requestInitiator.isEmpty()){
            throw new UserNotFoundException("The request initiator doesn't exists in db");
        }
        else if(requestInitiator.get().getRole() == Role.ADMIN){
            Optional<User> existingUser = getUserById(id);
            User updatedUser = existingUser.get();
            body.applyUpdates(updatedUser);
            userRepository.save(updatedUser);
            return Optional.of(updatedUser);
        }
        else if(body.getRole() != null){
            throw new NotAllowedException("You are not authorized to update the role");
        }
        else if(requestInitiator.get().getId().equals(currId)) {
            Optional<User> existingUser = getUserById(id);
            User updatedUser = existingUser.get();
            body.applyUpdates(updatedUser);
            userRepository.save(updatedUser);
            return Optional.of(updatedUser);
        }
        else{
            throw new NotAllowedException("You are not authorized to update other users");
        }
    }

    @Override
    public void deleteUserById(Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currId = Long.parseLong(authentication.getPrincipal().toString());

        Optional<User> requestInitiator = getUserById(currId);
        if(requestInitiator.isEmpty()){
            throw new UserNotFoundException("The request initiator doesn't exists in db");
        }
        else if(requestInitiator.get().getRole() == Role.ADMIN){
            getUserById(id);
            userRepository.deleteById(id);
        }
        else if(requestInitiator.get().getId().equals(id)) {
            getUserById(id);
            userRepository.deleteById(id);
        }
        else{
            throw new NotAllowedException("You are not authorized to delete other users");
        }
    }
}
