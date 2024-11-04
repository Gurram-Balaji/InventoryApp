import React, { useEffect, useState } from "react";
import apiClient from '../../components/baseUrl';

import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  CartesianGrid
} from "recharts";

const StackedBarChart = () => {
  const [data, setData] = useState([]);

  useEffect(() => {
    // Fetch supply and demand data from the API
    const fetchData = async () => {
      try {
        const response = await apiClient.get("/locations/stackedBarData"); // Replace with your API
        setData(response.data.payload);

      } catch (error) {
        console.error("Error fetching data:", error);
      }
    };

    fetchData();
  }, []);

  // Transform data to fit recharts' format for stacked bar charts
  const formattedData = data.map((location) => ({
    location: location.locationDesc,
    ONHAND: location.supplyDetails.ONHAND,
    INTRANSIT: location.supplyDetails.INTRANSIT,
    HARD_PROMISED: location.demandDetails.HARD_PROMISED,
    PLANNED: location.demandDetails.PLANNED,
  }));

  return (
    <ResponsiveContainer width="90%" height={800}>
      <div style={{ display: 'flex', justifyContent: 'center' }}>
        <h1 style={{ fontSize: '50px' }}>Inventory Summary at Locations</h1>
      </div>
      <BarChart
        data={formattedData}
        margin={{ top: 20, right: 30, left: 20, bottom: 120 }} // Increased bottom margin for X-axis labels
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis
          dataKey="location"
          angle={-45}  // Rotate the labels
          textAnchor="end"
          interval={0}  // Show all labels
          tick={{ fontSize: 12, width: 100, overflow: 'hidden', textOverflow: 'ellipsis' }} // Handle long text with ellipsis
        />
        <YAxis />
        <Tooltip />
        <Legend verticalAlign="top" height={40} /> {/* Move legend to the top */}
        <Bar dataKey="ONHAND" name="ON HAND" stackId="supply" fill="#82ca9d" />
        <Bar dataKey="INTRANSIT" stackId="supply" fill="#8884d8" />
        <Bar dataKey="HARD_PROMISED" name="HARD PROMISED" stackId="demand" fill="#ff8042" />
        <Bar dataKey="PLANNED" stackId="demand" fill="#ffc658" />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default StackedBarChart;
