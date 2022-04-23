package com.graffitter.users.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.graffitter.users.dao.FollowingRepository;
import com.graffitter.users.dao.UserRepository;
import com.graffitter.users.dto.UserDTO;
import com.graffitter.users.entity.User;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    FollowingRepository followingRepository;

    // @MockBean
    // LibraryService libraryService;

    @Captor
    ArgumentCaptor<User> captor;

    @Test
    public void addUserTestSuccess() {
        User user = buildUser();
        // when(libraryService.buildId(book.getIsbn(),
        // book.getAisle())).thenReturn(book.getId());
        // when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(false);
        when(userRepository.save(any())).thenReturn(user);

        ResponseEntity<UserDTO> response = userService.addUser(user);

        assertEquals(response.getStatusCode(), HttpStatus.CREATED);

        /*
         * AddResponse ad = (AddResponse) response.getBody();
         * ad.getId();
         * assertEquals(book.getId(), ad.getId());
         * assertEquals("Success Book is Added", ad.getMsg());
         * verify(userRepository, new Times(1)).save(captor.capture());
         * Book savedBook = captor.getValue();
         * assertTrue(book.equals(savedBook));
         */
    }

    /*
     * @Test
     * public void addBookTestAlreadyExists() {
     * Book book = buildBook();
     * when(libraryService.buildId(book.getIsbn(),
     * book.getAisle())).thenReturn(book.getId());
     * when(libraryService.checkBookAlreadyExist(book.getId())).thenReturn(true);
     * ResponseEntity response = userService.addBookImplementation(book);
     * assertEquals(response.getStatusCode(), HttpStatus.ACCEPTED);
     * AddResponse ad = (AddResponse) response.getBody();
     * ad.getId();
     * assertEquals(book.getId(), ad.getId());
     * assertEquals("Book already exist", ad.getMsg());
     * verify(userRepository, new Times(0)).save(any());
     * }
     */

    public User buildUser() {
        User user = new User();
        user.setUsername("mruser");
        user.setEmail("user@gmail.com");
        return user;
    }
}
