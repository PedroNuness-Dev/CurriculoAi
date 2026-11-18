package br.com.CurriculoAi.repositories.user;

import br.com.CurriculoAi.models.UserSocialNetwork;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSocialNetworkRepository extends JpaRepository<UserSocialNetwork, Long> {

    @Query("""
    SELECT usn FROM UserSocialNetwork usn
    WHERE usn.user.userId = :userId
    ORDER BY usn.userSocialNetworkId
""")
    List<UserSocialNetwork> findAllUserSocialNetworksOfUser(@Param("userId") Long userId);

    Optional<UserSocialNetwork> findByUserSocialNetworkIdAndUser_UserId(Long userSocialNetworkId, Long userId);
}
