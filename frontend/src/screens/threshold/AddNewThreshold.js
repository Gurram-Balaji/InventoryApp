import { useState, useEffect } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, FormControl, InputLabel, MenuItem, Select } from '@mui/material';
import { errorToast, successToast } from '../../components/Toast';
import apiClient from '../../components/baseUrl';

export default function NewDemandForm({ fetchRow, page, setOpenAddDialog, openAddDialog }) {

    const [locationOptions, setLocationOptions] = useState([]);
    const [itemOptions, setItemOptions] = useState([]);

    const [newData, setNewData] = useState({
        "itemId": '',
        "locationId": '',
        "minThreshold": '',
        "maxThreshold": ''
    });

    const handleAddSave = async () => {
        const { minThreshold, maxThreshold } = newData;
        if (!minThreshold || !maxThreshold) {
            errorToast("Please fill in all required fields.");
            return;
        }

        if (minThreshold<0 || maxThreshold<0) {
            errorToast("Quantity should not be negitive.");
            return;
        }

        try {
            const response = await apiClient.post('/atpThresholds', newData);
            if (response.data.status === 404)
                errorToast(response.data.message);
            else if (response.data.success === true) {
                successToast("Threshold added successfully!");
              
                fetchRow(page);
            }
        } catch (error) {
            errorToast("Failed to add threshold");
        }
        setOpenAddDialog(false);
        setNewData({
            "itemId": '',
            "locationId": '',
            "minThreshold": '',
            "maxThreshold": ''
        });
    };

    useEffect(() => {
        const fetchIds = async () => {
            try {
                // Fetch location IDs and descriptions
                const locationResponse = await apiClient.get('/locations/ids');
                const locationData = locationResponse.data.payload || [];
                const locations = locationData.map(item => {
                    try {
                        const parsedItem = JSON.parse(item);
                        return {
                            id: parsedItem.locationId || null,
                            name: parsedItem.locationDesc || ''  // Extract description
                        };
                    } catch (e) {
                        console.error("Error parsing location JSON:", e);
                        return null;
                    }
                }).filter(item => item !== null);  // Remove any null values

                setLocationOptions(locations);
                // Fetch item IDs and descriptions
                const itemResponse = await apiClient.get('/items/ids');
                const itemData = itemResponse.data.payload || [];
                const items = itemData.map(item => {
                    try {
                        const parsedItem = JSON.parse(item);
                        return {
                            id: parsedItem.itemId || null,
                            name: parsedItem.itemDescription || ''  // Extract description
                        };
                    } catch (e) {
                        console.error("Error parsing item JSON:", e);
                        return null;
                    }
                }).filter(item => item !== null);  // Remove any null values

                setItemOptions(items);

            } catch (error) {
                console.error("Error fetching IDs:", error);
                errorToast("Failed to fetch IDs");
            }
        };

        fetchIds();
    }, []);




    return (
        <Dialog open={openAddDialog} onClose={() => setOpenAddDialog(false)} maxWidth="md" fullWidth>
            <DialogTitle>Add New Demand</DialogTitle>
            <DialogContent style={{ padding: '30px 50px 10px' }}>
                {/* Location ID Select Box */}
                <FormControl fullWidth margin="dense" variant="standard">
                    <InputLabel>Location</InputLabel>
                    <Select
                        value={newData.locationId || ''}
                        onChange={(e) => setNewData({ ...newData, locationId: e.target.value })}
                    >
                        {Array.isArray(locationOptions) && locationOptions.map((location) => (
                            <MenuItem key={location.id} value={location.id}>{location.name}</MenuItem>
                        ))}
                    </Select>
                </FormControl>

                {/* Item ID Select Box */}
                <FormControl fullWidth margin="dense" variant="standard">
                    <InputLabel>Item</InputLabel>
                    <Select
                        value={newData.itemId || ''}
                        onChange={(e) => setNewData({ ...newData, itemId: e.target.value })}
                    >
                        {Array.isArray(itemOptions) && itemOptions.map((item) => (
                            <MenuItem key={item.id} value={item.id}>{item.name}</MenuItem>
                        ))}
                    </Select>
                </FormControl>

                <TextField
                    margin="dense"
                    label="Min Threshold"
                    type="number"
                    fullWidth
                    variant="standard"
                    value={newData.minThreshold || ''}
                    onChange={(e) => setNewData({ ...newData, minThreshold: e.target.value })}
                />

                <TextField
                    margin="dense"
                    label="Max Threshold"
                    type="number"
                    fullWidth
                    variant="standard"
                    value={newData.maxThreshold || ''}
                    onChange={(e) => setNewData({ ...newData, maxThreshold: e.target.value })}
                />

            </DialogContent>

            <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '26px' }}>
                <Button onClick={() => setOpenAddDialog(false)} style={{
                    backgroundColor: 'blue',
                    color: 'white',
                    fontWeight: 'bold',
                    textTransform: 'uppercase',
                    marginRight: '100px',
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
