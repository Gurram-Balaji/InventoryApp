import { useState, useEffect } from 'react';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, FormControl } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';
import { errorToast, successToast } from '../../components/Toast';
import apiClient from '../../components/baseUrl';

export default function NewThresholdForm({ fetchRow, page, setOpenAddDialog, openAddDialog }) {

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

        try {
            const response = await apiClient.post('/atpThresholds', newData);
            if (response.data.status === 404)
                errorToast(response.data.message);
            else if (response.data.success ) {
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

    // Fetch locations
    const fetchLocations = async (searchTerm = '') => {
        try {
            const locationResponse = await apiClient.get(`/locations/ids?search=${searchTerm}`);
            const locationData = locationResponse.data.payload.content || [];
            const locations = locationData.map(item => {
                const parsedItem = JSON.parse(item);
                return {
                    id: parsedItem.locationId || null,
                    name: parsedItem.locationDesc || ''
                };
            });
            setLocationOptions(locations);
        } catch (error) {
             console.error("Error fetching locations:", error);
            errorToast("Failed to fetch locations");
        }
    };

    // Fetch items
    const fetchItems = async (searchTerm = '') => {
        try {
            const itemResponse = await apiClient.get(`/items/ids?search=${searchTerm}`);
            const itemData = itemResponse.data.payload.content || [];
            const items = itemData.map(item => {
                const parsedItem = JSON.parse(item);
                return {
                    id: parsedItem.itemId || null,
                    name: parsedItem.itemDescription || ''
                };
            });
            setItemOptions(items);
        } catch (error) {
             console.error("Error fetching items:", error);
            errorToast("Failed to fetch items");
        }
    };

    useEffect(() => {
        fetchLocations(); // Fetch initial locations
        fetchItems(); // Fetch initial items
    }, []);

    return (
         <Dialog PaperProps={{ className: 'dialog-custom' }} open={openAddDialog} maxWidth="md" fullWidth>
            <DialogTitle className="dialog-title-custom">Add New Threshold</DialogTitle>
            <DialogContent style={{ padding: '30px 50px 10px' }}>
                
                {/* Location Autocomplete */}
                <FormControl fullWidth margin="dense">
                    <Autocomplete
                        options={locationOptions}
                        getOptionLabel={(option) => option.name || ''}
                        onInputChange={(event, newInputValue) => fetchLocations(newInputValue)} // Search locations as user types
                        onChange={(event, newValue) => setNewData({ ...newData, locationId: newValue ? newValue.id : '' })} // Set selected locationId
                        renderInput={(params) => (
                            <TextField {...params} label="Search Location" variant="outlined" fullWidth margin="dense" />
                        )}
                    />
                </FormControl>

                {/* Item Autocomplete */}
                <FormControl fullWidth margin="dense">
                    <Autocomplete
                        options={itemOptions}
                        getOptionLabel={(option) => option.name || ''}
                        onInputChange={(event, newInputValue) => fetchItems(newInputValue)} // Search items as user types
                        onChange={(event, newValue) => setNewData({ ...newData, itemId: newValue ? newValue.id : '' })} // Set selected itemId
                        renderInput={(params) => (
                            <TextField {...params} label="Search Item" variant="outlined" fullWidth margin="dense" />
                        )}
                    />
                </FormControl>

                <TextField
                    margin="dense"
                    label="Min Threshold"
                    type="number"
                    fullWidth
                    value={newData.minThreshold || ''}
                    onChange={(e) => setNewData({ ...newData, minThreshold: e.target.value })}
                />

                <TextField
                    margin="dense"
                    label="Max Threshold"
                    type="number"
                    fullWidth
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
