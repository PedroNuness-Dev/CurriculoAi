package br.com.CurriculoAi.mapper.user;

import br.com.CurriculoAi.dto.response.IdiomaUserDtoResponse;
import br.com.CurriculoAi.mapper.language.LanguageMapper;
import br.com.CurriculoAi.models.UserLanguage;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {LanguageMapper.class})
public interface UserLanguageMapper {


    IdiomaUserDtoResponse toDto(UserLanguage userLanguage);
}
