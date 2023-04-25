#-------------------------------
# DDL
#-------------------------------

use sample;

-- sample.category definition
CREATE TABLE `category` (
  `category_id` bigint(20) auto_increment  NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `cat_level` int(11) DEFAULT NULL,
  `cat_name` varchar(255) DEFAULT NULL,
  `status` varchar(30) NOT NULL,
  `upper_cat_code` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '카테고리';

-- sample.products definition
CREATE TABLE `products` (
  `products_id` bigint(20) auto_increment  NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `like_count` int(11) DEFAULT 0,
  `name` varchar(255) NOT NULL,
  `price` bigint(20) NOT NULL,
  `stock_quantity` int(11) NOT NULL,
  `view_count` int(11) DEFAULT 0,
  `category_id` bigint(20) NOT NULL,
  PRIMARY KEY (`products_id`),
  KEY `FK1cf90etcu98x1e6n9aks3tel3` (`category_id`),
  CONSTRAINT `FK1cf90etcu98x1e6n9aks3tel3` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT '상품';

-- sample.users definition
CREATE TABLE `users` (
  `users_id` bigint(20) auto_increment  NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `deleted_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `user_level` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`users_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '사용자';

