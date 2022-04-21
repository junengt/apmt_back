package click.applemt.apmt.service;

import click.applemt.apmt.controller.profile.PointDto;
import click.applemt.apmt.domain.User;
import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.repository.userRepository.AccountHistoryRepository;
import click.applemt.apmt.repository.userRepository.UserRepository;
import click.applemt.apmt.security.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AccountHistoryRepository historyRepository;




    @Transactional
    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {
        //firebase 에서 가져온 token이 유효한지 확인 후 uid를 가져온다.

        Optional<User> findUser = userRepository.findById(uid);
        //가져온 uid로 우리 DB에 등록된 user인지 확인한다.

        if(findUser.isPresent()){
            //findUser가 있다면
            User getUser = findUser.get();
            // 가져온다.
            AuthUser user = makeAuthUser(getUser);
            return user;
            // 인증정보를 security에 띄워준다. (이제 어느 메소드에서나 쓸 수 있다.)
        }else{
            //인증받은 토근이지만 유저가 없다면
            User user = new User();
            //유저 인스턴스를 새로만들어
            user.newUser(uid);
            userRepository.save(user);
            //데이터베이스에 저장한다.
            AuthUser authUser = makeAuthUser(user);
            //마찬 가지로 인증정보를 띄운다.
            return authUser;
        }

    }

    private AuthUser makeAuthUser(User getUser) {
        AuthUser user = new AuthUser();
        //UserDetails를 상속받은 user 인스턴스를 생성해준다.
        user.setUid(getUser.getUid());
        user.setAccount(getUser.getAccount());
        return user;
    }
    @Transactional
    public User pointUpdate(PointDto data, AuthUser authUser) {
        User findUser = userRepository.findById(authUser.getUid()).get();
        AccountHistory history = new AccountHistory().pointDtoToAccountHistory(data,findUser);
        historyRepository.save(history);
        if(data.isChargeOrRefund()){
            findUser.plusAccount((long) data.getPoint());
        }else{
            findUser.minusAccount((long) data.getPoint());
        }
        return findUser;

    }
}
