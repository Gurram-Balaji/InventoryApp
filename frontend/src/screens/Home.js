import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate from react-router-dom
import '../form.css'; // Adjust based on your styles
import apiClient from '../components/baseUrl'; // Adjust based on your actual base URL setup
import MotionHoc from "./MotionHoc";
import ScatterPlot from '../components/ScatterPlot';


const Dashboard = () => {
    const [dashboardData, setDashboardData] = useState({
        totalItems: 0,
        totalLocations: 0,
        totalSupplies: 0,
        totalDemands: 0,
    });

    const navigate = useNavigate(); // Initialize navigate function for redirection

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                const response = await apiClient.get('/dashboard');
                const data = response.data.payload;
                setDashboardData(data);
            } catch (error) {
                console.error("Error fetching dashboard data:", error);
            }
        };

        fetchDashboardData();
    }, []);

    // Function to handle navigation based on card clicked
    const handleNavigation = (path) => {
        navigate(path); // Use navigate to go to the specified route
    };

    return (
        <div className="dashboard-container">
            <h1 style={{marginBottom:'20px'}}>Dashboard</h1>

            <ScatterPlot />
           
            <div className="dashboard-grid">
                <div className="dashboard-card" onClick={() => handleNavigation('/items')}>
                    <h3>Total Items</h3>
                    <p>{dashboardData.totalItems}</p>
                </div>
                <div className="dashboard-card" onClick={() => handleNavigation('/location')}>
                    <h3>Total Locations</h3>
                    <p>{dashboardData.totalLocations}</p>
                </div>
                <div className="dashboard-card" onClick={() => handleNavigation('/supply')}>
                    <h3>Total Supplies</h3>
                    <p>{dashboardData.totalSupplies}</p>
                </div>
                <div className="dashboard-card" onClick={() => handleNavigation('/demand')}>
                    <h3>Total Demands</h3>
                    <p>{dashboardData.totalDemands}</p>
                </div>
            </div>
        </div>
    );
};

export default MotionHoc(Dashboard);
