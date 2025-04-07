package com.example.spring_security_crud_api.repository;

import com.example.spring_security_crud_api.model.Item;
import com.example.spring_security_crud_api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCreatedBy(User user);
    List<Item> findByNameContainingIgnoreCase(String name);
}