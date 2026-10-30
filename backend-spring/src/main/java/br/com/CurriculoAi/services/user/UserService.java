package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserRegisterRequest;
import br.com.CurriculoAi.dto.user.UserResponse;
import br.com.CurriculoAi.enums.role.RoleType;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.UserRepository;
import br.com.CurriculoAi.services.role.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    

    @Transactional
    public UserResponse registerUserWithRoleTypeCandidate(UserRegisterRequest userRegisterRequest) {

        log.info("Iniciando o processo de criação de usuario");

        if (userRepository.findByEmail(userRegisterRequest.email()).isPresent()) throw new IllegalArgumentException("Email inválido para cadastro");

        User newUser = buildUserWithRoleCandidate(userRegisterRequest);

        User savedUser = userRepository.save(newUser);

        log.info("Usuário criado com sucesso! Id: {}", savedUser.getUserId());

        return new UserResponse(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRoles()
        );
    }

    public User buildUserWithRoleCandidate(UserRegisterRequest userRegisterRequest){

        User newUser = User.builder()
                .name(userRegisterRequest.name())
                .password(passwordEncoder.encode(userRegisterRequest.password()))
                .email(userRegisterRequest.email())
                .build();

        newUser = roleService.addRoleInUser(newUser, RoleType.ROLE_CANDIDATO);

        return newUser;
    }
}
