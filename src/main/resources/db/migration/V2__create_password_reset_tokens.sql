-- V2__create_password_reset_tokens.sql

CREATE TABLE password_reset_tokens (
                                       id UUID PRIMARY KEY,
                                       user_id UUID NOT NULL,
                                       token_hash VARCHAR(64) NOT NULL,
                                       expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
                                       used_at TIMESTAMP WITH TIME ZONE,
                                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                       CONSTRAINT fk_password_reset_tokens_user
                                           FOREIGN KEY (user_id)
                                               REFERENCES users(id)
                                               ON DELETE CASCADE,

                                       CONSTRAINT uc_password_reset_tokens_hash
                                           UNIQUE (token_hash)
);

CREATE INDEX idx_password_reset_tokens_user_id
    ON password_reset_tokens(user_id);

CREATE INDEX idx_password_reset_tokens_expires_at
    ON password_reset_tokens(expires_at);