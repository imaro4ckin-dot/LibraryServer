package org.example.libraryserver.repository;

import org.example.libraryserver.model.CheckoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface CheckoutRepository extends JpaRepository<CheckoutEntity, Integer> {

    List<CheckoutEntity> findByUserIdAndReturnedAtIsNull(int userId);

    Optional<CheckoutEntity> findByBookIdAndUserIdAndReturnedAtIsNull(int bookId, int userId);

    Optional<CheckoutEntity> findByBookIdAndReturnedAtIsNull(int bookId);

    @Query("SELECT c FROM CheckoutEntity c WHERE c.returnedAt IS NULL")
    List<CheckoutEntity> findAllActive();

    void deleteByBookId(int bookId);

    void deleteByUserId(int userId);
}
