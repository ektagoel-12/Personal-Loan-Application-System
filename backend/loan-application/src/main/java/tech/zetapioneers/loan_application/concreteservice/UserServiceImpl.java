package tech.zetapioneers.loan_application.concreteservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.zetapioneers.loan_application.DTO.UpdateUserRequest;
import tech.zetapioneers.loan_application.Entities.User;
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
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> updateUserById(Long id, UpdateUserRequest body) {
        Optional<User> existingUser = getUserById(id);
        if(existingUser.isEmpty()){
            throw new RuntimeException("User not found exception");
        }
        User updatedUser = existingUser.get();
        body.applyUpdates(updatedUser);
        userRepository.save(updatedUser);
        return Optional.of(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        if(getUserById(id).isEmpty()) throw new RuntimeException("User not found");
        userRepository.deleteById(id);
    }
}
