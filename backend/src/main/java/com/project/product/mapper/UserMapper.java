package com.project.product.mapper;

import com.project.product.controller.user.model.UserModel;
import com.project.product.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true) // ID, otomatik oluşturulacağı için
    @Mapping(target = "createdUserId", ignore = true) // BaseModel alanları servis katmanında set edilecek
    @Mapping(target = "updatedUserId", ignore = true)
    @Mapping(target = "deletedUserId", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "deletedDate", ignore = true)
    User toEntity(UserModel userModel);

    UserModel toModel(User user);

    List<User> toEntityList(List<UserModel> userModelList);

    List<UserModel> toModelList(List<User> userList);

    // **Mevcut User nesnesini güncellemek için**
    void updateUserFromModel(UserModel userModel, @MappingTarget User user);
}