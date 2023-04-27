package com.commerce.domain.user;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.commerce.domain.user.common.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

	boolean existsUserByEmail(String email);


	Optional<User> findByIdAndStatusIn(Long id, Set<UserStatus> status);

	default Optional<User> findByIdExcludeDeleted(Long id){
		return this.findByIdAndStatusIn(id, EnumSet.of(UserStatus.ACTIVE,UserStatus.BAN));
	}

	Optional<User> findByIdAndStatus(Long id,UserStatus status);


	Optional<User> findByEmailAndPassword(String email,String password);

	Optional<User> findByEmail(String email);

	Page<User> findAll(Pageable pageable);

}
