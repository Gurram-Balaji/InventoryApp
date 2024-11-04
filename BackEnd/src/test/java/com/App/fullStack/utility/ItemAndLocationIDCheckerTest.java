package com.App.fullStack.utility;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemAndLocationIDCheckerTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private ItemAndLocationIDChecker idChecker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateItemAndLocationID_ValidIDs() {
        // Arrange
        String itemId = "item123";
        String locationId = "location123";
        when(itemRepository.existsByItemId(itemId)).thenReturn(true);
        when(locationRepository.existsByLocationId(locationId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> idChecker.validateItemAndLocationID(itemId, locationId));
        verify(itemRepository, times(1)).existsByItemId(itemId);
        verify(locationRepository, times(1)).existsByLocationId(locationId);
    }

    @Test
    void testValidateItemAndLocationID_InvalidItemID() {
        // Arrange
        String itemId = "invalidItem";
        String locationId = "location123";
        when(itemRepository.existsByItemId(itemId)).thenReturn(false);
        when(locationRepository.existsByLocationId(locationId)).thenReturn(true);

        // Act & Assert
        FoundException exception = assertThrows(FoundException.class, () ->
                idChecker.validateItemAndLocationID(itemId, locationId)
        );

        // Verify exception message
        assertEquals("Item with ItemId: " + itemId + " not found.", exception.getMessage());

        // Verify item repository interaction
        verify(itemRepository, times(1)).existsByItemId(itemId);

        // Verify that location repository interaction was never called
        verify(locationRepository, never()).existsByLocationId(locationId);
    }


    @Test
    void testValidateItemAndLocationID_InvalidLocationID() {
        // Arrange
        String itemId = "item123";
        String locationId = "invalidLocation";
        when(itemRepository.existsByItemId(itemId)).thenReturn(true);
        when(locationRepository.existsByLocationId(locationId)).thenReturn(false);

        // Act & Assert
        FoundException exception = assertThrows(FoundException.class, () -> 
            idChecker.validateItemAndLocationID(itemId, locationId)
        );
        assertEquals("Location with LocationId: " + locationId + " not found.", exception.getMessage());
        verify(itemRepository, times(1)).existsByItemId(itemId);
        verify(locationRepository, times(1)).existsByLocationId(locationId);
    }

    @Test
    void testValidateItemAndLocationID_NullIDs() {
        // Arrange
        String itemId = null;
        String locationId = null;

        // Act & Assert
        assertDoesNotThrow(() -> idChecker.validateItemAndLocationID(itemId, locationId));
        verify(itemRepository, never()).existsByItemId(any());
        verify(locationRepository, never()).existsByLocationId(any());
    }

    @Test
    void testValidateItemAndLocationID_ItemNull_LocationValid() {
        // Arrange
        String itemId = null;
        String locationId = "location123";
        when(locationRepository.existsByLocationId(locationId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> idChecker.validateItemAndLocationID(itemId, locationId));
        verify(locationRepository, times(1)).existsByLocationId(locationId);
        verify(itemRepository, never()).existsByItemId(any());
    }

    @Test
    void testValidateItemAndLocationID_ItemValid_LocationNull() {
        // Arrange
        String itemId = "item123";
        String locationId = null;
        when(itemRepository.existsByItemId(itemId)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> idChecker.validateItemAndLocationID(itemId, locationId));
        verify(itemRepository, times(1)).existsByItemId(itemId);
        verify(locationRepository, never()).existsByLocationId(any());
    }
}
