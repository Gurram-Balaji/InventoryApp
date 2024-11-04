import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import VerifyEmail from './VerifyEmail';
import apiClient from '../../components/baseUrl'; // Adjust path accordingly
import { errorToast, successToast } from '../../components/Toast';

jest.mock('../../components/baseUrl'); // Mock API client
jest.mock('../../components/Toast'); // Mock Toast notifications

describe('VerifyEmail Component', () => {
  const mockNavigate = jest.fn();

  beforeEach(() => {
    jest.resetAllMocks();
  });

  it('should display a success message when email is verified', async () => {
    // Mock successful verification response
    apiClient.get.mockResolvedValue({
      data: { status: 200, message: 'Email successfully verified!' },
    });

    render(
      <MemoryRouter initialEntries={['/verify-email?token=testToken']}>
        <Routes>
          <Route path="/verify-email" element={<VerifyEmail />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(apiClient.get).toHaveBeenCalledWith('/auth/verify-email?token=testToken');
    });

    expect(successToast).toHaveBeenCalledWith('Email successfully verified!');
  });

  it('should display an error message when email verification fails (404)', async () => {
    // Mock failed verification response
    apiClient.get.mockResolvedValue({
      data: { status: 404, message: 'Verification failed' },
    });

    render(
      <MemoryRouter initialEntries={['/verify-email?token=invalidToken']}>
        <Routes>
          <Route path="/verify-email" element={<VerifyEmail />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(apiClient.get).toHaveBeenCalledWith('/auth/verify-email?token=invalidToken');
    });

    expect(errorToast).toHaveBeenCalledWith('Verification failed');
    expect(screen.getByText('Verification failed. Please check the link and try again.')).toBeInTheDocument();
  });

  it('should display a generic error message when there is an API error', async () => {
    // Mock API error
    apiClient.get.mockRejectedValue(new Error('Network error'));

    render(
      <MemoryRouter initialEntries={['/verify-email?token=errorToken']}>
        <Routes>
          <Route path="/verify-email" element={<VerifyEmail />} />
        </Routes>
      </MemoryRouter>
    );

    await waitFor(() => {
      expect(apiClient.get).toHaveBeenCalledWith('/auth/verify-email?token=errorToken');
    });
    expect(errorToast).toHaveBeenCalledWith('Email verification failed. Please try again.');
  });

  it('should navigate to the login page when "Go to Login" button is clicked', async () => {
    // Mock successful verification
    apiClient.get.mockResolvedValue({
      data: { status: 200, message: 'Email successfully verified!' },
    });

    render(
      <MemoryRouter initialEntries={['/verify-email?token=testToken']}>
        <Routes>
          <Route path="/verify-email" element={<VerifyEmail />} />
          <Route path="/login" element={<div>Login Page</div>} />
        </Routes>
      </MemoryRouter>
    );

    // Wait for successful verification message
    await waitFor(() => {
      expect(screen.getByText('Your email has been successfully verified.')).toBeInTheDocument();
    });

    // Click the "Go to Login" button
    fireEvent.click(screen.getByText('Go to Login'));

    // Check if the login page is displayed
    await waitFor(() => {
      expect(screen.getByText('Login Page')).toBeInTheDocument();
    });
  });
});
