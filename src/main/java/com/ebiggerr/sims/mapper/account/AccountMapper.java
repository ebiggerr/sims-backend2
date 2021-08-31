package com.ebiggerr.sims.mapper.account;

import com.ebiggerr.sims.DTO.Account.AccountOutput;
import com.ebiggerr.sims.domain.account.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class );

    AccountOutput accountToAccountDto(Account acc);

}
