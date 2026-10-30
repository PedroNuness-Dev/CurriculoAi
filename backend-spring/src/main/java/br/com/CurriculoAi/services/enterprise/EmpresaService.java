package br.com.CurriculoAi.services.enterprise;

import br.com.CurriculoAi.dto.response.EmpresaDtoResponse;
import br.com.CurriculoAi.models.Enterprise;
import br.com.CurriculoAi.repositories.EnterpriseRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EmpresaService {

    private final EnterpriseRepository enterpriseRepository;

    private static final Logger logger = LoggerFactory.getLogger(EmpresaService.class);

    public List<EmpresaDtoResponse> findAll(){

        logger.info("Buscando empresas...");

        List<Enterprise> empresasBuscadas = enterpriseRepository.findAll();

        logger.info("Empresas buscadas, com um total de: {}", empresasBuscadas.size());

        return empresasBuscadas
                .stream()
                .map(empresa -> {
                    return new EmpresaDtoResponse(empresa.getId(),empresa.getNome());
                })
                .toList();
    }
}
