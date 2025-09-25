package tech.zetapioneers.loan_application.services;

import tech.zetapioneers.loan_application.dto.UpdateUserRequest;
import tech.zetapioneers.loan_application.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUser();
    Optional<User> getUserById(Long id);
    Optional<User> updateUserById(Long id, UpdateUserRequest user);
    void deleteUserById(Long id);
}
