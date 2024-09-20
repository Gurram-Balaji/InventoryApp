import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import apiClient from '../../components/baseUrl';
import { errorToast, successToast } from '../../components/Toast';
import './VerifyEmail.css'; // Import the CSS file

const VerifyEmail = () => {
  const [message, setMessage] = useState('');
  const location = useLocation();
  const navigate = useNavigate();

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
        }
      } catch (error) {
        setMessage('Email verification failed. Please try again.');
        errorToast('Email verification failed. Please try again.');
      }
    };

    verifyEmail();
  }, [location.search]);

  const handleLoginRedirect = () => {
    navigate('/login');
  };

  return (
    <div className="verify-email-container">
      <div className="verify-email-box">
        <h1 className="verify-email-heading">Email Verification</h1>
        <p className={`verify-email-message ${message.includes('failed') ? 'error' : 'success'}`}>
          {message}
        </p>
          <button className="button button--primary" onClick={handleLoginRedirect}>
            Go to Login
          </button>
      </div>
    </div>
  );
};

export default VerifyEmail;
