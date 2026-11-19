package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.dto.project.ProjectResponse;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.models.Project;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.project.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GitHubImportWriter {
    private final EntityManager entityManager;
    private final ProjectRepository repository;
    private final ProjectMapper mapper;

    @Transactional
    public List<ProjectResponse> save(Long userId, List<ProjectRequest> selected) {
        User user = entityManager.find(User.class, userId, LockModeType.PESSIMISTIC_WRITE);
        if (user == null) throw new IllegalArgumentException("Usuário não encontrado.");
        Map<String, Project> existing = new HashMap<>();
        repository.findAllByUser_UserIdOrderByProjectIdAsc(userId)
                .forEach(project -> existing.put(key(project.getLink()), project));
        Map<String, ProjectResponse> result = new LinkedHashMap<>();
        for (var item : selected) {
            String key = key(item.link());
            Project project = existing.get(key);
            if (project == null) {
                project = repository.save(Project.builder().user(user).title(item.title())
                        .description(item.description()).link(item.link()).abilities(new HashSet<>()).build());
                existing.put(key, project);
            }
            result.put(key, mapper.toDto(project));
        }
        return List.copyOf(result.values());
    }

    private String key(String url) {
        return url.strip().replaceFirst("/$", "").replaceFirst("(?i)\\.git$", "").toLowerCase(Locale.ROOT);
    }
}
