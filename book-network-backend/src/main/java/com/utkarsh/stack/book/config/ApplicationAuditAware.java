package com.utkarsh.stack.book.config;

import com.utkarsh.stack.book.user.User;
import com.utkarsh.stack.book.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class ApplicationAuditAware implements AuditorAware<Integer> {

//    private UserRepository userRepository;
//    public ApplicationAuditAware(UserRepository userRepository) {
//        this.userRepository = userRepository;
//    }
    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth ==null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken){
            log.info("Auth is null");
            return Optional.empty();
        }
        User user = (User) auth.getPrincipal();
//        if(auth.getPrincipal() instanceof User){
//            User user = (User) auth.getPrincipal();
//            log.info("Auth instance is of User: "+ auth);
//            return Optional.of(user.getId());
//        }
//        log.info("Auth instance is not of user: "+ auth);
//        return Optional.empty();
        return Optional.ofNullable(user.getId());
//        log.info("Current auditor"+ auth.getPrincipal());
//        log.info("Claims"+ auth);
//        User authenticatedUser = userRepository.findByEmail(auth.getPrincipal().toString()).get();
//        User authenticatedUser = (User) auth.getPrincipal();
//        return Optional.ofNullable(authenticatedUser.getId());
//        return Optional.ofNullable(auth.getPrincipal().toString());
    }
}
