package br.com.CurriculoAi.services.social_network;

import br.com.CurriculoAi.dto.social_network.SocialNetworkRequest;
import br.com.CurriculoAi.dto.social_network.SocialNetworkResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.social_network.SocialNetworkMapper;
import br.com.CurriculoAi.models.SocialNetwork;
import br.com.CurriculoAi.repositories.social_network.SocialNetworkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SocialNetworkService {

    private final SocialNetworkRepository socialNetworkRepository;
    private final SocialNetworkMapper socialNetworkMapper;

    @Transactional
    public SocialNetworkResponse createSocialNetwork(SocialNetworkRequest socialNetworkRequest) {

        log.info("Iniciando o processo de criação de rede social");

        if (socialNetworkRepository.existsByNameIgnoreCase(socialNetworkRequest.name())) {
            throw new IllegalArgumentException("Já existe uma rede social com esse nome");
        }

        SocialNetwork newSocialNetwork = SocialNetwork.builder()
                .name(socialNetworkRequest.name())
                .build();

        SocialNetwork savedSocialNetwork = socialNetworkRepository.save(newSocialNetwork);

        log.info("Rede social criada com sucesso! Id: {}", savedSocialNetwork.getSocialNetworkId());

        return socialNetworkMapper.toDto(savedSocialNetwork);
    }

    public List<SocialNetworkResponse> findAllSocialNetworks() {

        log.info("Iniciando busca de todas as redes sociais");

        List<SocialNetwork> socialNetworks = socialNetworkRepository.findAll();

        log.info("Redes sociais buscadas, com um total de: {}", socialNetworks.size());

        return socialNetworks
                .stream()
                .map(socialNetworkMapper::toDto)
                .toList();
    }

    public SocialNetworkResponse getSocialNetworkById(Long socialNetworkId) {
        return socialNetworkMapper.toDto(findSocialNetworkById(socialNetworkId));
    }

    @Transactional
    public SocialNetworkResponse updateSocialNetwork(Long socialNetworkId, SocialNetworkRequest socialNetworkRequest) {

        log.info("Iniciando a atualização da rede social com id [{}]", socialNetworkId);

        SocialNetwork socialNetwork = findSocialNetworkById(socialNetworkId);

        if (socialNetworkRepository.existsByNameIgnoreCaseAndSocialNetworkIdNot(socialNetworkRequest.name(), socialNetworkId)) {
            throw new IllegalArgumentException("Já existe uma rede social com esse nome");
        }

        socialNetwork.setName(socialNetworkRequest.name());

        SocialNetwork updatedSocialNetwork = socialNetworkRepository.save(socialNetwork);

        log.info("Rede social atualizada com sucesso! Id: {}", updatedSocialNetwork.getSocialNetworkId());

        return socialNetworkMapper.toDto(updatedSocialNetwork);
    }

    // Público para ser reutilizado por outros services (ex: UserSocialNetworkService)
    public SocialNetwork findSocialNetworkById(Long socialNetworkId) {
        return socialNetworkRepository.findById(socialNetworkId)
                .orElseThrow(() -> new ResourceNotFoundException("Rede social com id: " + socialNetworkId + " não encontrada"));
    }
}