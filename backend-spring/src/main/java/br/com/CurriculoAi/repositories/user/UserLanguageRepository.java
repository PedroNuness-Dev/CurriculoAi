package br.com.CurriculoAi.repositories.user;

import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.models.UserLanguage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLanguageRepository extends JpaRepository<UserLanguage, Long> {

    @Query("""
    SELECT ul FROM UserLanguage ul
    WHERE ul.user = :user
""")
    List<UserLanguage> findAllUserLanguageOfUser(@Param("user") User user);
}
