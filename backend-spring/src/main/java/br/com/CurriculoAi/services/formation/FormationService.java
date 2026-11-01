package br.com.CurriculoAi.services.formation;

import br.com.CurriculoAi.dto.formation.FormationRequest;
import br.com.CurriculoAi.dto.formation.FormationResponse;
import br.com.CurriculoAi.enums.formation.FormationType;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.formation.FormationMapper;
import br.com.CurriculoAi.models.Course;
import br.com.CurriculoAi.models.Formation;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.formation.FormationRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.course.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FormationService {

    private final CourseService courseService;
    private final FormationRepository formationRepository;
    private final AuthService authService;
    private final FormationMapper formationMapper;

    @Transactional
    public List<FormationResponse> registerFormationsByUser(List<FormationRequest> formationRequests){

        if (formationRequests.isEmpty()) throw new ListIsEmptyException("Lista de formações está vazia");

        log.info("Iniciando cadastro de formações do usuário, com o total de: {}", formationRequests.size());

        User user = authService.getUserByTokenAuthenticate();

        Map<Long, Course> coursesMap = getCoursesByFormationsRequest(formationRequests);

        List<Formation> formationsForSave = formationRequests
                .stream()
                .map(formationRequest -> {

                    Course course = Optional.ofNullable(coursesMap.get(formationRequest.courseId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Curso com id: "+formationRequest.courseId()+" não encontrado"));

                    Boolean inProgress = formationRequest.finishDate() == null;

                    return Formation.builder()
                            .user(user)
                            .course(course)
                            .formationType(FormationType.from(formationRequest.formationType()))
                            .startDate(formationRequest.startDate())
                            .finishDate(formationRequest.finishDate())
                            .inProgress(inProgress)
                            .build();
                })
                .toList();

        List<Formation> formationsSaved = formationRepository.saveAll(formationsForSave);

        log.info("Cadastro de formações realizada com sucesso!");

        return formationsSaved
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    public FormationResponse getFormationUserByFormationId(Long formationId){

        Long userId = authService.getUserIdByTokenAuthenticate();

        Formation formation = formationRepository.findByFormationIdAndUser_UserId(formationId,userId)
                .orElseThrow(() -> new ResourceNotFoundException("Formação com id: "+formationId+" não encontrada"));

        return formationMapper.toDto(formation);
    }

    public List<FormationResponse> findAllFormationsUser(){

        Long userId = authService.getUserIdByTokenAuthenticate();

        List<Formation> formations = formationRepository.findAllFormationsUser(userId);

        return formations
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    private Map<Long, Course> getCoursesByFormationsRequest(List<FormationRequest> formationRequests){
        // Pega todos os ids das formacoes sem repetir
        Set<Long> coursesIds = formationRequests
                .stream()
                .map(FormationRequest::courseId)
                .collect(Collectors.toSet());

        return courseService
                .findAllById(coursesIds);
    }
}
