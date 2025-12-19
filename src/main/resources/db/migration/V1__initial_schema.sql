-- Users table
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `email` VARCHAR(255) NOT NULL UNIQUE,
  `password` VARCHAR(255),
  `name` VARCHAR(255) NOT NULL,
  `username` VARCHAR(255),
  `profile_picture_url` VARCHAR(500),
  `bio` TEXT,
  `account_type` VARCHAR(50) DEFAULT 'CREATOR',
  `plan_type` VARCHAR(50) DEFAULT 'BASIC',
  `is_verified` BOOLEAN DEFAULT FALSE,
  `rating` DECIMAL(3,2) DEFAULT 0.00,
  `instagram_id` VARCHAR(255),
  `instagram_access_token` VARCHAR(500),
  `oauth_provider` VARCHAR(50),
  `is_active` BOOLEAN DEFAULT TRUE,
  `is_banned` BOOLEAN DEFAULT FALSE,
  `ban_reason` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  INDEX `idx_email` (`email`),
  INDEX `idx_instagram_id` (`instagram_id`),
  INDEX `idx_plan_type` (`plan_type`),
  INDEX `idx_is_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- User photos table
CREATE TABLE IF NOT EXISTS `user_photos` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `photo_url` VARCHAR(500) NOT NULL,
  `photo_key` VARCHAR(500),
  `caption` VARCHAR(500),
  `is_from_instagram` BOOLEAN DEFAULT FALSE,
  `instagram_media_id` VARCHAR(255),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Requests table (shoutout requests)
CREATE TABLE IF NOT EXISTS `requests` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `sender_id` BIGINT NOT NULL,
  `receiver_id` BIGINT NOT NULL,
  `photo_id` BIGINT NOT NULL,
  `status` VARCHAR(50) DEFAULT 'PENDING',
  `sender_repost_status` VARCHAR(50),
  `receiver_repost_status` VARCHAR(50),
  `deadline` DATETIME NOT NULL,
  `repost_completed_at` DATETIME,
  `rating_by_sender` INT,
  `rating_by_receiver` INT,
  `sender_comment` TEXT,
  `receiver_comment` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`photo_id`) REFERENCES `user_photos` (`id`) ON DELETE CASCADE,
  INDEX `idx_sender_id` (`sender_id`),
  INDEX `idx_receiver_id` (`receiver_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_deadline` (`deadline`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Analytics table
CREATE TABLE IF NOT EXISTS `analytics` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `request_id` BIGINT,
  `reach` INT DEFAULT 0,
  `profile_visits` INT DEFAULT 0,
  `clicks` INT DEFAULT 0,
  `followers_gained` INT DEFAULT 0,
  `metric_date` DATE NOT NULL,
  `metric_type` VARCHAR(50),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_metric_date` (`metric_date`),
  INDEX `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Strikes table (for violation tracking)
CREATE TABLE IF NOT EXISTS `strikes` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `reason` VARCHAR(255) NOT NULL,
  `request_id` BIGINT,
  `description` TEXT,
  `strike_count` INT DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Notifications table
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `notification_type` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255),
  `message` TEXT NOT NULL,
  `related_user_id` BIGINT,
  `related_request_id` BIGINT,
  `is_read` BOOLEAN DEFAULT FALSE,
  `action_url` VARCHAR(500),
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`related_user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  FOREIGN KEY (`related_request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_is_read` (`is_read`),
  INDEX `idx_created_at` (`created_at`),
  INDEX `idx_notification_type` (`notification_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Payments table
CREATE TABLE IF NOT EXISTS `payments` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `user_id` BIGINT NOT NULL,
  `plan_type` VARCHAR(50) NOT NULL,
  `amount` DECIMAL(10,2) NOT NULL,
  `currency` VARCHAR(10) DEFAULT 'INR',
  `payment_gateway` VARCHAR(50) NOT NULL,
  `transaction_id` VARCHAR(255) UNIQUE,
  `payment_status` VARCHAR(50) DEFAULT 'PENDING',
  `payment_method` VARCHAR(50),
  `order_id` VARCHAR(255),
  `razorpay_payment_id` VARCHAR(255),
  `razorpay_order_id` VARCHAR(255),
  `razorpay_signature` VARCHAR(500),
  `error_message` TEXT,
  `valid_from` DATE,
  `valid_till` DATE,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  INDEX `idx_user_id` (`user_id`),
  INDEX `idx_transaction_id` (`transaction_id`),
  INDEX `idx_payment_status` (`payment_status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Reports/Flags table
CREATE TABLE IF NOT EXISTS `reports` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `reporter_id` BIGINT NOT NULL,
  `reported_user_id` BIGINT NOT NULL,
  `request_id` BIGINT,
  `reason` VARCHAR(255) NOT NULL,
  `description` TEXT,
  `status` VARCHAR(50) DEFAULT 'OPEN',
  `admin_notes` TEXT,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `resolved_at` DATETIME,
  FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`reported_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  FOREIGN KEY (`request_id`) REFERENCES `requests` (`id`) ON DELETE SET NULL,
  INDEX `idx_reporter_id` (`reporter_id`),
  INDEX `idx_reported_user_id` (`reported_user_id`),
  INDEX `idx_status` (`status`),
  INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
