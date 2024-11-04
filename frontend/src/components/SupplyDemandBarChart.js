import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, CartesianGrid, Legend, ResponsiveContainer } from 'recharts';
import apiClient from './baseUrl';
import { TextField, FormControl } from '@mui/material';
import { errorToast } from './Toast';
import Autocomplete from '@mui/material/Autocomplete';

const SupplyDemandBarChart = () => {
    const [SupplyDemand, setSupplyDemand] = useState([]);
    const [locationOptions, setLocationOptions] = useState([]);
    const [locationName, setLocationName] = useState([]);
    const [newData, setNewData] = useState({
        locationId: '',
    });

    useEffect(() => {
        const fetchData = async () => {
            try {
                const response = await apiClient.get(`availability/getAvailabilityScatterData?locationId=${newData.locationId}`);
                if (response.data.status === 404) {
                    errorToast(response.data.message);
                } else if (response.data.success) {
                    const transformedData = response.data.payload.scatterDataDTO.map(item => ({
                        itemPrice: item.itemPrice,
                        Supply: item.supplyQuantity,
                        Demand: item.demandQuantity,
                        Name: item.itemName
                    }));
                    setLocationName(response.data.payload.locationName);
                    setSupplyDemand(transformedData);
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
            errorToast("Failed to fetch locations");
        }
    };

    const CustomTooltip = ({ active, payload }) => {
        if (active && payload && payload.length) {
            const { Name, Supply, Demand, itemPrice } = payload[0].payload;
            return (
                <div style={{
                    border: '1px solid #cccccc',
                    borderRadius: '4px',
                    padding: '10px',
                    boxShadow: '0 2px 6px rgba(0, 0, 0, 0.2)',
                    backgroundColor: Supply < Demand ? "red" : "#ffff"
                }}>
                    <b><p>{Name}</p>
                    <p>{`Price: ₹${itemPrice}`}</p></b>
                    <p>{`Supply: ${Supply} units`}</p>
                    <p>{`Demand: ${Demand} units`}</p>
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
            backgroundColor: '#f0f0f0',
            padding: '20px',
            borderRadius: '8px',
            boxShadow: '0 4px 8px rgba(0, 0, 0, 0.2)'
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

            <h2>Items Supply vs. Demand - {locationName}</h2>
            <ResponsiveContainer width={1000} height={500}>
                <BarChart data={SupplyDemand} margin={{ top: 10, right: 10, left: 0, bottom: 60 }}>
                    <CartesianGrid strokeDasharray="3 3" />
                    <XAxis dataKey="Name" 
                    angle={-45}  // Rotate the labels
                    textAnchor="end"
                    interval={0}  // Show all labels
                    tick={{ fontSize: 12, width: 100, overflow: 'hidden', textOverflow: 'ellipsis' }} />
                    <YAxis />
                    <Tooltip content={<CustomTooltip />} />
                    <Legend verticalAlign="top" height={40} />
                    <Bar dataKey="Supply" fill="#82ca9d" />
                    <Bar dataKey="Demand" fill="#fc7060" />
                </BarChart>
            </ResponsiveContainer>
        </div>
    );
};

export default SupplyDemandBarChart;
