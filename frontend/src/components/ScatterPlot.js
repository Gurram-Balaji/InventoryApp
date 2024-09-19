import React, { useEffect, useState } from 'react';
import { ScatterChart, Scatter, XAxis, YAxis, Tooltip, CartesianGrid, Legend } from 'recharts';
import apiClient from './baseUrl';
import { TextField, FormControl } from '@mui/material';
import { errorToast } from './Toast';
import Autocomplete from '@mui/material/Autocomplete';

const ScatterPlot = () => {
    const [supply, setSupply] = useState([]);
    const [demand, setDemand] = useState([]);
    const [highlightedItem, setHighlightedItem] = useState(null); // State to track hovered item

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
                    const transformedSupplyData = response.data.payload.scatterDataDTO.map(item => ({
                        itemPrice: item.itemPrice,
                        Quantity: item.supplyQuantity,
                        Name: item.itemName
                    }));

                    const transformedDemandData = response.data.payload.scatterDataDTO.map(item => ({
                        itemPrice: item.itemPrice,
                        Quantity: item.demandQuantity,
                        Name: item.itemName
                    }));

                    setLocationName(response.data.payload.locationName);
                    setSupply(transformedSupplyData);
                    setDemand(transformedDemandData);
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
            const { Name: hoveredName } = payload[0].payload;
    
            // Find the matching supply and demand data by name
            const supplyData = supply.find(s => s.Name === hoveredName);
            const demandData = demand.find(d => d.Name === hoveredName);
    
            return (
                <div style={{
                    border: '1px solid #cccccc',
                    borderRadius: '4px',
                    padding: '10px',
                    boxShadow: '0 2px 6px rgba(0, 0, 0, 0.2)',
                    backgroundColor: supplyData.Quantity<demandData.Quantity ? "red" : "#ffff"
                }}>
                    <b><p>{hoveredName}</p>
                    <p>{`Price: ₹${supplyData.itemPrice}`}</p></b>
                    {supplyData && (
                        <div>
                            <p>{`Supply: ${supplyData.Quantity} units`}</p>
                        </div>
                    )}
                    {demandData && (
                        <div>
                            <p>{`Demand: ${demandData.Quantity} units`}</p>
                        </div>
                    )}
                </div>
            );
        }
        return null;
    };
    
    useEffect(() => {
        fetchLocations();
    }, [newData]);

    // Handle hover and click events to highlight related dots
    const handleMouseEnter = (data) => {
        setHighlightedItem(data.Name); // Track the hovered item by its name
    };

    const handleMouseLeave = () => {
        setHighlightedItem(null); // Reset highlighted item when mouse leaves
    };

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
            <ScatterChart width={1000} height={400}>
                <CartesianGrid strokeDasharray="2 2" />
                <XAxis
                    type="number"
                    dataKey="itemPrice"
                    name="Item Price"
                    tickFormatter={formatXAxis}
                />
                <YAxis
                    type="number"
                    dataKey="Quantity"
                    name="Quantity"
                />
                <Tooltip content={<CustomTooltip />} cursor={{ strokeDasharray: 'none' }} />
                <Legend />
                <Scatter
                    name="Supply"
                    data={supply}
                    fill="#8884d8"
                    onMouseEnter={handleMouseEnter}
                    onMouseLeave={handleMouseLeave}
                    shape={(props) => (
                        <circle
                            {...props}
                            r={highlightedItem === props.payload.Name ? 8 : 4} 
                            fill={"#8884d8"} 
                        />
                    )}
                />
                <Scatter
                    name="Demand"
                    data={demand}
                    fill="#0c00fa"
                    onMouseEnter={handleMouseEnter}
                    onMouseLeave={handleMouseLeave}
                    shape={(props) => (
                        <circle
                            {...props}
                            r={highlightedItem === props.payload.Name ? 8 : 4} 
                            fill={"#0c00fa"} 
                        />
                    )}
                />
            </ScatterChart>
        </div>
    );
};

export default ScatterPlot;
