import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import './VerifyEmail.css';  // Import your custom styles
import apiClient from '../../components/baseUrl';
import { errorToast, successToast } from '../../components/Toast';

const VerifyEmail = () => {
  const [message, setMessage] = useState('');
  const [isVerified, setIsVerified] = useState(false); // New state to track verification status
  const location = useLocation();

  useEffect(() => {
    const verifyEmail = async () => {
      const query = new URLSearchParams(location.search);
      const token = query.get('token');

      try {
        const response = await apiClient.get(`/auth/verify-email?token=${token}`);
        if (response.data.status === 404) {
          errorToast(response.data.message);
          setMessage('Verification failed. Please check the link and try again.');
        } else {
          successToast('Email successfully verified!');
          setMessage('Your email has been successfully verified.');
          setIsVerified(true); // Set verification status
        }
      } catch (error) {
        setMessage('Email verification failed. Please try again.');
        errorToast('Email verification failed. Please try again.');
      }
    };

    verifyEmail();
  }, [location.search]); // Dependency array

  return (
    <div className="verify-email-container">
      <div className="verify-email-box">
        <h2 className="verify-email-heading">Email Verification</h2>
        <p className={`verify-email-message ${message.includes('failed') ? 'error' : 'success'}`}>
          {message}
        </p>
        {isVerified && (
          <p className="verification-success-message">
            Your email has been successfully verified. You can now <a href="/login">log in</a>.
          </p>
        )}
      </div>
    </div>
  );
};

export default VerifyEmail;
