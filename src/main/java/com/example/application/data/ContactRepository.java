package com.example.application.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c " +
            "WHERE lower(c.firstName) LIKE lower(concat('%', :searchTerm, '%')) " +
            "OR lower(c.lastName) LIKE lower(concat('%', :searchTerm, '%'))")
    List<Contact> search(@Param("searchTerm") String searchTerm);

}
