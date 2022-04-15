package click.applemt.apmt.service;

import click.applemt.apmt.controller.userController.PointDTO;
import click.applemt.apmt.controller.userController.UidDataDTO;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.repository.userRepository.AccountHistoryRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountHistoryRepository historyRepository;
    @Transactional
    public User join(UidDataDTO uidData) {
        User user = new User().UidDataToUser(uidData);
        User findUser = userRepository.findByUid(user.getUid());
        if(findUser == null){
            return userRepository.save(user);
        }
        return findUser;
    }

    public User findOne(String uid) {
        return userRepository.findByUid(uid);
    }


    @Transactional
    public User pointUpdate(@RequestBody PointDTO data){
        User findUser = userRepository.findByUid(data.getUid());
        AccountHistory history = new AccountHistory().pointDtoToAccountHistory(data,findUser);
        historyRepository.save(history);
        if(data.isChargeOrRefund()){
            findUser.plusAccount(data.getPoint());
        }else{
            findUser.minusAccount(data.getPoint());
        }
        return findUser;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        return null;
    }
    // accessToken이 firebase에서 발급한 토근이 맞는지만 확인
}
