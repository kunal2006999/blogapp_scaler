package com.example.blogapp.users;


import com.example.blogapp.common.dtos.ErrorResponse;
import com.example.blogapp.users.dtos.CreateUserRequest;
import com.example.blogapp.users.dtos.CreateUserResponse;
import com.example.blogapp.users.dtos.LoginUserRequest;
import org.apache.catalina.User;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UsersController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("")
    ResponseEntity<CreateUserResponse> signupUser(@RequestBody CreateUserRequest request) {
        UserEntity savedUser = userService.createUser(request);
        URI savedUserUri = URI.create("/users/" + savedUser.getId());

        return ResponseEntity.created(savedUserUri).body(modelMapper.map(savedUser, CreateUserResponse.class));
    }

    @PostMapping("/login")
    ResponseEntity<CreateUserResponse> loginUser(@RequestBody LoginUserRequest request) {
        UserEntity savedUser = userService.loginUser(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(modelMapper.map(savedUser, CreateUserResponse.class));

    }

    @ExceptionHandler({
            UserService.UserNotFoundException.class
    })
    ResponseEntity<ErrorResponse> handleUserNotFoundException(Exception ex){
        String message;
        HttpStatus status;

        if(ex instanceof UserService.UserNotFoundException) {
            message = ex.getMessage();
            status = HttpStatus.NOT_FOUND;
        } else {
            message = "something went wrong";
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ErrorResponse response = ErrorResponse.builder()
                .message(message)
                .build();

        return ResponseEntity.status(status).body(response);
    }
}
