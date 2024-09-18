import React, { useEffect, useState } from 'react';
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, CartesianGrid, Legend } from 'recharts';
import apiClient from './baseUrl';
import { TextField, FormControl } from '@mui/material';
import { errorToast } from './Toast';
import Autocomplete from '@mui/material/Autocomplete';


const ScatterPlot = () => {
    const [data, setData] = useState([]);
    const [locationOptions, setLocationOptions] = useState([]);
    const [locationName, setLocationName] = useState([]);
    const [newData, setNewData] = useState({
        "locationId": '',
    });
    useEffect(() => {
        // Fetch data from your API or source
        const fetchData = async () => {
            try {
                const response = await apiClient.get(`availability/getAvailabilityScatterData?locationId=${newData.locationId}`);
                if (response.data.status === 404) {
                    errorToast(response.data.message);
                } else if (response.data.success) {


                    const transformedData = response.data.payload.scatterDataDTO.map(item => ({
                        itemPrice: item.itemPrice,
                        availableQuantity: item.availableQuantity,
                        Name: item.itemName
                    }));

                    setLocationName(response.data.payload.locationName);
                    setData(transformedData);
                }
            } catch (error) {
                console.error('Error fetching data:', error);
            }
        };

        fetchData();
    }, [newData]);

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


    const formatXAxis = (value) => {
        return `₹${value}`; // Unit symbol ₹ to the left of the value
    };

    const CustomTooltip = ({ active, payload }) => {
        if (active && payload && payload.length) {
            const { Name, itemPrice, availableQuantity } = payload[0].payload;
            return (
                <div style={{
                    backgroundColor: '#ffffff',
                    border: '1px solid #cccccc',
                    borderRadius: '4px',
                    padding: '10px',
                    boxShadow: '0 2px 6px rgba(0, 0, 0, 0.2)'
                }}>
                    <p>{<b>{Name}</b>}</p>
                    <p>{`Price: ₹${itemPrice}`}</p>
                    <p>{`Available Quantity: ${availableQuantity} units`}</p>
                </div>
            );
        }
        return null;
    };

    useEffect(() => {
        fetchLocations();
    }, [newData]);


    return (
        <div style={{
            backgroundColor: '#f0f0f0',  // Background color for the chart area
            padding: '20px',  // Padding around the chart
            borderRadius: '8px',  // Rounded corners
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)'  // Shadow effect
        }}>
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

            <h2>Item Price vs. Availability - {locationName}</h2>
            <ScatterChart width={1000} height={400}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis
                    type="number"
                    dataKey="itemPrice"
                    name="Item Price"
                    tickFormatter={formatXAxis}
                />
                <YAxis
                    type="number"
                    dataKey="availableQuantity"
                    name="Available Quantity"
                />
                <Tooltip content={<CustomTooltip />} cursor={{ strokeDasharray: '3 3' }} />
                <Legend />
                <Scatter
                    name="Items"
                    data={data}
                    fill="#8884d8"
                    animationBegin={0}
                    animationDuration={3000}
                    animationEasing="cubic-bezier(0.42, 0, 0.58, 1)"
                />
            </ScatterChart>
        </div>
    );
};

export default ScatterPlot;
