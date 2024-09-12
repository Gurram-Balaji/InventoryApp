// PrivateRoute.js
import React from 'react';
import { Navigate } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectToken } from '../store/authSlice';
const PrivateRoute = ({ children }) => {
  const token =useSelector(selectToken);

  return token ? children : <Navigate to="/" />;
};

export default PrivateRoute ;
