package br.com.CurriculoAi.mapper.user;

import br.com.CurriculoAi.dto.user.UserLanguageResponse;
import br.com.CurriculoAi.mapper.language.LanguageMapper;
import br.com.CurriculoAi.models.UserLanguage;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {LanguageMapper.class})
public interface UserLanguageMapper {


    UserLanguageResponse toDto(UserLanguage userLanguage);
}
