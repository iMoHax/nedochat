package ru.intech.nedochat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.intech.nedochat.entity.Role;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.repository.RoleRepository;
import ru.intech.nedochat.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Optional;


@Service
public class UsersService implements UserDetailsService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private Role defaultRole;

    @Autowired
    public UsersService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init(){
        Optional<Role> userRole = roleRepository.findByName(Role.USER);
        defaultRole = userRole.orElseGet(()-> roleRepository.save(new Role(Role.USER)));
    }

    public User add(User user){
        String login = user.getUsername();
        if (userRepository.existsByUsername(login)){
            throw new UserExistAuthenticationException("Имя пользователя "+login+" уже занято" );
        }
        user = userRepository.save(user);
        if (user.getRoles().isEmpty()){
            user.addRole(defaultRole);
        }
        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(name);
         return user.orElseThrow(()-> new UsernameNotFoundException("Пользователь "+name+" не найден"));
    }

}
