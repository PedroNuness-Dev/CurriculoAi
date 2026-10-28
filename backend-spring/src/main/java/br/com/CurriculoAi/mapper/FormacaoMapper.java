package br.com.CurriculoAi.mapper;

import br.com.CurriculoAi.dto.response.FormacaoUserDtoResponse;
import br.com.CurriculoAi.models.FormacaoUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CursoMapper.class})
public interface FormacaoMapper {

    FormacaoUserDtoResponse toDto(FormacaoUser formacaoUser);
}
