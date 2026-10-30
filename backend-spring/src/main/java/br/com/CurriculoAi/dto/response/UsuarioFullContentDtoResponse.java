package br.com.CurriculoAi.dto.response;

import br.com.CurriculoAi.dto.user.UserResponse;

import java.util.List;

public record UsuarioFullContentDtoResponse(
        UserResponse usuario,
        AreaDTOResponse area,
        List<FormacaoUserDtoResponse> formacoes,
        List<ExperienciaUserDtoResponse> experiencias,
        List<CertificacaoUserDtoResponse> certificoes,
        DisponibilidadeUserDtoResponse disponibilidade,
        List<IdiomaUserDtoResponse> idiomas,
        List<ProjetoDtoResponse> projetos
) {
}
