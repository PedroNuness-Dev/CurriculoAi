package br.com.CurriculoAi.services.availability;

import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
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

        // O banco tem UNIQUE(user_id): sem essa checagem, o segundo cadastro estoura 500
        if (availabilityRepository.existsByUser_UserId(user.getUserId()))
            throw new IllegalArgumentException("Usuário já possui disponibilidade cadastrada. Use o PUT para atualizar");

        Availability availability = Availability.builder()
                .startAvailability(availabilityRequest.startAvailability())
                .workModel(WorkModel.from(availabilityRequest.workModel()))
                .user(user)
                .build();

        Availability availabilitySaved = availabilityRepository.save(availability);

        log.info("Disponibilidade salva com sucesso! Com id: {}", availabilitySaved.getAvailabilityId());

        return availabilityMapper.toDto(availabilitySaved);
    }


    public AvailabilityResponse getAvailabilityOfUser(){

        Availability availability = findAvailabilityOfAuthenticatedUser();

        return availabilityMapper.toDto(availability);
    }

    @Transactional
    public AvailabilityResponse updateAvailabilityOfUser(AvailabilityRequest availabilityRequest){

        Availability availability = findAvailabilityOfAuthenticatedUser();

        log.info("Iniciando atualização da disponibilidade com id [{}]", availability.getAvailabilityId());

        availability.setStartAvailability(availabilityRequest.startAvailability());
        availability.setWorkModel(WorkModel.from(availabilityRequest.workModel()));

        Availability updatedAvailability = availabilityRepository.save(availability);

        log.info("Disponibilidade com id [{}] atualizada com sucesso", updatedAvailability.getAvailabilityId());

        return availabilityMapper.toDto(updatedAvailability);
    }

    @Transactional
    public void deleteAvailabilityOfUser(){

        Availability availability = findAvailabilityOfAuthenticatedUser();

        availabilityRepository.delete(availability);

        log.info("Disponibilidade com id [{}] removida com sucesso", availability.getAvailabilityId());
    }

    private Availability findAvailabilityOfAuthenticatedUser(){

        Long userId = authService.getUserIdByTokenAuthenticate();

        return availabilityRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não possui disponibilidade cadastrada"));
    }
}
