package com.graffitter.users.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.graffitter.users.dao.FollowingRepository;
import com.graffitter.users.dao.UserRepository;
import com.graffitter.users.dto.UserDTO;
import com.graffitter.users.entity.Following;
import com.graffitter.users.entity.User;
import com.graffitter.users.exception.NotAllowed;
import com.graffitter.users.exception.ResourceAlreadyExists;
import com.graffitter.users.exception.ResourceDoesNotExist;
import com.graffitter.users.config.Configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@Service
public class UserService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private FollowingRepository followingRepo;
    // TODO: use configuration
    @Autowired
    private Configuration configuration;

    private UserDTO MakeUserDTO(String username, User user) {
        String otherUsername = user.getUsername();
        ModelMapper userMapper = new ModelMapper();
        if (!otherUsername.equals(username)) {
            userMapper.addMappings(new PropertyMap<User, UserDTO>() {
                @Override
                protected void configure() {
                    skip(destination.getEmail());
                }
            });
        }
        UserDTO userDTO = userMapper.map(user, UserDTO.class);
        boolean isFollowed = followingRepo.findByFollowerAndFollowee(username, user.getUsername()).isPresent();
        if (isFollowed) {
            userDTO
                    .add(linkTo(methodOn(UserService.class).getUser(otherUsername, username)).withSelfRel()
                            .withType("GET"))
                    .add(linkTo(methodOn(UserService.class).getFollowes(otherUsername, username)).withRel(
                            "followees").withType("GET"))
                    .add(linkTo(methodOn(UserService.class).getFollowers(otherUsername, username)).withRel(
                            "followers")
                            .withType("GET"))
                    .add(linkTo(methodOn(UserService.class).removeFollowee(username,
                            otherUsername)).withRel("unfollow")
                            .withType("DELETE"));
        } else if (!username.equals(otherUsername)) {
            userDTO
                    .add(linkTo(methodOn(UserService.class).getUser(otherUsername, username)).withSelfRel()
                            .withType("GET"))
                    .add(linkTo(methodOn(UserService.class).getFollowes(otherUsername, username)).withRel(
                            "followees").withType("GET"))
                    .add(linkTo(methodOn(UserService.class).getFollowers(otherUsername, username)).withRel(
                            "followers")
                            .withType("GET"))
                    .add(linkTo(methodOn(UserService.class).addFollowee(username,
                            otherUsername)).withRel("follow")
                            .withType("PUT"));
        } else if (userRepo.findByUsername(username).isPresent()) {
            userDTO
                    .add(linkTo(methodOn(UserService.class).getUser(username, username)).withSelfRel().withType("GET"))
                    .add(linkTo(methodOn(UserService.class).updateUser(username, null)).withSelfRel().withType("PUT"))
                    .add(linkTo(methodOn(UserService.class).deleteUser(username)).withSelfRel()
                            .withType("DELETE"))
                    .add(linkTo(methodOn(UserService.class).getFollowes(username, username)).withRel(
                            "followees").withType("GET"))
                    .add(linkTo(methodOn(UserService.class).getFollowers(username, username)).withRel(
                            "followers")
                            .withType("GET"));
        } else {
            userDTO.add(linkTo(methodOn(UserService.class).addUser(null)).withRel("create")
                    .withType("POST"));
        }
        return userDTO;
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> addUser(@Valid @RequestBody User user) {
        if (userRepo.findByUsername(user.getUsername()).isEmpty()) {
            user = userRepo.save(user);
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{username}")
                    .buildAndExpand(user.getUsername()).toUri();
            return ResponseEntity.created(location).body(MakeUserDTO(user.getUsername(),
                    user));
        }
        throw new ResourceAlreadyExists("Username " + user.getUsername() +
                " already exists.");
    }

    @GetMapping("/users/{requestedUsername}")
    public UserDTO getUser(@PathVariable String requestedUsername, @RequestParam String username) {
        Optional<User> user = userRepo.findByUsername(requestedUsername);
        if (user.isEmpty()) {
            throw new ResourceDoesNotExist("Username " + requestedUsername + " does not exist.");
        } else if (userRepo.findByUsername(username).isEmpty()) {
            throw new NotAllowed("Username " + username + " does not exist.");
        }
        return MakeUserDTO(username, user.get());
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @Valid @RequestBody User user) {
        if (userRepo.findByUsername(username).isPresent()) {
            user = userRepo.save(user);
            return ResponseEntity.ok(MakeUserDTO(username, user));
        }
        throw new ResourceDoesNotExist("Username " + username + " does not exist.");
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<Void> deleteUser(@PathVariable String username) {
        if (userRepo.findByUsername(username).isPresent()) {
            List<UserDTO> deletedUsers = userRepo.deleteByUsername(username).stream()
                    .map(user -> MakeUserDTO(username, user))
                    .collect(Collectors.toList());
            if (deletedUsers != null && !deletedUsers.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
        }
        throw new ResourceDoesNotExist("Username " + username + " does not exist.");
    }

    @GetMapping("/users")
    public Page<UserDTO> getUsers(@RequestParam String username, @RequestParam int page,
            @RequestParam int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("username"));
        Optional<User> activeUser = userRepo.findByUsername(username);
        if (activeUser.isPresent()) {
            return userRepo.findAll(pageRequest)
                    .map(user -> MakeUserDTO(activeUser.get().getUsername(), user));
        }
        throw new NotAllowed("Username " + username + " does not exist.");
    }

    @GetMapping("/users/{follower}/followees")
    public List<UserDTO> getFollowes(@PathVariable String follower, @RequestParam String username) {
        if (userRepo.findByUsername(follower).isEmpty()) {
            throw new ResourceDoesNotExist("Username " + follower + " does not exist.");
        } else if (userRepo.findByUsername(username).isEmpty()) {
            throw new NotAllowed("Username " + username + " does not exist.");
        } else {
            return followingRepo.findByFollower(follower).stream()
                    .map(f -> f.followee)
                    .map(uname -> userRepo.findById(uname).get())
                    .map(user -> MakeUserDTO(username, user))
                    .collect(Collectors.toList());
        }
    }

    @PutMapping("/users/{follower}/followees")
    public List<UserDTO> addFollowee(@PathVariable String follower, @RequestParam String username) {
        if (userRepo.findByUsername(username).isEmpty()) {
            throw new NotAllowed("User " + username + " does not exist.");
        } else if (userRepo.findByUsername(follower).isEmpty()) {
            throw new ResourceDoesNotExist("User " + follower + " does not exist.");
        } else if (follower.equals(username)) {
            throw new NotAllowed("User can't follow itself");
        } else if (followingRepo.findByFollowerAndFollowee(follower, username).isEmpty()) {
            followingRepo.save(new Following(follower, username));
        }
        return followingRepo.findByFollower(follower).stream()
                .map(f -> f.followee)
                .map(uname -> userRepo.findById(uname).get())
                .map(user -> MakeUserDTO(follower, user))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/users/{follower}/followees")
    public List<UserDTO> removeFollowee(@PathVariable String follower, @RequestParam String username) {
        if (userRepo.findByUsername(follower).isEmpty()) {
            throw new ResourceDoesNotExist("User " + follower + " does not exist.");
        } else if (userRepo.findByUsername(username).isEmpty()) {
            throw new NotAllowed("User " + username + " does not exist.");
        }
        followingRepo.deleteByFollowerAndFollowee(follower, username);
        return followingRepo.findByFollower(follower).stream()
                .map(f -> f.followee)
                .map(uname -> userRepo.findById(uname).get())
                .map(user -> MakeUserDTO(follower, user))
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{followee}/followers")
    public List<UserDTO> getFollowers(@PathVariable String followee, @RequestParam String username) {
        if (userRepo.findByUsername(followee).isEmpty()) {
            throw new ResourceDoesNotExist("Username " + followee + " does not exist.");
        } else if (userRepo.findByUsername(username).isEmpty()) {
            throw new NotAllowed("Username " + username + " does not exist.");
        } else {
            return followingRepo.findByFollowee(followee).stream()
                    .map(f -> f.follower)
                    .map(uname -> userRepo.findById(uname).get())
                    .map(user -> MakeUserDTO(username, user))
                    .collect(Collectors.toList());
        }
    }
}
