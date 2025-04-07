package com.example.spring_security_crud_api.service;

import com.example.spring_security_crud_api.dto.ItemDto;
import com.example.spring_security_crud_api.model.Item;
import com.example.spring_security_crud_api.model.User;
import com.example.spring_security_crud_api.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public List<Item> findByUser(User user) {
        return itemRepository.findByCreatedBy(user);
    }

    public List<Item> searchItems(String query) {
        return itemRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    @Transactional
    public Optional<Item> updateItem(Long id, ItemDto itemDto, User currentUser) {
        return itemRepository.findById(id)
                .map(existingItem -> {
                    // Check if user is owner or admin
                    if (!isOwnerOrAdmin(existingItem, currentUser)) {
                        throw new SecurityException("Not authorized to update this item");
                    }

                    existingItem.setName(itemDto.getName());
                    existingItem.setDescription(itemDto.getDescription());
                    existingItem.setPrice(itemDto.getPrice());

                    return itemRepository.save(existingItem);
                });
    }

    @Transactional
    public boolean deleteItem(Long id, User currentUser) {
        return itemRepository.findById(id)
                .map(item -> {
                    if (!isOwnerOrAdmin(item, currentUser)) {
                        throw new SecurityException("Not authorized to delete this item");
                    }

                    itemRepository.delete(item);
                    return true;
                })
                .orElse(false);
    }

    private boolean isOwnerOrAdmin(Item item, User user) {
        return item.getCreatedBy().equals(user) ||
                user.getRoles().contains("ADMIN");
    }

    // Convert Item to ItemDto
    public ItemDto toDto(Item item) {
        ItemDto dto = new ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setPrice(item.getPrice());
        return dto;
    }

    // Convert list of Items to list of ItemDtos
    public List<ItemDto> toDtoList(List<Item> items) {
        return items.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // Convert ItemDto to Item
    public Item toEntity(ItemDto dto) {
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setPrice(dto.getPrice());
        return item;
    }
}