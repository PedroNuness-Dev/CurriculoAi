package br.com.CurriculoAi.repositories.user;

import br.com.CurriculoAi.models.UserEvaluation;

import java.util.Optional;

public interface UserEvaluationRepository {

    Optional<UserEvaluation> findByUserUserIdAndCurrentTrue(Long userId);

    boolean existsByUserUserIdAndContentHashAndRubricVersion(Long userId, String contentHash, String rubricVersion);
}
