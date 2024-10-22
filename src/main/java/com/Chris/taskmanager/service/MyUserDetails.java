package com.Chris.taskmanager.service;

import com.Chris.taskmanager.models.user.User;
import com.Chris.taskmanager.models.user.UserPrincipal;
import com.Chris.taskmanager.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetails implements UserDetailsService {

    private final UserRepository userRepo;

    public MyUserDetails(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User doesn't exist");
        }
        return new UserPrincipal(user);
    }
}
