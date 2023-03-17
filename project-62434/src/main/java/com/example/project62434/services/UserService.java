package com.example.project62434.services;

import com.example.project62434.dto.UserDto;
import com.example.project62434.models.Task;
import com.example.project62434.models.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService {
    User getUserById(Long userId);

    User getUserByUsername(String username);

    List<User> getUsers();

    @Transactional
    List<UserDto> getUserDTOs();

    User createUser(User userDto);

    User createUser(String firstName, String lastName, String username, String password, String email, String roles);

    User updateUserById(Long userId, UserDto userDto);

    void deleteUserById(Long userId);

    User updateUserById(Long id, String firstName, String lastName, String username, String password, String email, String roles);

    User updateUserByIdWithPassword(Long id, String firstName, String lastName,String contacts, String username,String oldPassword, String password, String email, String roles);

    User getOwnerByUserName(String productOwner);

    User getDeveloperByUsername(String developer);

    void updateContacts(User user, String contacts);

    List<User> getUsersByRole(String role);

    void updateAssignedTasks(Long id, Task task);

    @Transactional
    User updateModifiedTime(Long id);
}
