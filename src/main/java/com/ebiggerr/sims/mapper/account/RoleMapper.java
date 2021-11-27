package com.ebiggerr.sims.mapper.account;

import com.ebiggerr.sims.DTO.Roles.RoleInput;
import com.ebiggerr.sims.DTO.Roles.UpdateRoleDetailsInput;
import com.ebiggerr.sims.domain.account.RoleDetails;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RoleMapper {

    RoleMapper INSTANCE = Mappers.getMapper( RoleMapper.class );

    RoleInput roleToRoleDTO (RoleDetails roleDetails);

    UpdateRoleDetailsInput roleToRoleDetailsUpdateDto(RoleDetails roleDetails);
}
