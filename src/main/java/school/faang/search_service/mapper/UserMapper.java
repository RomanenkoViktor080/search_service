package school.faang.search_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.search_service.document.user.User;
import school.faang.search_service.dto.UserDto;
import school.faang.search_service.kafka.dto.user.UserCreate;
import school.faang.search_service.kafka.dto.user.update.UserUpdate;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    User toUser(UserCreate dto);

    UserDto toUserDto(User dto);

    void update(UserUpdate userDto, @MappingTarget User entity);
}
