package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.ItemDTO;
import com.manager.exception.EntityNotFound;
import com.manager.repository.ItemRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ItemServiceTests {

    @InjectMocks
    private ItemService itemService;
    @Mock
    private ItemRepository itemRepository;

    Item item1 = new Item(1L, "item 1");
    Item item2 = new Item(2L, "item 2");
    Item item3 = new Item(3L, "item 3");
    Item itemToSave = new Item("item 3");

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        Mockito.when(itemRepository.findById(1l)).thenReturn(Optional.of(item1));
        Mockito.when(itemRepository.findById(2l)).thenReturn(Optional.of(item2));
        Mockito.when(itemRepository.findAll()).thenReturn(items);

        Item itemToUpdate = item1;
        itemToUpdate.setName(itemToSave.getName());
        Mockito.when(itemRepository.save(itemToUpdate)).thenReturn(itemToUpdate);
    }

    @Test
    public void getItemsTest() {
        List<ItemDTO> result = itemService.getItems();
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void getItemByIdTest() {
        try {
            ItemDTO result = itemService.getItemById(item1.getId());
            Assertions.assertThat(result.getId()).isEqualTo(item1.getId());
            Assertions.assertThat(result.getName()).isEqualTo(item1.getName());
        } catch (EntityNotFound e) {
            Assertions.fail("EntityNotFound should not be thrown");
        }
    }

    @Test
    public void getItemByIdFail() {
        Assertions.assertThatThrownBy(() -> itemService.getItemById(3L)).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void updateItemTest() {
        try {
            ItemDTO result = itemService.updateItem(1l, Item.toDTO(itemToSave));
            Assertions.assertThat(result.getName()).isEqualTo(itemToSave.getName());
        } catch (EntityNotFound e) {
            Assertions.fail("EntityNotFound should not be thrown");
        }

    }

    @Test
    public void updateItemTestFail() {
        Assertions.assertThatThrownBy(
                () -> itemService.updateItem(3l, Item.toDTO(itemToSave))
        ).isInstanceOf(EntityNotFound.class);
    }
}
