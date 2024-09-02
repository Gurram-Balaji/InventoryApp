package com.App.fullStack.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.LocationRepository;

@Service
public class ItemAndLocationIDChecker {

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public LocationRepository locationRepository;

    public void validateItemAndLocationID(String itemId, String LocationId) {
        if (itemId != null && !itemRepository.existsByItemId(itemId))
            throw new FoundException("Item with ItemId: " + itemId + " not found.");

        if (LocationId != null && !locationRepository.existsByLocationId(LocationId))
            throw new FoundException("Location with LocationId: " + LocationId + " not found.");
    }

}
