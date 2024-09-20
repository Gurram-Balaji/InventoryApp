import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { useSelector } from 'react-redux';
import { selectToken } from '../store/authSlice';

const PrivateRoute = ({ children }) => {
  const token = useSelector(selectToken);
  const location = useLocation(); // Get the current path

  // Check if the path is '/verify-email'
  const isVerifyEmailRoute = location.pathname === '/verify-email';

  // Allow access to '/verify-email' even if the user is not authenticated
  if (!token && !isVerifyEmailRoute) {
    // If the token is not available and it's not '/verify-email', redirect to login
    return <Navigate to="/" />;
  }
console.log("hi");
  // If the route is '/verify-email', allow access without redirect
  return children;
};

export default PrivateRoute;
