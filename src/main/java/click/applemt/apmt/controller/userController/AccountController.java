package click.applemt.apmt.controller.userController;

import click.applemt.apmt.domain.point.AccountHistory;
import click.applemt.apmt.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/profileItem")
    public Result profileItem(@RequestBody UidDataDTO uid){
      List<AccountHistory> historyList  =  accountService.myProfileAccount(uid);
        List<ProfileItemDto> dtoList = historyList.stream().map((e) -> {
            ProfileItemDto dto = new ProfileItemDto();
            dto.setDate(e.getCreatedTime());
            dto.setPrice(e.getPrice());
            dto.setDivision(e.getDivision().toString());
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
