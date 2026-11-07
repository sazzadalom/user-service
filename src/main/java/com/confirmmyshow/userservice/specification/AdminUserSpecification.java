package com.confirmmyshow.userservice.specification;

import com.confirmmyshow.userservice.entity.User;
import com.confirmmyshow.userservice.enums.UserStatus;

import org.springframework.data.jpa.domain.Specification;

public class AdminUserSpecification {

	private AdminUserSpecification() {}

	public static Specification<User> hasEmail(String email) {

		return (root, query, criteriaBuilder) -> {

			if (email == null || email.isBlank()) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),"%" + email.trim().toLowerCase() + "%");
		};
	}

	public static Specification<User> hasStatus(UserStatus status) {

		return (root, query, criteriaBuilder) -> {

			if (status == null) {
				return criteriaBuilder.conjunction();
			}

			return criteriaBuilder.equal(root.get("status"), status);
		};
	}
}