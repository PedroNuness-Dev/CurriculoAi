package br.com.CurriculoAi.services.availability;

import br.com.CurriculoAi.dto.availability.AvailabilityRequest;
import br.com.CurriculoAi.dto.availability.AvailabilityResponse;
import br.com.CurriculoAi.enums.availability.WorkModel;
import br.com.CurriculoAi.mapper.availability.AvailabilityMapper;
import br.com.CurriculoAi.models.Availability;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.availability.AvailabilityRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;
    private final AuthService authService;
    private final AvailabilityMapper availabilityMapper;

    @Transactional
    public AvailabilityResponse registerAvailbilityUser(AvailabilityRequest availabilityRequest){

        log.info("Iniciando o cadastro de disponibilidade do usuário...");

        User user = authService.getUserByTokenAuthenticate();

        Availability availability = Availability.builder()
                .startAvailability(availabilityRequest.startAvailability())
                .workModel(WorkModel.from(availabilityRequest.workModel()))
                .user(user)
                .build();

        Availability availabilitySaved = availabilityRepository.save(availability);

        log.info("Disponibilidade salva com sucesso! Com id: {}", availabilitySaved.getAvailabilityId());

        return availabilityMapper.toDto(availabilitySaved);
    }
}
