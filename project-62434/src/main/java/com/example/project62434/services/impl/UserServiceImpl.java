package com.example.project62434.services.impl;

import com.example.project62434.dto.ChangePasswordDto;
import com.example.project62434.dto.UserDto;
import com.example.project62434.enums.Role;
import com.example.project62434.exceptions.EntityNotFoundException;
import com.example.project62434.exceptions.IncorrectInputException;
import com.example.project62434.exceptions.InvalidEntityDataException;
import com.example.project62434.models.Task;
import com.example.project62434.models.User;
import com.example.project62434.respositories.ProjectRepository;
import com.example.project62434.respositories.UserRepository;
import com.example.project62434.services.UserService;
import com.example.project62434.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User", "id", userId.toString()));
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserByUsername(String username) {
        User user = userRepository.getUserByUsername(username).orElseThrow(() -> new EntityNotFoundException("The user with the name " + username + " has not been found"));
        user.getAssignedTasksForDeveloper().size();
        user.getCompletedProjectResultsForProductOwner().size();
        user.getCompletedTaskResultsForDeveloper().size();
        user.getProjectsForProductOwner().size();
        return user;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getUsers() {
        List<User> result = userRepository.findAll();
        for (User user : result) {
            user.getAssignedTasksForDeveloper().size();
            user.getCompletedProjectResultsForProductOwner().size();
            user.getCompletedTaskResultsForDeveloper().size();
            user.getProjectsForProductOwner().size();
        }
        return result;
    }

    @Override
    @Transactional
    public List<UserDto> getUserDTOs() {
        List<User> users = getUsers();
        List<UserDto> userDtos = new ArrayList<>();
        for (User u : users) {
            userDtos.add(new UserDto(u));
        }
        return userDtos;
    }

    @Override
    @Transactional
    public User createUser(@Valid User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new InvalidEntityDataException(String.format("User with username [%s] already exists!",
                    user.getUsername()));
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User createUser(String firstName, String lastName, String username, String password, String email, String roles) {
        if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty() || username == null || username.isEmpty() || password == null || password.isEmpty() || email == null || email.isEmpty() || roles == null || roles.isEmpty()){
            throw new IncorrectInputException("No empty fields allowed when creating a user");
        }
        Set<Role> rolesSet = extractRoles(roles);
        if (!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[#%@!^&*?|/._\\-=]).{8,15}$")) {
            throw new IncorrectInputException("Wrong password format");
        }
        User user = new User(firstName, lastName, username, password, email, "", rolesSet);
        return createUser(user);
    }

    private Set<Role> extractRoles(String roles) {
        String[] rolesArr = roles.split(",");
        Set<Role> rolesSet = new HashSet<>();
        for (String s : rolesArr) {
            s.trim();
            switch (s) {
                case "DEVELOPER" -> rolesSet.add(Role.DEVELOPER);
                case "ADMIN" -> rolesSet.add(Role.ADMIN);
                case "PRODUCT_OWNER" -> rolesSet.add(Role.PRODUCT_OWNER);
            }
        }
        return rolesSet;
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto changePasswordDto) {
        User user = getUserById(userId);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if (!encoder.matches(changePasswordDto.getOldPassword(), user.getPassword())) {
            throw new InvalidEntityDataException("Old password does not match.");
        }

        String newPassword = changePasswordDto.getNewPassword();
        validatePassword(newPassword);
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new InvalidEntityDataException("Password must be at least 8 characters long.");
        }
        if (!password.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*")) {
            throw new InvalidEntityDataException("Password must contain at least one lowercase letter, one uppercase letter and one digit.");
        }
    }

    @Override
    @Transactional
    public User updateUserById(Long userId, UserDto userDto) {
        User user = getUserById(userId);

        if (userDto.getRole().equals(Role.ADMIN)) {
            User loggedUser = getUserByUsername(jwtUtils.getCurrentUsername());

            if (user.getId().equals(loggedUser.getId())) {
                throw new InvalidEntityDataException(String.format("Role: '%s' is invalid. "
                        + "You can self-register only in 'USER' role.", userDto.getRole()));
            }
        }

        user.setUsername(userDto.getUsername());
        user.setImageUrl(userDto.getImageUrl());
        user.setEmail(userDto.getEmail());

        user.setUsername(userDto.getUsername());
        user.setContacts(userDto.getContacts());
        setImageUrl(userDto.getImageUrl(), user);

        return userRepository.save(user);
    }

    private void setImageUrl(String imageUrl, User user) {
        if (imageUrl == null) {
            imageUrl = "https://cdn5.vectorstock.com/i/1000x1000/45/79/male-avatar-profile-picture-silhouette-light-vector-4684579.jpg";
        }
        user.setImageUrl(imageUrl);
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        User foundUser = getUserById(userId);
        projectRepository.findAll().stream().filter(p -> p.getOwner().getUsername().equals(foundUser.getUsername())).forEach(p -> p.setOwner(null));
        userRepository.delete(foundUser);
    }

    @Override
    @Transactional
    public User updateUserById(Long id, String firstName, String lastName, String username, String password, String email, String roles) {
        if (userRepository.findById(id).isEmpty()) {
            return createUser(firstName, lastName, username, password, email, roles);
        }
        User user = userRepository.findById(id).get();
        if (firstName != null && !firstName.isEmpty()) {
            user.setFirstName(firstName);
        }
        if (lastName != null && !lastName.isEmpty()) {
            user.setLastName(lastName);
        }
        if (username != null && !username.isEmpty()) {
            Set<String> oldContacts = user.getContacts();
            updateContacts(user, "");
            user.setUsername(username);
            if (!oldContacts.isEmpty()) {
                StringBuilder formattedOldContacts = new StringBuilder("");
                oldContacts.stream().forEach(s -> formattedOldContacts.append(s + ","));
                formattedOldContacts.deleteCharAt(formattedOldContacts.length() - 1);
                updateContacts(user, formattedOldContacts.toString());
            }
        }
        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (roles != null && !roles.isEmpty()) {
            user.setRole(extractRoles(roles));
        }
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User updateUserByIdWithPassword(Long id, String firstName, String lastName, String contacts, String username, String oldPassword, String password, String email, String roles) {
        if (password != null && !password.isEmpty() && !passwordEncoder.matches(oldPassword, getUserById(id).getPassword())) {
            throw new IncorrectInputException("Wrong password entered. Cannot change password.");
        }
        User user = updateUserById(id, firstName, lastName, username, password, email, roles);
        updateContacts(user, contacts);
        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        user.getProjectsForProductOwner().size();
        user.getCompletedTaskResultsForDeveloper().size();
        user.getAssignedTasksForDeveloper().size();
        user.getCompletedProjectResultsForProductOwner().size();
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getOwnerByUserName(String productOwner) {
        User owner = getUserByUsername(productOwner);
        if (owner.getRole().contains(Role.PRODUCT_OWNER)) {
            return owner;
        } else {
            throw new IncorrectInputException(productOwner + " does not have the role of PRODUCT_OWNER");
        }
    }

    @Override
    public User getDeveloperByUsername(String developer) {
        User developerInstance = getUserByUsername(developer);
        if (developerInstance.getRole().contains(Role.DEVELOPER)) {
            return developerInstance;
        } else {
            throw new IncorrectInputException(developer + " does not have the role of DEVELOPER");
        }
    }

    @Override
    @Transactional
    public void updateContacts(User user, String contacts) {
        for (String s : user.getContacts()) {
            getUserByUsername(s).getContacts().remove(user.getUsername());
        }
        user.setContacts(new HashSet<>());
        if (contacts != null && !contacts.isEmpty()) {
            String[] individualContacts = contacts.split(",");
            for (String s : individualContacts) {
                s = s.trim();
                User userInContacts = getUserByUsername(s);
                Set<String> contactsOfTheCurrentUserInTheLoop = userInContacts.getContacts();
                if (!contactsOfTheCurrentUserInTheLoop.contains(user.getUsername()) && !s.equals(user.getUsername())) {
                    userInContacts.getContacts().add(user.getUsername());
                }
                userRepository.save(userInContacts);
                if (!user.getUsername().equals(s)) {
                    user.getContacts().add(s);
                }

            }
            List<User> allUsers = userRepository.findAll();
            for (User u : allUsers) {
                if (u.getId() == user.getId()) {
                    continue;
                }
                if (!user.getContacts().contains(u.getUsername())) {
                    u.getContacts().remove(user.getUsername());
                }
            }
        }
    }

    @Override
    @Transactional
    public List<User> getUsersByRole(String role) {
        Role roleEnum = null;
        switch (role) {
            case "DEVELOPER" -> roleEnum = Role.DEVELOPER;
            case "PRODUCT_OWNER" -> roleEnum = Role.PRODUCT_OWNER;
            case "ADMIN" -> roleEnum = Role.ADMIN;
        }
        if (roleEnum == null) {
            throw new IncorrectInputException("Incorrect input on filter criteria on filtering users by role");
        }
        final Role finalRoleEnum = roleEnum;
        List<User> result = new ArrayList<>();
        getUsers().stream().filter(user -> user.getRole().contains(finalRoleEnum)).forEach(user -> {
            user.getAssignedTasksForDeveloper().size();
            user.getCompletedProjectResultsForProductOwner().size();
            user.getCompletedTaskResultsForDeveloper().size();
            user.getProjectsForProductOwner().size();
            result.add(user);
        });
        return result;
    }

    @Override
    @Transactional
    public void updateAssignedTasks(Long id, Task task) {
        User dev = getUserById(id);
        dev.getAssignedTasksForDeveloper().add(task);
        userRepository.save(dev);
    }

    @Override
    @Transactional
    public User updateModifiedTime(Long id) {
        User user = getUserById(id);
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        User user = this.getUserByUsername(username);
        return jwtUtils.getUserDetails(user);
    }
}