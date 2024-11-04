import React, { useState, useEffect } from 'react';
import '../../form.css';
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import MotionHoc from "../MotionHoc";
import { TextField, FormControl } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';


const Available = () => {
    const [itemOptions, setItemOptions] = useState([]);
    const [locationOptions, setLocationOptions] = useState([]);
    const [selectedItem, setSelectedItem] = useState('');
    const [selectedLocation, setSelectedLocation] = useState('');
    const [version, setVersion] = useState('v1');  // Default to 'v1'
    const [responseData, setResponseData] = useState(null);

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
            errorToast("Failed to fetch items");
        }
    };

    useEffect(() => {
        fetchLocations();
        fetchItems();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        let url = '';
        if (!selectedItem) {
            errorToast("Select the item.");
            return;
        }
        if (version === 'v1') {
            url = selectedLocation ? `/availability/v1/${selectedItem.id}/${selectedLocation.id}` : `/availability/v1/${selectedItem.id}`;
        } else if (version === 'v2') {
            url = selectedLocation ? `/availability/v2/${selectedItem.id}/${selectedLocation.id}` : `/availability/v2/${selectedItem.id}`;
        }

        try {
            const response = await apiClient.get(url);
            if (response.data.status === 404)
                errorToast(response.data.message);
            else                 
                setResponseData(response.data);
        } catch (error) {
             console.error("Error fetching availability data:", error);
        }
    };

    const getColorIndicator = (stockLevel) => {
        switch (stockLevel) {
            case 'Yellow': return 'gold';
            case 'Red': return 'orangered';
            case 'Green': return 'lime';
            default: return 'white';
        }
    };

    return (
        <>
            <h1 style={{ margin: '0 32%' }}>Availability</h1>
            <div className="container">
                {/* Flexbox container for form */}
                <form onSubmit={handleSubmit} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '10px' }}>
                    {/* Location Autocomplete */}
                    <FormControl style={{ flex: '2' }}>
                        <Autocomplete
                            options={locationOptions}
                            getOptionLabel={(option) => option.name || ''}
                            onInputChange={(event, newInputValue) => fetchLocations(newInputValue)}
                            onChange={(event, newValue) => setSelectedLocation(newValue)}
                            renderInput={(params) => (
                                <TextField {...params} label="Search Location" variant="outlined" fullWidth />
                            )}
                        />
                    </FormControl>

                    {/* Item Autocomplete */}
                    <FormControl style={{ flex: '2' }}>
                        <Autocomplete
                            options={itemOptions}
                            getOptionLabel={(option) => option.name || ''}
                            onInputChange={(event, newInputValue) => fetchItems(newInputValue)}
                            onChange={(event, newValue) => setSelectedItem(newValue)}
                            renderInput={(params) => (
                                <TextField {...params} label="Search Item" variant="outlined" fullWidth />
                            )}
                        />
                    </FormControl>

                    {/* Threshold Checkbox */}
                    <div className="form-group" style={{ flex: '1', padding: '10px' }}>
                        <label>
                            <input style={{ display: 'inline-block' }}
                                type="checkbox"
                                checked={version === 'v2'}
                                onChange={(e) => setVersion(e.target.checked ? 'v2' : 'v1')}
                            /> Threshold Level
                        </label>
                    </div>

                    {/* Submit Button */}
                    <button type="submit" style={{ flex: '1', padding: '10px' }}>Submit</button>
                </form>
            </div>

            {/* Response Display */}
            <div className="response" style={{ backgroundColor: getColorIndicator(responseData?.payload.stockLevel || '') }}>
                <h3>{responseData ? 'Available Quantity' : 'Select location and item.'}</h3>
                <h1>{responseData ? responseData.payload.availableQty : '?'}</h1>
            </div>

            {/* Stock Levels Explanation */}
            <div style={{ color: '#757578', fontWeight: "bold", display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
                <span style={{ marginRight: '20px' }}>Red - Lower than the threshold level.</span>
                <span style={{ marginRight: '20px' }}>Yellow - Within the threshold level.</span>
                <span>Green - Above the max threshold level.</span>
            </div>
        </>
    );
};

export default MotionHoc(Available);
