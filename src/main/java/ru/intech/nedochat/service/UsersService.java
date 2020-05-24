package ru.intech.nedochat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private PasswordEncoder passwordEncoder;
    private Role defaultRole;

    @Autowired
    public UsersService(SessionRegistry sessionRegistry, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.sessionRegistry = sessionRegistry;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init(){
        Optional<Role> userRole = roleRepository.findByName(Role.USER);
        defaultRole = userRole.orElseGet(this::initDefaultRolesAndUsers);
    }

    private Role initDefaultRolesAndUsers(){
        User admin = new User("admin","Администратор",passwordEncoder.encode("1234567890"));
        admin.addRole(new Role(Role.ADMIN));
        userRepository.save(admin);
        return roleRepository.save(new Role(Role.USER));
    }

    @Transactional
    public User add(User user){
        String login = user.getUsername();
        if (userRepository.existsByUsernameAndDisabled(login,false)){
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
        Optional<User> user = userRepository.findByUsernameAndDisabled(name,false);
        return user.orElseThrow(()-> new UsernameNotFoundException("Пользователь "+name+" не найден"));
    }

}
