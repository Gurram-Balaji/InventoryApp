import { useState } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Checkbox, TextField, FormControl, FormControlLabel, InputLabel, MenuItem, Select } from '@mui/material';
import { errorToast, successToast } from '../../components/Toast';
import apiClient from '../../components/baseUrl';

export default function NewItemForm({ locationTypeOptions, fetchRow, page, setOpenAddDialog, openAddDialog }) {

    const [newData, setNewData] = useState({
        "locationId": "",
        "locationDesc": "",
        "locationType": "WAREHOUSE",
        "pickupAllowed": true,
        "shippingAllowed": true,
        "deliveryAllowed": true,
        "addressLine1": "",
        "addressLine2": "",
        "addressLine3": "",
        "city": "",
        "state": "",
        "country": "",
        "pinCode": ""
    });


    const handleAddSave = async () => {
        // Validate fields
        const { locationId, locationDesc, locationType, addressLine1, city, state, country, pinCode } = newData;
        if (!locationId || !locationDesc || !locationType || !addressLine1 || !city || !state || !country || !pinCode) {
            errorToast("Please fill in all required fields.");
            return;
        }

        try {
            const response = await apiClient.post('/locations', newData);

            if (response.data.status === 404)
                errorToast(response.data.message);
            else if (response.data.success === true) {
                successToast("Location added successfully!");
               
                fetchRow(page);
            }

        } catch (error) {
            errorToast("Failed to add location");
        }
        setOpenAddDialog(false);
        setNewData({
            "locationId": "",
            "locationDesc": "",
            "locationType": "WAREHOUSE",
            "pickupAllowed": true,
            "shippingAllowed": true,
            "deliveryAllowed": true,
            "addressLine1": "",
            "addressLine2": "",
            "addressLine3": "",
            "city": "",
            "state": "",
            "country": "",
            "pinCode": ""
        });
    };

    return (
         <Dialog PaperProps={{className: 'dialog-custom',  }}open={openAddDialog} onClose={() => setOpenAddDialog(false)} maxWidth="md" fullWidth>
            <DialogTitle className="dialog-title-custom" >Add New Location...</DialogTitle>
            <DialogContent style={{ padding: '30px 50px 10px' }}>
                <TextField
                    autoFocus
                    margin="dense"
                    label="Location ID"
                    type="text"
                    fullWidth
                    
                    value={newData.locationId}
                    onChange={(e) => setNewData({ ...newData, locationId: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Description"
                    type="text"
                    fullWidth
                    
                    value={newData.locationDesc}
                    onChange={(e) => setNewData({ ...newData, locationDesc: e.target.value })}
                />

                {/* Location Type Select Box */}
                <FormControl fullWidth margin="dense" >
                    <InputLabel>Location Type</InputLabel>
                    <Select label="Location Type"
                        value={newData.locationType}
                        onChange={(e) => setNewData({ ...newData, locationType: e.target.value })}
                    >
                        {locationTypeOptions.map((type) => (
                            <MenuItem key={type} value={type}>{type.toString().replace(/_/g, ' ') }</MenuItem>
                        ))}
                    </Select>
                </FormControl>

                {/* Address Fields */}
                <TextField
                    margin="dense"
                    label="Address Line 1"
                    type="text"
                    fullWidth
                    
                    value={newData.addressLine1}
                    onChange={(e) => setNewData({ ...newData, addressLine1: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Address Line 2"
                    type="text"
                    fullWidth
                    
                    value={newData.addressLine2}
                    onChange={(e) => setNewData({ ...newData, addressLine2: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Address Line 3"
                    type="text"
                    fullWidth
                    
                    value={newData.addressLine3}
                    onChange={(e) => setNewData({ ...newData, addressLine3: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="City"
                    type="text"
                    fullWidth
                    
                    value={newData.city}
                    onChange={(e) => setNewData({ ...newData, city: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="State"
                    type="text"
                    fullWidth
                    
                    value={newData.state}
                    onChange={(e) => setNewData({ ...newData, state: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Country"
                    type="text"
                    fullWidth
                    
                    value={newData.country}
                    onChange={(e) => setNewData({ ...newData, country: e.target.value })}
                />
                <TextField
                    margin="dense"
                    label="Pin Code"
                    type="text"
                    fullWidth
                    
                    value={newData.pinCode}
                    onChange={(e) => setNewData({ ...newData, pinCode: e.target.value })}
                />

                {/* Fulfillment Options */}
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newData.pickupAllowed}
                            onChange={(e) => setNewData({ ...newData, pickupAllowed: e.target.checked })}
                        />
                    }
                    label="Pickup Allowed"
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newData.shippingAllowed}
                            onChange={(e) => setNewData({ ...newData, shippingAllowed: e.target.checked })}
                        />
                    }
                    label="Shipping Allowed"
                />
                <FormControlLabel
                    control={
                        <Checkbox
                            checked={newData.deliveryAllowed}
                            onChange={(e) => setNewData({ ...newData, deliveryAllowed: e.target.checked })}
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
                <Button onClick={() => handleAddSave(newData)} style={{
                    backgroundColor: 'green',
                    color: 'white',
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                }}>Save</Button>
            </DialogActions>
        </Dialog>
    );
}