package br.com.CurriculoAi.mapper.certification;

import br.com.CurriculoAi.dto.certification.CertificationResponse;
import br.com.CurriculoAi.mapper.institution.InstitutionMapper;
import br.com.CurriculoAi.models.Certification;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {InstitutionMapper.class})
public interface CertificationMapper {

    CertificationResponse toDto(Certification certification);
}
