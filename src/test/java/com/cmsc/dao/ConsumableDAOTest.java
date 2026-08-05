package com.cmsc.dao;

import com.cmsc.model.Consumable;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests ConsumableDAOImpl against the real CMSC database.
 *
 * @author Saleh Alhajj
 */
class ConsumableDAOTest {

    private final ConsumableDAO consumableDAO
            = new ConsumableDAOImpl();

    @Test
    void savesFindsUpdatesAndDeletesConsumable() {
        String type = "Test Filament-" + System.currentTimeMillis();

        Consumable testConsumable = new Consumable(
                0,
                type,
                "g",
                50.0,
                10.0,
                100.0
        );

        Consumable savedConsumable
                = consumableDAO.save(testConsumable);

        int generatedId = savedConsumable.getConsumableId();

        assertTrue(generatedId > 0);

        try {
            Consumable foundConsumable
                    = consumableDAO.findById(generatedId);

            assertNotNull(foundConsumable);
            assertEquals(type, foundConsumable.getType());
            assertEquals("g", foundConsumable.getUnit());
            assertTrue(foundConsumable.isLowStock());

            Consumable foundByType
                    = consumableDAO.findByType(type);

            assertNotNull(foundByType);
            assertEquals(generatedId, foundByType.getConsumableId());

            List<Consumable> lowStock
                    = consumableDAO.findLowStock();

            assertTrue(lowStock.stream()
                    .anyMatch(consumable
                            -> consumable.getConsumableId() == generatedId));

            List<Consumable> allConsumables
                    = consumableDAO.findAll();

            assertTrue(allConsumables.stream()
                    .anyMatch(consumable
                            -> consumable.getConsumableId() == generatedId));

            savedConsumable.updateStock(250.0);
            consumableDAO.update(savedConsumable);

            Consumable updatedConsumable
                    = consumableDAO.findById(generatedId);

            assertNotNull(updatedConsumable);
            assertEquals(
                    250.0,
                    updatedConsumable.getStockLevel(),
                    0.001
            );

            List<Consumable> updatedLowStock
                    = consumableDAO.findLowStock();

            assertTrue(updatedLowStock.stream()
                    .noneMatch(consumable
                            -> consumable.getConsumableId() == generatedId));
        } finally {
            consumableDAO.delete(generatedId);
        }

        assertNull(consumableDAO.findById(generatedId));
        assertNull(consumableDAO.findByType(type));
    }
}