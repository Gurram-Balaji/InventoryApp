import React, { useState, useEffect } from 'react';
import '../../form.css';
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import MotionHoc from "../MotionHoc";


const Available = () => {
    const [itemOptions, setItemOptions] = useState([]);
    const [locationOptions, setLocationOptions] = useState([]);
    const [selectedItem, setSelectedItem] = useState('');
    const [selectedLocation, setSelectedLocation] = useState('');
    const [version, setVersion] = useState('v1');
    const [responseData, setResponseData] = useState(null);

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
            }
        };

        fetchIds();
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        let url = '';
        if (selectedItem === '') {
            errorToast("Select the item.");
            return;
        }
        if (version === 'v1') {
            if (selectedLocation) {
                url = `/availability/v1/${selectedItem}/${selectedLocation}`;
            } else {
                url = `/availability/v1/${selectedItem}`;
            }
        } else if (version === 'v2') {
            if (selectedLocation) {
                url = `/availability/v2/${selectedItem}/${selectedLocation}`;
            } else {
                url = `/availability/v2/${selectedItem}`;
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
        <div className="container">
            <h1>Availability</h1>
            <form onSubmit={handleSubmit} className="form">
                <div className="form-group">
                    <label htmlFor="item">Item</label>
                    <select
                        id="item"
                        value={selectedItem}
                        onChange={(e) => setSelectedItem(e.target.value)}
                    >
                        <option value="">Select an item</option>
                        {itemOptions.map(item => (
                            <option key={item.id} value={item.id}>{item.name}</option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <label htmlFor="location">Location</label>
                    <select
                        id="location"
                        value={selectedLocation}
                        onChange={(e) => setSelectedLocation(e.target.value)}
                    >
                        <option value="">All Location</option>
                        {locationOptions.map(location => (
                            <option key={location.id} value={location.id}>{location.name}</option>
                        ))}
                    </select>
                </div>

                <div className="form-group">
                    <div>
                        <label>
                            <input
                                type="radio"
                                value="v1"
                                checked={version === 'v1'}
                                onChange={() => setVersion('v1')}
                            />
                            Without Stock Level
                        </label>
                        <label>
                            <input
                                type="radio"
                                value="v2"
                                checked={version === 'v2'}
                                onChange={() => setVersion('v2')}
                            />
                            With Stock Level
                        </label>
                    </div>
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
    );
};

export default MotionHoc(Available);
