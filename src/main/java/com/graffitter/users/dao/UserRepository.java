package com.graffitter.users.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.graffitter.users.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public Optional<User> findByUsername(String username);

    @Transactional
    public List<User> deleteByUsername(String username);
}
