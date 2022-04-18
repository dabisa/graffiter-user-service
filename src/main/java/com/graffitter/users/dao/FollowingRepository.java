package com.graffitter.users.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.graffitter.users.entity.Following;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingRepository extends JpaRepository<Following, Long> {
    public Optional<Following> findByFollowerAndFollowee(String follower, String folowee);

    @Transactional
    public long deleteByFollowerAndFollowee(String follower, String folowee);

    public List<Following> findByFollower(String follower);

    public List<Following> findByFollowee(String followee);
}
