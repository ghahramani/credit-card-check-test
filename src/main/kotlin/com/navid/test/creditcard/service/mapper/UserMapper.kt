package com.navid.test.creditcard.service.mapper

import com.navid.test.creditcard.domain.User
import com.navid.test.creditcard.service.dto.UserDTO
import com.navid.test.creditcard.service.mapper.util.UserMappingUtil
import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

/**
 * Mapper for the entity User and its DTO UserDTO.
 */
@Mapper(componentModel = "spring", uses = [UserMappingUtil::class])
interface UserMapper {

    @Mappings(
        Mapping(target = "password", ignore = true)
    )
    fun userDTOToUser(userDTO: UserDTO): User

    @InheritInverseConfiguration
    fun userToUserDTO(user: User): UserDTO

}
