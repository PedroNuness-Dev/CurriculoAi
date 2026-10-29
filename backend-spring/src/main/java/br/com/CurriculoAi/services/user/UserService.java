package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.auth.LoginRequest;
import br.com.CurriculoAi.dto.auth.LoginResponse;
import br.com.CurriculoAi.dto.auth.TokenDetails;
import br.com.CurriculoAi.dto.request.UsuarioDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioFullContentDtoResponse;
import br.com.CurriculoAi.dto.response.UsuarioTokenIdentResponseDto;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.UsuarioMapper;
import br.com.CurriculoAi.models.Role;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.AreaUserRepository;
import br.com.CurriculoAi.repositories.RoleRepository;
import br.com.CurriculoAi.repositories.UsuarioCadRepository;
import br.com.CurriculoAi.security.TokenService;
import br.com.CurriculoAi.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper mapper;
    private final AreaUserRepository areaUserRepository;
    private final UsuarioCadRepository repository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;
    private final TokenIdentificacaoUsuarioService tokenIdentificacaoUsuarioService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public User findByid(Long id) {

        logger.info("Procurando o usuario pelo id: {}", id);

        User entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esse id não foi encontrado no sistema"));

        logger.info("Usuário encontrado!");
        return entity;
    }

    @Transactional
    public UsuarioTokenIdentResponseDto createUsuario(UsuarioDtoRequest usuarioCadDTO) {

        logger.info("Iniciando o processo de criação de usuario");

        if (repository.findByEmail(usuarioCadDTO.email()).isPresent()) throw new IllegalArgumentException("Email inválido para cadastro");

        //Registra como ROLE_CANDIDATO TODO: VER DEPOIS COMO UTILIZAR DE RECRUTADOR
        Role role = roleRepository.findById(1L)
                .orElseThrow(() -> new ResourceNotFoundException("Role não encontrada"));


        var entity = User.builder()
                .nome(usuarioCadDTO.nome())
                .senha(passwordEncoder.encode(usuarioCadDTO.senha()))
                .email(usuarioCadDTO.email())
                .build();

        entity.getRoles().add(role);

        User savedEntity = repository.save(entity);

        String tokenDeIdentificacao = tokenIdentificacaoUsuarioService.gerarTokenDeIdentificacao(savedEntity);

        UsuarioTokenIdentResponseDto responseDto = new UsuarioTokenIdentResponseDto(savedEntity.getId(), savedEntity.getNome(), tokenDeIdentificacao);

        logger.info("Usuário criado com sucesso! Id: {}", savedEntity.getId());

        return responseDto;

    }

    public LoginResponse login(LoginRequest loginRequest){

        logger.info("Iniando método de Login do usuário...");

        UserDetailsImpl userDetails = authenticateUser(loginRequest);

        TokenDetails tokenCreated = tokenService.generateToken(userDetails);

        logger.info("Login efetuado com sucesso!");

        return new LoginResponse(
                tokenCreated.token(),
                "Bearer",
                tokenCreated.expiration(),
                userDetails.getUserId(),
                userDetails.getName(),
                userDetails.getEmail()
        );
    }

    @Transactional
    public void deleteUser(Long id) {

        logger.info("Iniciando método de deletar usuario...");

        var entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Esse id não foi encontrado no sistema"));

        logger.info("Usuário deletado com sucesso!");

         repository.delete(entity);
    }

    public UsuarioFullContentDtoResponse me() {

        logger.info("Iniando método de construção dos dados do usuário para retorno através do JWT");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado");
        }

        Object principal = authentication.getPrincipal();
        User userAutenticado;

        logger.warn("Resgatar usuário através do subject do JWT efetuado com sucesso!");

        if (principal instanceof User) {
            userAutenticado = (User) principal;

        } else if (principal instanceof UserDetails) {

            String email = ((UserDetails) principal).getUsername();

            userAutenticado = repository.findByEmail(email)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        } else if (principal instanceof String) {

            String p = (String) principal;

            // anonymousUser é a principal padrão do Spring para auth anônima
            if ("anonymousUser".equals(p)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário anônimo");
            }

            // se for username/email, tenta buscar no banco
            userAutenticado = repository.findByEmail(p)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        } else {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Tipo de principal inesperado: " +
                            (principal == null ? "null" : principal.getClass().getName())
            );
        }

        User userBuscado = repository.findById(userAutenticado.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        logger.info("Usuário encontrado com sucesso, iniciando processo de construção do DTO...");

        return mapper.toFullContentDto(userBuscado);
    }

    private UserDetailsImpl authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.email(),loginRequest.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return (UserDetailsImpl) authentication.getPrincipal();
    }
}
