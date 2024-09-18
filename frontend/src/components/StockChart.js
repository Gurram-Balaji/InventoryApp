import React from 'react';
import { LineChart, Line, XAxis, YAxis, Tooltip, Legend, CartesianGrid } from 'recharts';
import { Box } from '@mui/material';

// Convert data to a format where each product has its own data key in the data objects
const transformData = (data) => {
  const dates = [...new Set(data.map(d => d.date))];
  const products = [...new Set(data.map(d => d.product))];

  // Initialize an array of objects with date keys
  const transformedData = dates.map(date => {
    const item = { date };
    products.forEach(product => {
      const productData = data.find(d => d.date === date && d.product === product);
      item[product] = productData ? productData.items : 0; // Use 0 if no data for this date
    });
    return item;
  });

  return transformedData;
};

const StockChart = ({ data }) => {
  const products = [...new Set(data.map(d => d.product))];
  const transformedData = transformData(data);

  return (
    <Box sx={{ p: 2 }}>
      <LineChart width={1400} height={700} data={transformedData}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="date" />
        <YAxis />
        <Tooltip />
        <Legend />
        {products.map(product => (
          <Line
            key={product}
            type="monotone"
            dataKey={product}
            stroke={getRandomColor()} 
            strokeWidth={3} 
            activeDot={{ r: 3 }}
          />
        ))}
      </LineChart>
    </Box>
  );
};

// Function to generate random colors
const getRandomColor = () => {
  const letters = '0123456789ABCDEF';
  let color = '#';
  for (let i = 0; i < 6; i++) {
    color += letters[Math.floor(Math.random() * 16)];
  }
  return color;
};

export default StockChart;
