package ru.intech.nedochat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.repository.UserRepository;


@Service
public class UsersService implements UserDetailsService {

    private UserRepository userRepository;

    @Autowired
    public UsersService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User add(User user){
        String login = user.getUsername();
        if (userRepository.existsByUsername(login)){
            throw new UserExistAuthenticationException("Имя пользователя "+login+" уже занято" );
        }
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(name);
        if (user == null){
            throw new UsernameNotFoundException("Пользователь "+name+" не найден");
        }
        return user;
    }

}
