package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.StockMovement;
import com.manager.entity.StockMovementDTO;
import com.manager.entity.StockMovementStatus;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.repository.StockMovementRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class StockMovementServiceTests {

    @InjectMocks
    private StockMovementService stockMovementService;
    @Mock
    private StockMovementRepository stockMovementRepository;

    private static final Long FINISHED_STOCK_MOVEMENT_ID = 1L;
    private static final Long NEW_STOCK_MOVEMENT_ID = 2L;
    private static final Long NEW_STOCK_MOVEMENT_ID_TO_UPDATE = 3L;
    private static final Long NOT_FOUND_STOCK_MOVEMENT_ID = 5L;
    private static final Date CREATION_DATE = new Date();

    private static final Long ITEM_ID = 1L;
    private static final Long QUANTITY = 5L;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        // Prepare find scenario
        StockMovement stockMovementFinished = createStockMovement(FINISHED_STOCK_MOVEMENT_ID
                , StockMovementStatus.FINISHED.name());
        Mockito.when(stockMovementRepository.findById(FINISHED_STOCK_MOVEMENT_ID))
                .thenReturn(Optional.of(stockMovementFinished));

        // Prepare find scenario
        StockMovement stockMovementNew = createStockMovement(NEW_STOCK_MOVEMENT_ID
                , StockMovementStatus.NEW.name());
        Mockito.when(stockMovementRepository.findById(NEW_STOCK_MOVEMENT_ID))
                .thenReturn(Optional.of(stockMovementNew));

        // Prepare find by item scenario
        List<StockMovement> stockMovementList = new ArrayList<>();
        stockMovementList.add(stockMovementFinished);
        stockMovementList.add(stockMovementNew);
        Mockito.when(stockMovementRepository.findAllByItemId(ITEM_ID))
                .thenReturn(stockMovementList);

        // Prepare update scenario
        StockMovement stockMovementNewToUpdate = createStockMovement(NEW_STOCK_MOVEMENT_ID_TO_UPDATE
                , StockMovementStatus.NEW.name());
        Mockito.when(stockMovementRepository.findById(NEW_STOCK_MOVEMENT_ID_TO_UPDATE))
                .thenReturn(Optional.of(stockMovementNewToUpdate));
        stockMovementNewToUpdate.setItem(new Item(2l, null));
        Mockito.when(stockMovementRepository.save(stockMovementNewToUpdate))
                .thenReturn(stockMovementNewToUpdate);

    }

    @Test
    public void getStockMovementsByItemTest() {
        List<StockMovementDTO> result = stockMovementService.getStockMovementsByItem(ITEM_ID);
        Assertions.assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void getOrderByIdTest() {
        try {
            StockMovementDTO result = stockMovementService.getStockMovementById(NEW_STOCK_MOVEMENT_ID);
            StockMovement stockMovementNew = createStockMovement(NEW_STOCK_MOVEMENT_ID, StockMovementStatus.NEW.name());
            StockMovementDTO stockMovementDTO = StockMovement.toDTO(stockMovementNew);
            Assertions.assertThat(result.getId()).isEqualTo(stockMovementDTO.getId());
            Assertions.assertThat(result.getItemId()).isEqualTo(stockMovementDTO.getItemId());
            Assertions.assertThat(result.getQuantity()).isEqualTo(stockMovementDTO.getQuantity());
        } catch (EntityNotFound e) {
            Assertions.fail(String.format("%s should not be thrown", e.getClass().getName()));
        }
    }

    @Test
    public void updateOrderEntityNotFoundTest() {
        StockMovementDTO dto = createStockMovementDTO(NOT_FOUND_STOCK_MOVEMENT_ID);
        Assertions.assertThatThrownBy(
                () -> stockMovementService.updateStockMovement(NOT_FOUND_STOCK_MOVEMENT_ID, dto)
        ).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void updateOrderChangeNotAllowedTest() {
        StockMovementDTO dto = createStockMovementDTO(FINISHED_STOCK_MOVEMENT_ID);
        Assertions.assertThatThrownBy(
                () -> stockMovementService.updateStockMovement(
                        FINISHED_STOCK_MOVEMENT_ID,
                        dto)
        ).isInstanceOf(ChangeNotAllowed.class);
    }

    @Test
    public void deleteOrderTest() {
        Assertions.assertThatNoException()
                .isThrownBy(() -> stockMovementService.deleteStockMovementById(NEW_STOCK_MOVEMENT_ID));
    }

    @Test
    public void deleteOrderEntityNotFoundTest() {
        Assertions.assertThatThrownBy(
                () -> stockMovementService.deleteStockMovementById(NOT_FOUND_STOCK_MOVEMENT_ID)
        ).isInstanceOf(EntityNotFound.class);
    }

    @Test
    public void deleteOrderChangeNotAllowedTest() {
        Assertions.assertThatThrownBy(
                () -> stockMovementService.deleteStockMovementById(FINISHED_STOCK_MOVEMENT_ID)
        ).isInstanceOf(ChangeNotAllowed.class);
    }

    private StockMovement createStockMovement(Long id, String status) {
        StockMovement stockMovement = new StockMovement();
        stockMovement.setId(id);
        stockMovement.setCreationDate(CREATION_DATE);
        stockMovement.setStatus(status);
        stockMovement.setItem(new Item(ITEM_ID, null));
        stockMovement.setQuantity(QUANTITY);
        return stockMovement;
    }

    private StockMovementDTO createStockMovementDTO(Long id) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(id);
        dto.setCreationDate(CREATION_DATE);
        dto.setItemId(ITEM_ID);
        dto.setQuantity(QUANTITY);
        return dto;
    }
}
