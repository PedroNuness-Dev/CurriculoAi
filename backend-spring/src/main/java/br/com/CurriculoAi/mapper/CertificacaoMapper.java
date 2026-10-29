package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.CertificacaoUserDtoResponse;
import br.com.CurriculoAi.models.Certification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {InstituicaoMapper.class})
public interface CertificacaoMapper {

    CertificacaoUserDtoResponse toDto(Certification certification);
}
