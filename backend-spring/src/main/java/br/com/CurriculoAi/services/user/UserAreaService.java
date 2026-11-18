package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.area.AreaResponse;
import br.com.CurriculoAi.dto.user.UserAreaResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
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
@Transactional(readOnly = true)
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

        return toResponse(user, area);
    }

    public UserAreaResponse getAreaOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        Area area = getAreaOrThrow(user);

        return toResponse(user, area);
    }

    @Transactional
    public UserAreaResponse updateAreaOfUser(Long areaId){

        log.info("Iniciando atualização da área do usuário autenticado para a área id [{}]", areaId);

        // O vínculo é único (ManyToOne), então atualizar é trocar a área vinculada
        return addAreaToUser(areaId);
    }

    @Transactional
    public void removeAreaOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        getAreaOrThrow(user);

        // Remove apenas o vínculo; a Area é um cadastro compartilhado e não é apagada
        user.setArea(null);

        userRepository.save(user);

        log.info("Área desvinculada com sucesso do usuário id [{}]", user.getUserId());
    }

    private Area getAreaOrThrow(User user){
        if (user.getArea() == null) throw new ResourceNotFoundException("Usuário não possui área cadastrada");

        return user.getArea();
    }

    private UserAreaResponse toResponse(User user, Area area){
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
