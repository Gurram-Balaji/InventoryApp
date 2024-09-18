import React from 'react';
import StockChart from '../../components/StockChart';
const generateData = (startDate, numDays, numProducts) => {
  const data = [];
  const products = Array.from({ length: numProducts }, (_, i) => `Item ${String.fromCharCode(65 + i)}`);
  const start = new Date(startDate);

  for (let day = 0; day < numDays; day++) {
    const date = new Date(start);
    date.setDate(start.getDate() + day);
    const dateString = date.toISOString().split('T')[0];

    products.forEach(product => {
      const items = Math.floor(Math.random() * 500) + 10; // Random items between 100 and 199
      data.push({ date: dateString, product, items });
    });
  }

  return data;
};

const data = generateData('2024-11-01', 10, 15); // 10 days, 5 products



const StackedBarChart = () => {
  return (
    <div>
      <h1>Stock Inventory Chart</h1>
      <StockChart data={data} />
    </div>
  );
};


export default StackedBarChart;
