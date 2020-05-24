package ru.intech.nedochat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.intech.nedochat.entity.Role;
import ru.intech.nedochat.entity.User;
import ru.intech.nedochat.repository.RoleRepository;
import ru.intech.nedochat.repository.UserRepository;

import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UsersService implements UserDetailsService {

    private SessionRegistry sessionRegistry;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private Role defaultRole;

    @Autowired
    public UsersService(SessionRegistry sessionRegistry, UserRepository userRepository, RoleRepository roleRepository) {
        this.sessionRegistry = sessionRegistry;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void init(){
        Optional<Role> userRole = roleRepository.findByName(Role.USER);
        defaultRole = userRole.orElseGet(()-> roleRepository.save(new Role(Role.USER)));
    }

    @Transactional
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

    public List<User> getActiveUsers(){
        return userRepository.findAllByDisabledOrderByName(false);
    }

    public List<User> getOnlineUsers(){
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof UserDetails)
                .map(User.class::cast)
                .sorted(Comparator.comparing(User::getName))
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(name);
         return user.orElseThrow(()-> new UsernameNotFoundException("Пользователь "+name+" не найден"));
    }

}
