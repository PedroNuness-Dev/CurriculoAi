package br.com.CurriculoAi.dto.response;

import java.util.List;

public record UsuarioFullContentDtoResponse(
        UsuarioCadDtoResponse usuario,
        AreaDTOResponse area,
        List<FormacaoUserDtoResponse> formacoes,
        List<ExperienciaUserDtoResponse> experiencias,
        List<CertificacaoUserDtoResponse> certificoes,
        DisponibilidadeUserDtoResponse disponibilidade,
        List<IdiomaUserDtoResponse> idiomas,
        List<ProjetoDtoResponse> projetos
) {
}
