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
    <ResponsiveContainer width="90%" height={700} >
      <h1 style={{display:'flex',fontSize:'50px',justifyContent:'center'}}>Items Summary at Locations</h1>
      <BarChart data={formattedData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="location" />
        <YAxis />
        <Tooltip />
        <Legend />
        <Bar dataKey="ONHAND" stackId="supply" fill="#82ca9d" />
        <Bar dataKey="INTRANSIT" stackId="supply" fill="#8884d8" />
        <Bar dataKey="HARD_PROMISED" stackId="demand" fill="#ff8042" />
        <Bar dataKey="PLANNED" stackId="demand" fill="#ffc658" />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default StackedBarChart;
