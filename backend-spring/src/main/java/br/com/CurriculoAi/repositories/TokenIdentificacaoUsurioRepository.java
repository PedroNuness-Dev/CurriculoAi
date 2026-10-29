package br.com.CurriculoAi.repositories;

import br.com.CurriculoAi.models.TokenIdentificacaoUsuario;
import br.com.CurriculoAi.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenIdentificacaoUsurioRepository extends JpaRepository<TokenIdentificacaoUsuario, Long> {

    void deleteByUsuarioCad(Usuario usuario);

    Optional<TokenIdentificacaoUsuario> findByToken(String token);
}
