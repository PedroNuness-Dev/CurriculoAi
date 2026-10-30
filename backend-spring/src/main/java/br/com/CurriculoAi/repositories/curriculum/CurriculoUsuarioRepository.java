package br.com.CurriculoAi.repositories.curriculum;

import br.com.CurriculoAi.models.UserCurriculum;
import br.com.CurriculoAi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurriculoUsuarioRepository extends JpaRepository<UserCurriculum, Long> {

    List<UserCurriculum> findByUsuario(User user);
}