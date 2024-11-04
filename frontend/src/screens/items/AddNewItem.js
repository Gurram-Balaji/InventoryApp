import { useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Checkbox, TextField, FormControl, FormControlLabel, InputLabel, MenuItem, Select } from '@mui/material';
import { errorToast, successToast } from '../../components/Toast';
import apiClient from '../../components/baseUrl';

export default function NewItemForm({ statusOptions, fetchItems, page, setOpenAddDialog, openAddDialog }) {

    const [newItemData, setNewItemData] = useState({
        itemId: '',
        itemDescription: '',
        category: '',
        type: '',
        status: 'ACTIVE',
        price: '',
        pickupAllowed: true,
        shippingAllowed: true,
        deliveryAllowed: true
    });


    const handleAddSave = async () => {
        // Validate fields
        const { itemId, itemDescription, category, type, price } = newItemData;
        if (!itemId || !itemDescription || !category || !type || !price) {
            errorToast("Please fill in all required fields.");
            return;
        }

        if (price < 0) {
            errorToast("Price Should not be negitive.");
            return;
        }

        try {
            const response = await apiClient.post('/items', newItemData);
            if (response.data.status === 404)
                errorToast(response.data.message);
            else if (response.data.success ) {
                successToast("Item added successfully!");
                fetchItems(page); // Refresh items after delete
            }

        } catch (error) {
            errorToast("Failed to add item");
        }
        setOpenAddDialog(false);
        setNewItemData({
            itemId: '',
            itemDescription: '',
            category: '',
            type: '',
            status: 'ACTIVE',
            price: '',
            pickupAllowed: true,
            shippingAllowed: true,
            deliveryAllowed: true
        });
    };

    return (
         <Dialog PaperProps={{className: 'dialog-custom',  }} open={openAddDialog} onClose={() => setOpenAddDialog(false)} maxWidth="md" fullWidth>
            <DialogTitle className="dialog-title-custom" >Add New Item...</DialogTitle>
            <DialogContent style={{ padding: '30px 50px 10px' }}>
                <TextField
                    autoFocus
                    margin="dense"
                    label="Item ID"
                    type="text"
                    fullWidth
                    
                    value={newItemData.itemId}
                    onChange={(e) => setNewItemData({ ...newItemData, itemId: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Description"
                    type="text"
                    fullWidth
                    
                    value={newItemData.itemDescription}
                    onChange={(e) => setNewItemData({ ...newItemData, itemDescription: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Category"
                    type="text"
                    fullWidth
                    
                    value={newItemData.category}
                    onChange={(e) => setNewItemData({ ...newItemData, category: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Type"
                    type="text"
                    fullWidth
                    
                    value={newItemData.type}
                    onChange={(e) => setNewItemData({ ...newItemData, type: e.target.value })}
                />
                <FormControl fullWidth margin="dense">
                    <InputLabel>Status</InputLabel>
                    <Select label="Status"
                        value={newItemData.status}
                        onChange={(e) => setNewItemData({ ...newItemData, status: e.target.value })}
                    >
                        {statusOptions.map((status) => (
                            <MenuItem key={status} value={status}>{status}</MenuItem>
                        ))}
                    </Select>
                </FormControl>
                <TextField
                    margin="dense"
                    label="Price"
                    type="number"
                    fullWidth
                    
                    value={newItemData.price}
                    onChange={(e) => setNewItemData({ ...newItemData, price: e.target.value })}
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newItemData.pickupAllowed}
                            onChange={(e) => setNewItemData({ ...newItemData, pickupAllowed: e.target.checked })}
                        />
                    }
                    label="Pickup Allowed"
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newItemData.shippingAllowed}
                            onChange={(e) => setNewItemData({ ...newItemData, shippingAllowed: e.target.checked })}
                        />
                    }
                    label="Shipping Allowed"
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newItemData.deliveryAllowed}
                            onChange={(e) => setNewItemData({ ...newItemData, deliveryAllowed: e.target.checked })}
                        />
                    }
                    label="Delivery Allowed"
                />
            </DialogContent>
            <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '26px' }}>
                <Button onClick={() => setOpenAddDialog(false)} style={{
                    backgroundColor: 'blue',
                    color: 'white',
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                    marginRight: '100px', // Space between buttons
                }}>Cancel</Button>
                <Button onClick={handleAddSave} style={{
                    backgroundColor: 'green',
                    color: 'white',
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                }}>Save</Button>
            </DialogActions>
        </Dialog>
    );
}