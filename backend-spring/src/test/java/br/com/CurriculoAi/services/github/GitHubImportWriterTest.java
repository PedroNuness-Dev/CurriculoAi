package br.com.CurriculoAi.services.github;

import br.com.CurriculoAi.dto.project.ProjectRequest;
import br.com.CurriculoAi.mapper.project.ProjectMapper;
import br.com.CurriculoAi.models.Project;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.project.ProjectRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;

class GitHubImportWriterTest {
    @Test
    void skipsExistingProjectsAndLocksOnlyAuthenticatedUser() {
        var repository = mock(ProjectRepository.class);
        var entityManager = mock(EntityManager.class);
        var mapper = mock(ProjectMapper.class);
        var user = User.builder().userId(7L).build();
        var existing = Project.builder().user(user).link("https://github.com/owner/demo.git").build();
        when(entityManager.find(User.class, 7L, LockModeType.PESSIMISTIC_WRITE)).thenReturn(user);
        when(repository.findAllByUser_UserIdOrderByProjectIdAsc(7L)).thenReturn(List.of(existing));
        when(mapper.toDto(existing)).thenReturn(new br.com.CurriculoAi.dto.project.ProjectResponse(
                1L, "demo", "Descrição revisada", existing.getLink(), java.util.Set.of()));
        var writer = new GitHubImportWriter(entityManager, repository, mapper);
        writer.save(7L, List.of(new ProjectRequest("Demo", "Nova descrição", "https://github.com/OWNER/demo", List.of())));
        verify(repository, never()).save(any());
        verify(entityManager).find(User.class, 7L, LockModeType.PESSIMISTIC_WRITE);
    }
}
