CREATE TABLE saved_posts (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    saved_at DATETIME(6) NOT NULL,
    CONSTRAINT uk_saved_post_user UNIQUE (post_id, user_id),
    CONSTRAINT fk_saved_posts_post FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
    CONSTRAINT fk_saved_posts_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_saved_posts_user_saved_at ON saved_posts(user_id, saved_at DESC);
