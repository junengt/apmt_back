package click.applemt.apmt.controller.profile;

import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.security.AuthUser;
import click.applemt.apmt.service.AccountService;
import click.applemt.apmt.util.Time;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/profileItem")
    public Result profileItem(@AuthenticationPrincipal AuthUser user){
      List<AccountHistory> historyList  =  accountService.myProfileAccount(user.getUid());
        List<ProfileItemDto> dtoList = historyList.stream().map((e) -> {
            ProfileItemDto dto = new ProfileItemDto();
            dto.setDate(Time.calculateTime(java.sql.Timestamp.valueOf(e.getCreatedTime())));
            dto.setPrice(e.getPrice());

            if(e.getDivision().toString().equals("DEPOSIT")){
                dto.setDivision("입금");
            }else if(e.getDivision().toString().equals("WITHDRAW")){
                dto.setDivision("출금");
            }
            dto.setTitle("계좌거래");
            return dto;
        }).toList();
        return new Result(dtoList);
    }


    @Data
    @AllArgsConstructor
    static class Result<T> {
        private T data;
    }

}
