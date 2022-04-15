package click.applemt.apmt.service;

import click.applemt.apmt.controller.userController.UidDataDTO;
import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.repository.userRepository.AccountHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountHistoryRepository accountRepository;



    public List<AccountHistory> myProfileAccount(UidDataDTO uid) {
      return accountRepository.findByUserId(uid.getUid());

    }
}
