import React, { useState, useEffect } from 'react';
import '../../form.css';
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import MotionHoc from "../MotionHoc";
import {  TextField, FormControl } from '@mui/material';
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
    fetchLocations(); 
    fetchItems(); 
}, []);




    const handleSubmit = async (e) => {
        e.preventDefault();
        let url = '';
        if (selectedItem === undefined) {
            errorToast("Select the item.");
            return;
        }
        if (version === 'v1') {
            if (selectedLocation) {
                url = `/availability/v1/${selectedItem.id}/${selectedLocation.id}`;
            } else {
                url = `/availability/v1/${selectedItem.id}`;
            }
        } else if (version === 'v2') {
            if (selectedLocation) {
                url = `/availability/v2/${selectedItem.id}/${selectedLocation.id}`;
            } else {
                url = `/availability/v2/${selectedItem.id}`;
            }
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
            case 'Yellow':
                return 'yellow';
            case 'Red':
                return 'red';
            case 'Green':
                return 'green';
            default:
                return 'white';
        }
    };

    return (
        <>
       
            <div className="container">
                <h1>Availability</h1>
                <form onSubmit={handleSubmit} className="form">
                   

         {/* Location Autocomplete */}
         <FormControl fullWidth margin="dense">
                    <Autocomplete
                        options={locationOptions}
                        getOptionLabel={(option) => option.name || ''}
                        onInputChange={(event, newInputValue) => fetchLocations(newInputValue)} // Search locations as user types
                        onChange={(event, newValue) =>{setSelectedLocation(newValue); console.log(newValue);}}
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
                        onChange={(event, newValue) => setSelectedItem(newValue)}
                        renderInput={(params) => (
                            <TextField {...params} label="Search Item" variant="outlined" fullWidth margin="dense" />
                        )}
                    />
                </FormControl>

                    <div className="form-group">
                        <label>
                            <input style={{ display: 'inline-block' }}
                                type="checkbox"
                                checked={version === 'v2'}
                                onChange={(e) => setVersion(e.target.checked ? 'v2' : 'v1')}
                            /> Threshold Level
                        </label>
                    </div>

                    <button type="submit">Submit</button>
                </form>

                {responseData && (
                    <div className="response" style={{ backgroundColor: getColorIndicator(responseData.payload.stockLevel) }} >
                        <h3>Available Quantity</h3>
                        <h1>{responseData.payload.availableQty}</h1>
                    </div>
                )}


            </div>
            <div style={{ color: '#757578', fontWeight: "bold", marginLeft: "100px" }}>
                <h4 style={{ color: '#535363' }}>Threshold Levels : </h4>
                <p>
                    <span className="dot red"></span>Lower than the threshold level.
                </p>
                <p>
                    <span className="dot yellow"></span>Within the threshold level.
                </p>
                <p>
                    <span className="dot green"></span>Above the max threshold level.
                </p>
            </div>
        </>
    );
};

export default MotionHoc(Available);
