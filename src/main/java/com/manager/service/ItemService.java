package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.ItemDTO;
import com.manager.exception.EntityNotFound;
import com.manager.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemService {

    @Autowired
    ItemRepository itemRepository;

    public List<ItemDTO> getItems() {
        return itemRepository.findAll()
                .stream()
                .map(Item::toDTO)
                .collect(Collectors.toList());
    }

    public ItemDTO getItemById(Long id) throws EntityNotFound {
        return Item.toDTO(getEntityByIdIfExists(id));
    }

    public ItemDTO createItem(ItemDTO dto) {
        Item item = Item.fromDTO(dto);
        return Item.toDTO(itemRepository.save(item));
    }

    public ItemDTO updateItem(Long id, ItemDTO dto) throws EntityNotFound {
        Item itemDatabase = getEntityByIdIfExists(id);

        if (dto.getName() != null && !dto.getName().isEmpty()) {
            itemDatabase.setName(dto.getName());
        }

        return Item.toDTO(itemRepository.save(itemDatabase));
    }

    public void deleteItemById(Long id) {
        itemRepository.deleteById(id);
    }

    private Item getEntityByIdIfExists(Long id) throws EntityNotFound {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isPresent()) {
            return optionalItem.get();
        } else {
            throw new EntityNotFound(String.format("Item with id %s not found ", id));
        }
    }
}
