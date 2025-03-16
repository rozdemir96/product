package com.project.product.mapper;

import com.project.product.controller.user.model.UserModel;
import com.project.product.entity.User;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-03-16T23:20:41+0300",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.12.1.jar, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User toEntity(UserModel userModel) {
        if ( userModel == null ) {
            return null;
        }

        User user = new User();

        user.setName( userModel.getName() );
        user.setSurname( userModel.getSurname() );
        user.setEmail( userModel.getEmail() );
        user.setMobilePhone( userModel.getMobilePhone() );
        user.setAddress( userModel.getAddress() );
        user.setUsername( userModel.getUsername() );
        user.setPassword( userModel.getPassword() );
        user.setEnabled( userModel.getEnabled() );
        user.setRole( userModel.getRole() );

        return user;
    }

    @Override
    public UserModel toModel(User user) {
        if ( user == null ) {
            return null;
        }

        UserModel userModel = new UserModel();

        userModel.setId( user.getId() );
        userModel.setCreatedUserId( user.getCreatedUserId() );
        userModel.setUpdatedUserId( user.getUpdatedUserId() );
        userModel.setDeletedUserId( user.getDeletedUserId() );
        userModel.setCreatedDate( user.getCreatedDate() );
        userModel.setUpdatedDate( user.getUpdatedDate() );
        userModel.setDeletedDate( user.getDeletedDate() );
        userModel.setName( user.getName() );
        userModel.setSurname( user.getSurname() );
        userModel.setEmail( user.getEmail() );
        userModel.setMobilePhone( user.getMobilePhone() );
        userModel.setAddress( user.getAddress() );
        userModel.setUsername( user.getUsername() );
        userModel.setPassword( user.getPassword() );
        userModel.setEnabled( user.getEnabled() );
        userModel.setRole( user.getRole() );

        return userModel;
    }

    @Override
    public List<User> toEntityList(List<UserModel> userModelList) {
        if ( userModelList == null ) {
            return null;
        }

        List<User> list = new ArrayList<User>( userModelList.size() );
        for ( UserModel userModel : userModelList ) {
            list.add( toEntity( userModel ) );
        }

        return list;
    }

    @Override
    public List<UserModel> toModelList(List<User> userList) {
        if ( userList == null ) {
            return null;
        }

        List<UserModel> list = new ArrayList<UserModel>( userList.size() );
        for ( User user : userList ) {
            list.add( toModel( user ) );
        }

        return list;
    }

    @Override
    public void updateUserFromModel(UserModel userModel, User user) {
        if ( userModel == null ) {
            return;
        }

        user.setId( userModel.getId() );
        user.setCreatedUserId( userModel.getCreatedUserId() );
        user.setUpdatedUserId( userModel.getUpdatedUserId() );
        user.setDeletedUserId( userModel.getDeletedUserId() );
        user.setCreatedDate( userModel.getCreatedDate() );
        user.setUpdatedDate( userModel.getUpdatedDate() );
        user.setDeletedDate( userModel.getDeletedDate() );
        user.setName( userModel.getName() );
        user.setSurname( userModel.getSurname() );
        user.setEmail( userModel.getEmail() );
        user.setMobilePhone( userModel.getMobilePhone() );
        user.setAddress( userModel.getAddress() );
        user.setUsername( userModel.getUsername() );
        user.setPassword( userModel.getPassword() );
        user.setEnabled( userModel.getEnabled() );
        user.setRole( userModel.getRole() );
    }
}
