package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserSocialNetworkRequest;
import br.com.CurriculoAi.dto.user.UserSocialNetworkResponse;
import br.com.CurriculoAi.exceptions.ListIsEmptyException;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.mapper.user.UserSocialNetworkMapper;
import br.com.CurriculoAi.models.SocialNetwork;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.models.UserSocialNetwork;
import br.com.CurriculoAi.repositories.user.UserSocialNetworkRepository;
import br.com.CurriculoAi.services.auth.AuthService;
import br.com.CurriculoAi.services.social_network.SocialNetworkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserSocialNetworkService {

    private final UserSocialNetworkRepository userSocialNetworkRepository;
    private final AuthService authService;
    private final SocialNetworkService socialNetworkService;
    private final UserSocialNetworkMapper userSocialNetworkMapper;

    public List<UserSocialNetworkResponse> registerUserSocialNetworks(List<UserSocialNetworkRequest> userSocialNetworkRequests){

        if (userSocialNetworkRequests.isEmpty()) throw new ListIsEmptyException("Lista de redes sociais para atribuir ao usuário não pode ser vazia");

        User user = authService.getUserByTokenAuthenticate();

        log.info("Iniciando registro das redes sociais do usuário [{}}, com um total de: [{}}", user.getEmail(), userSocialNetworkRequests.size());

        Map<Long, SocialNetwork> socialNetworksMap = findSocialNetworksByUserSocialNetworksRequest(userSocialNetworkRequests);

        List<UserSocialNetwork> userSocialNetworksForSave = userSocialNetworkRequests
                .stream()
                .map(userSocialNetworkRequest -> {
                    SocialNetwork socialNetwork = Optional.ofNullable(socialNetworksMap.get(userSocialNetworkRequest.socialnetworkId()))
                            .orElseThrow(() -> new ResourceNotFoundException("Rede social com id: "+userSocialNetworkRequest.socialnetworkId()+" não encontrada"));

                    return UserSocialNetwork.builder()
                            .link(userSocialNetworkRequest.link())
                            .socialNetwork(socialNetwork)
                            .user(user)
                            .build();
                })
                .toList();

        List<UserSocialNetwork> userSocialNetworksSaved = userSocialNetworkRepository.saveAll(userSocialNetworksForSave);

        return userSocialNetworksSaved
                .stream()
                .map(userSocialNetworkMapper::toDto)
                .toList();
    }

    private Map<Long, SocialNetwork> findSocialNetworksByUserSocialNetworksRequest(List<UserSocialNetworkRequest> userSocialNetworkRequests){
        Set<Long> socialNetworksIds = userSocialNetworkRequests
                .stream()
                .map(UserSocialNetworkRequest::socialnetworkId)
                .collect(Collectors.toSet());

        return socialNetworkService.findSocialNetworksByIds(socialNetworksIds);
    }
}
