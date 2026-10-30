package br.com.CurriculoAi.services.auth;

import br.com.CurriculoAi.dto.auth.LoginRequest;
import br.com.CurriculoAi.dto.auth.LoginResponse;
import br.com.CurriculoAi.dto.auth.TokenDetails;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.security.TokenService;
import br.com.CurriculoAi.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    public LoginResponse authenticateUser(LoginRequest loginRequest){

        log.info("Starting authentication process for user with email [{}]", loginRequest.email());

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) Objects.requireNonNull(authentication.getPrincipal());

        TokenDetails tokenDetails = tokenService.generateToken(userDetails);

        log.info("Authentication completed successfully");

        return new LoginResponse(
                tokenDetails.token(),
                "Bearer",
                tokenDetails.expiration(),
                userDetails.getUserId(),
                userDetails.getName(),
                userDetails.getEmail()
        );
    }

    public Long getUserIdByTokenAuthenticate(){

        Authentication authentication = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());

        UserDetailsImpl userDetails = (UserDetailsImpl) Objects.requireNonNull(authentication.getPrincipal());
        return userDetails.getUserId();
    }

    public User getUserByTokenAuthenticate(){

        Authentication authentication = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication());

        UserDetailsImpl userDetails = (UserDetailsImpl) Objects.requireNonNull(authentication.getPrincipal());
        return userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário authenticado não encontrado"));
    }
}
