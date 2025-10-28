package com.example.accountservice.Mapper;

import com.example.accountservice.dto.response.BalanceDto;
import com.example.accountservice.dto.response.UserAccountDto;
import com.example.accountservice.model.Balance;
import com.example.accountservice.model.UserAccount;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModelMapper {
    UserAccountDto toUserAccountDto(UserAccount userAccount);
    List<UserAccountDto> toUserAccountDtoList(List<UserAccount> userAccount);

    UserAccount toUserAccountEntity(UserAccountDto userAccountDto);

    BalanceDto toBalanceDto(Balance balance);
    Balance toBalanceEntity(BalanceDto balanceDto);

}
