package com.manager.service;

import com.manager.entity.Item;
import com.manager.entity.StockMovement;
import com.manager.entity.StockMovementDTO;
import com.manager.entity.StockMovementStatus;
import com.manager.exception.ChangeNotAllowed;
import com.manager.exception.EntityNotFound;
import com.manager.repository.ItemRepository;
import com.manager.repository.StockMovementRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockMovementService {

    @Autowired
    StockMovementRepository stockMovementRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    OrderProcessorService orderProcessorService;

    private static final Logger logger = LogManager.getLogger(StockMovementService.class);

    public List<StockMovementDTO> getAllStockMovements() {
        return stockMovementRepository.findAll()
                .stream()
                .map(StockMovement::toDTO)
                .collect(Collectors.toList());
    }

    public List<StockMovementDTO> getStockMovementsByItem(Long itemId) {
        return stockMovementRepository.findAllByItemId(itemId)
                .stream()
                .map(StockMovement::toDTO)
                .collect(Collectors.toList());
    }

    public StockMovementDTO getStockMovementById(Long id) throws EntityNotFound {
        return StockMovement.toDTO(getEntityByIdIfExists(id));
    }

    public StockMovementDTO createStockMovement(StockMovementDTO dto) throws ChangeNotAllowed {
        validationEntity(dto);
        StockMovement stockMovement = StockMovement.fromDTO(dto);
        stockMovement.setCreationDate(new Date());
        stockMovement.setStatus(StockMovementStatus.NEW.name());
        stockMovement = stockMovementRepository.save(stockMovement);
        // log stock movement created
        logger.info(String.format("Stock Movement created to item %s and quantity %s", dto.getItemId(), dto.getQuantity()));
        // run async process to deliver orders
        orderProcessorService.processOrdersFromItem(dto.getItemId());
        return StockMovement.toDTO(stockMovement);
    }

    public StockMovementDTO updateStockMovement(Long id, StockMovementDTO dto)
            throws EntityNotFound, ChangeNotAllowed {
        StockMovement stockMovement = getEntityByIdIfExists(id);
        checkCanChangeStockMovement(stockMovement);
        validationEntity(dto);
        StockMovement.merge(stockMovement, dto);
        stockMovement = stockMovementRepository.save(stockMovement);
        // log stock movement updated
        logger.info(String.format("Stock Movement updated to item %s and quantity %s",
                stockMovement.getItem().getId(), stockMovement.getQuantity()));
        // run async process to deliver orders
        orderProcessorService.processOrdersFromItem(stockMovement.getItem().getId());
        return StockMovement.toDTO(stockMovement);
    }

    public void deleteStockMovementById(Long id)
            throws EntityNotFound, ChangeNotAllowed {
        StockMovement stockMovement = getEntityByIdIfExists(id);
        checkCanChangeStockMovement(stockMovement);
        stockMovementRepository.deleteById(id);
    }

    private StockMovement getEntityByIdIfExists(Long id) throws EntityNotFound {
        Optional<StockMovement> optional = stockMovementRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new EntityNotFound(String.format("Stock Movement with id %s not found ", id));
        }
    }

    private void checkCanChangeStockMovement(StockMovement stockMovement) throws ChangeNotAllowed {
        if (!StockMovementStatus.NEW
                .equals(StockMovementStatus.valueOf(stockMovement.getStatus()))) {
            throw new ChangeNotAllowed(
                    String.format("StockMovement with id %s can not be changed because it status is %s"
                            , stockMovement.getId()
                            , stockMovement.getStatus()));
        }
    }

    private void validationEntity(StockMovementDTO dto) throws ChangeNotAllowed {
        Optional<Item> optionalItem = itemRepository.findById(dto.getItemId());
        if (!optionalItem.isPresent()) {
            throw new ChangeNotAllowed(String.format("item %s invalid", dto.getItemId()));
        }

        if (dto.getQuantity() <= 0) {
            throw new ChangeNotAllowed("quantity must be greater than 0");
        }
    }
}
