package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.SupplyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private SupplyRepository supplyRepository;

    @Autowired
    private DemandRepository demandRepository;

    public List<Item> getAllItems() {

        try {
            return itemRepository.findAll();
        } catch (Exception e) {
            throw new FoundException("Items not exist.");
        }
    }

    public Item getItemByItemId(String itemId) {
        Optional<Item> existingItem = itemRepository.findByItemId(itemId);
        if (existingItem.isPresent())
            return existingItem.get();
        else
            throw new FoundException("Item with itemId " + itemId + " not exist.");

    }

    public Item addItem(Item item) {
        // Check if an item with the same itemId already exists
        Optional<Item> existingItem = itemRepository.findByItemId(item.getItemId());
        if (existingItem.isPresent())
            // Throw an exception or handle the case where the itemId already exists
            throw new FoundException("Item with itemId " + item.getItemId() + " already exists.");
        // If the itemId does not exist, save the new item
        return itemRepository.save(item);
    }

    public Item updateItem(String itemid, Item itemDetails) {
        Optional<Item> itemOptional = itemRepository.findByItemId(itemid);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            // Update fields only if they are not null
            if (itemDetails.getItemDescription() != null) {
                item.setItemDescription(itemDetails.getItemDescription());
            }
            if (itemDetails.getCategory() != null) {
                item.setCategory(itemDetails.getCategory());
            }
            if (itemDetails.getType() != null) {
                item.setType(itemDetails.getType());
            }
            if (itemDetails.getStatus() != null) {
                item.setStatus(itemDetails.getStatus());
            }
            if (itemDetails.getPrice() != null) {
                item.setPrice(itemDetails.getPrice());
            }
            // Update boolean fields directly
            item.setPickupAllowed(itemDetails.isPickupAllowed());
            item.setShippingAllowed(itemDetails.isShippingAllowed());
            item.setDeliveryAllowed(itemDetails.isDeliveryAllowed());
            return itemRepository.save(item);
        } else {
            throw new FoundException("Item with itemId " + itemid + " not exist.");

        }
    }

    public String deleteItem(String itemid) {
        // Check if any supply or demand exists for this item
        boolean supplyExists = supplyRepository.existsByItemId(itemid);
        boolean demandExists = demandRepository.existsByItemId(itemid);

        if (supplyExists || demandExists) {
            // Throw an exception or handle the case where deletion is not allowed
            throw new FoundException("Item cannot be deleted because it has associated supply or demand records.");
        }

        boolean itemExists = itemRepository.existsByItemId(itemid);

        if (itemExists) {
            // If no supply or demand exists, delete the item
            itemRepository.deleteByItemId(itemid);
            return "Item deleted successfully.";
        } else
            throw new FoundException("Item with itemId " + itemid + " not exist.");
    }
}
