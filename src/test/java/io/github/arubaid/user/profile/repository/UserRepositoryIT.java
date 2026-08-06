package io.github.arubaid.user.profile.repository;

import io.github.arubaid.testsupport.integration.AbstractIntegrationTest;
import io.github.arubaid.user.profile.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional //Automatically rolls back after each test
class UserRepositoryIT extends AbstractIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldSaveUser() {
        User user = new User();
        user.setEmail("john@example.com");
        user.setPassword("sample");

        User savedUser = repository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("john@example.com");
        assertThat(repository.count()).isEqualTo(1);
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = new User();
        user.setEmail("jane@example.com");
        user.setPassword("password123");
        repository.save(user);

        // When
        Optional<User> foundUser = repository.findByEmail("jane@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jane@example.com");
        assertThat(foundUser.get().getPassword()).isEqualTo("password123");
    }

    @Test
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        // When
        Optional<User> foundUser = repository.findByEmail("nonexistent@example.com");

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPassword("password123");
        repository.save(user);

        // When & Then
        assertThat(repository.existsByEmail("existing@example.com")).isTrue();
        assertThat(repository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void shouldThrowExceptionWhenSavingUserWithDuplicateEmail() {
        // Given
        User user1 = new User();
        user1.setEmail("duplicate@example.com");
        user1.setPassword("password1");
        repository.saveAndFlush(user1);

        // When
        User user2 = new User();
        user2.setEmail("duplicate@example.com");
        user2.setPassword("password2");

        // Then
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(user2));
    }

    @Test
    void shouldUpdateUser() {
        // Given
        User user = new User();
        user.setEmail("update@example.com");
        user.setPassword("oldPassword");
        User savedUser = repository.save(user);

        // When
        savedUser.setPassword("newPassword");
        repository.save(savedUser);

        // Then
        Optional<User> updatedUser = repository.findById(savedUser.getId());
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getPassword()).isEqualTo("newPassword");
        assertThat(updatedUser.get().getEmail()).isEqualTo("update@example.com");
    }

    @Test
    void shouldDeleteUser() {
        // Given
        User user = new User();
        user.setEmail("delete@example.com");
        user.setPassword("password123");
        User savedUser = repository.save(user);

        // When
        repository.deleteById(savedUser.getId());

        // Then
        assertThat(repository.findById(savedUser.getId())).isEmpty();
        assertThat(repository.count()).isZero();
    }

    @Test
    void shouldBeAbleToSaveUserWithNullFields() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(null);  // password could be null

        // When
        User savedUser = repository.save(user);

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getPassword()).isNull();
    }
}