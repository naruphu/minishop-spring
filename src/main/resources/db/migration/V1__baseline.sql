CREATE TABLE `user` (
                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                        `email` VARCHAR(255) NOT NULL,
                        `password` VARCHAR(255) NOT NULL,
                        `role` ENUM('ADMIN', 'CUSTOMER') NOT NULL,
                        `username` VARCHAR(255) NOT NULL,

                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`),
                        UNIQUE KEY `UKsb8bbouer5wak8vyiiy4pf2bx` (`username`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE `category` (
                            `id` INT NOT NULL AUTO_INCREMENT,
                            `name` VARCHAR(255) DEFAULT NULL,

                            PRIMARY KEY (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE `product` (
                           `id` BIGINT NOT NULL AUTO_INCREMENT,
                           `name` VARCHAR(255) DEFAULT NULL,
                           `price` DECIMAL(38,2) DEFAULT NULL,
                           `quantity` INT DEFAULT NULL,
                           `category_id` INT DEFAULT NULL,

                           PRIMARY KEY (`id`),

                           KEY `FK1mtsbur82frn64de7balymq9s` (`category_id`),

                           CONSTRAINT `FK1mtsbur82frn64de7balymq9s`
                               FOREIGN KEY (`category_id`)
                                   REFERENCES `category` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE `cart_item` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `quantity` INT NOT NULL,
                             `product_id` BIGINT NOT NULL,
                             `user_id` BIGINT NOT NULL,

                             PRIMARY KEY (`id`),

                             UNIQUE KEY `UK2dhdhp9i9ld88nv1bejg9hbx7`
                                 (`user_id`, `product_id`),

                             KEY `FKjcyd5wv4igqnw413rgxbfu4nv` (`product_id`),

                             CONSTRAINT `FKjcyd5wv4igqnw413rgxbfu4nv`
                                 FOREIGN KEY (`product_id`)
                                     REFERENCES `product` (`id`),

                             CONSTRAINT `FKjnaj4sjyqjkr4ivemf9gb25w`
                                 FOREIGN KEY (`user_id`)
                                     REFERENCES `user` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE `orders` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `created_at` DATETIME(6) NOT NULL,
                          `status` ENUM(
        'PENDING',
        'CONFIRMED',
        'SHIPPING',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL,
                          `total_amount` DECIMAL(38,2) NOT NULL,
                          `user_id` BIGINT NOT NULL,

                          PRIMARY KEY (`id`),

                          KEY `FKel9kyl84ego2otj2accfd8mr7` (`user_id`),

                          CONSTRAINT `FKel9kyl84ego2otj2accfd8mr7`
                              FOREIGN KEY (`user_id`)
                                  REFERENCES `user` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;


CREATE TABLE `order_item` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `price` DECIMAL(38,2) NOT NULL,
                              `quantity` INT NOT NULL,
                              `order_id` BIGINT NOT NULL,
                              `product_id` BIGINT NOT NULL,

                              PRIMARY KEY (`id`),

                              KEY `FKt4dc2r9nbvbujrljv3e23iibt` (`order_id`),
                              KEY `FK551losx9j75ss5d6bfsqvijna` (`product_id`),

                              CONSTRAINT `FK551losx9j75ss5d6bfsqvijna`
                                  FOREIGN KEY (`product_id`)
                                      REFERENCES `product` (`id`),

                              CONSTRAINT `FKt4dc2r9nbvbujrljv3e23iibt`
                                  FOREIGN KEY (`order_id`)
                                      REFERENCES `orders` (`id`)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_general_ci;