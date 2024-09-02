package com.App.fullStack.service;

import com.App.fullStack.exception.FoundException;
import com.App.fullStack.pojos.Item;
import com.App.fullStack.repositories.DemandRepository;
import com.App.fullStack.repositories.ItemRepository;
import com.App.fullStack.repositories.SupplyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ItemService {

    @Autowired
    public ItemRepository itemRepository;

    @Autowired
    public SupplyRepository supplyRepository;

    @Autowired
    public DemandRepository demandRepository;

    public Page<Item> getAllItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> Item = itemRepository.findAll(pageable);

        if (Item.isEmpty()) throw new FoundException("Items records not found.");

        return Item;
    }

    public Item getItemByItemId(String itemId) {
        Optional<Item> existingItem = itemRepository.findByItemId(itemId);

        if (existingItem.isPresent()) return existingItem.get();

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

    public Item updateItem(String itemId, Item itemDetails) {
        Optional<Item> itemOptional = itemRepository.findByItemId(itemId);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();

            // Update fields only if they are not null
            if (itemDetails.getItemDescription() != null) {
                item.setItemDescription(itemDetails.getItemDescription());
            }
            if (itemDetails.getStatus() != null) {
                item.setStatus(itemDetails.getStatus());
            }
            if (itemDetails.getPrice() != null) {
                item.setPrice(itemDetails.getPrice());
            }
            return itemRepository.save(item);
        }

        throw new FoundException("Item with itemId " + itemId + " not exist.");
    }

    public Item updateItemFulfillmentOptions(String itemId, Item itemDetails) {
        Optional<Item> itemOptional = itemRepository.findByItemId(itemId);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            // Update boolean fields directly
            item.setPickupAllowed(itemDetails.isPickupAllowed());
            item.setShippingAllowed(itemDetails.isShippingAllowed());
            item.setDeliveryAllowed(itemDetails.isDeliveryAllowed());
            return itemRepository.save(item);
        }

        throw new FoundException("Item with itemId " + itemId + " not exist.");
    }

    public String deleteItem(String itemId) {
        // Check if any supply or demand exists for this item
        if (supplyRepository.existsByItemId(itemId) || demandRepository.existsByItemId(itemId))
            // Throw an exception or handle the case where deletion is not allowed
            throw new FoundException("Item cannot be deleted because it has associated supply or demand records.");

        if (itemRepository.existsByItemId(itemId)) {
            // If no supply or demand exists, delete the item
            itemRepository.deleteByItemId(itemId);
            return "Item deleted successfully.";
        }

        throw new FoundException("Item with itemId " + itemId + " not exist.");
    }
}
