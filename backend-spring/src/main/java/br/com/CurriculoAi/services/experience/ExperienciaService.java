package br.com.CurriculoAi.services.experience;

import br.com.CurriculoAi.dto.request.ExperienciaDtoRequest;
import br.com.CurriculoAi.dto.response.UsuarioTokenIdentResponseDto;
import br.com.CurriculoAi.models.*;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.repositories.AreaRepository;
import br.com.CurriculoAi.repositories.EnterpriseRepository;
import br.com.CurriculoAi.repositories.ExperienceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExperienciaService {

    private final ExperienceRepository experienceRepository;
    private final AreaRepository areaRepository;
    private final EnterpriseRepository enterpriseRepository;

    private static final Logger logger = LoggerFactory.getLogger(ExperienciaService.class);

    @Transactional
    public UsuarioTokenIdentResponseDto cadastrarExperiencias(List<ExperienciaDtoRequest> experiencias, String token){

        logger.info("Iniciando cadastro de experiências do usuário, com um total de: {}", experiencias.size());

        TokenIdentificacaoUsuario tokenIdentificacaoUsuario = tokenIdentificacaoUsurioRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token não encotrado"));

        User user = tokenIdentificacaoUsuario.getUsuario();

        List<Long> idsAreas = experiencias
                .stream()
                .map(ExperienciaDtoRequest::id_area)
                .distinct()
                .toList();

        List<Long> idsEmpresas = experiencias
                .stream()
                .map(ExperienciaDtoRequest::id_empresa)
                .distinct()
                .toList();

        Map<Long, Area> areasPorId = areaRepository.findAllById(idsAreas)
                .stream()
                .collect(Collectors.toMap(Area::getId, Function.identity()));

        Map<Long, Enterprise> empresasPorId = enterpriseRepository.findAllById(idsEmpresas)
                .stream()
                .collect(Collectors.toMap(Enterprise::getId, Function.identity()));

        List<Experience> experienciasSalvas = experiencias
                .stream()
                .map(experiencia -> {

                    Area area = Optional.ofNullable(areasPorId.get(experiencia.id_area()))
                            .orElseThrow(() -> new ResourceNotFoundException("Área com id"+experiencia.id_area()+" não encontrada"));

                    Enterprise enterprise = Optional.ofNullable(empresasPorId.get(experiencia.id_empresa()))
                            .orElseThrow(() -> new ResourceNotFoundException("Empresa com id"+experiencia.id_empresa()+" não encontrada"));

                    return new Experience(
                            null,
                            area,
                            enterprise,
                            experiencia.dataInicio(),
                            experiencia.cargo(),
                            experiencia.dataFim(),
                            experiencia.titulo(),
                            experiencia.descriacao(),
                            experiencia.trabalhoAtual(),
                            user
                    );
                })
                .toList();

        experienceRepository.saveAll(experienciasSalvas);

        logger.info("Cadastro de experiências realizado com sucesso!");

        return new UsuarioTokenIdentResponseDto(tokenIdentificacaoUsuario.getId(), user.getNome(), token);
    }
}
