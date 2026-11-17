package br.com.CurriculoAi.services.user;

import br.com.CurriculoAi.dto.user.UserAddressResponse;
import br.com.CurriculoAi.exceptions.ResourceNotFoundException;
import br.com.CurriculoAi.models.Address;
import br.com.CurriculoAi.models.User;
import br.com.CurriculoAi.repositories.user.UserRepository;
import br.com.CurriculoAi.services.address.AddressService;
import br.com.CurriculoAi.services.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {

    private final UserRepository userRepository;
    private final AuthService authService;
    private final AddressService addressService;

    @Transactional
    public UserAddressResponse registerAddressInUserByZipCode(String zipCode){

        log.info("Iniciando cadastro de endereço no usuário autenticado pelo cep [{}]", zipCode);

        User user = authService.getUserByTokenAuthenticate();

        log.info("Usuário autenticado obtido, id [{}]", user.getUserId());

        Address address = addressService.getDetailsByZipCode(zipCode);

        log.info("Endereço obtido, id [{}] e cep [{}]", address.getAddressId(), address.getZipCode());

        user.setAddress(address);

        userRepository.save(user);

        log.info("Endereço id [{}] vinculado com sucesso ao usuário id [{}]",
                address.getAddressId(), user.getUserId());

        return toResponse(user, address);
    }

    public UserAddressResponse getAddressOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        Address address = getAddressOrThrow(user);

        return toResponse(user, address);
    }

    @Transactional
    public UserAddressResponse updateAddressOfUserByZipCode(String zipCode){

        log.info("Iniciando atualização do endereço do usuário autenticado pelo cep [{}]", zipCode);

        // O vínculo é único (ManyToOne), então atualizar é trocar o endereço vinculado
        return registerAddressInUserByZipCode(zipCode);
    }

    @Transactional
    public void removeAddressOfUser(){

        User user = authService.getUserByTokenAuthenticate();

        getAddressOrThrow(user);

        // Remove apenas o vínculo; o Address continua cadastrado e pode ser reutilizado por outros usuários
        user.setAddress(null);

        userRepository.save(user);

        log.info("Endereço desvinculado com sucesso do usuário id [{}]", user.getUserId());
    }

    private Address getAddressOrThrow(User user){
        if (user.getAddress() == null) throw new ResourceNotFoundException("Usuário não possui endereço cadastrado");

        return user.getAddress();
    }

    private UserAddressResponse toResponse(User user, Address address){
        return new UserAddressResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                addressService.toEntityResponse(address)
        );
    }
}
