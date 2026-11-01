package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.dto.user.UserAreaResponse;
import br.com.CurriculoAi.models.Area;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.area.AreaService;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAreaService {

    private final UserRepository userRepository;
    private final AreaService areaService;
    private final AuthService authService;

    @Transactional
    public UserAreaResponse addAreaToUser(Long areaId){

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando adição de área ao usuário autenticado com email: {}", user.getEmail());

        Area area = areaService.findAreaById(areaId);

        user.setArea(area);
        userRepository.save(user);

        log.info("Adição da área: [{}] para o usuário com email [{}] realizada com sucesso", area.getName(), user.getEmail());

        return new UserAreaResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                new AreaResponse(
                        area.getAreaId(),
                        area.getName()
                )
        );
    }
}
