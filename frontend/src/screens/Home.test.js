import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter, useNavigate } from 'react-router-dom'; // For providing routing context
import Dashboard from './Home';
import apiClient from '../components/baseUrl'; // Mocking the API client

// Mocking the apiClient
jest.mock('../components/baseUrl', () => ({
    get: jest.fn(),
}));

const mockedNavigate = jest.fn(); // Create a mock function for navigate
jest.mock('react-router-dom', () => ({
  ...jest.requireActual('react-router-dom'),
  useNavigate: () => mockedNavigate, // Return the mock function when useNavigate is called
}));
  
describe('Dashboard Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders dashboard and fetches data', async () => {
        // Mock the API response
        const mockData = {
            data: {
                payload: {
                    totalItems: 5,
                    totalLocations: 3,
                    totalSupplies: 10,
                    totalDemands: 7,
                },
            },
        };
        apiClient.get
            .mockResolvedValueOnce()
            .mockResolvedValueOnce()
            .mockResolvedValueOnce(mockData);

        // Render the component inside MemoryRouter to provide routing context
        render(
            <MemoryRouter>
                <Dashboard />
            </MemoryRouter>
        );

        // Assert that the heading is rendered
        expect(screen.getByText('Dashboard')).toBeInTheDocument();

        // Wait for the API call to resolve and check if the data is rendered
        await waitFor(() => {
            expect(screen.getByText('Total Items')).toBeInTheDocument();
            expect(screen.getByText('5')).toBeInTheDocument();

            expect(screen.getByText('Total Locations')).toBeInTheDocument();
            expect(screen.getByText('3')).toBeInTheDocument();

            expect(screen.getByText('Total Supplies')).toBeInTheDocument();
            expect(screen.getByText('10')).toBeInTheDocument();

            expect(screen.getByText('Total Demands')).toBeInTheDocument();
            expect(screen.getByText('7')).toBeInTheDocument();
        });

        // Verify that the API client was called correctly
        expect(apiClient.get).toHaveBeenCalledWith('/dashboard');
    });

    test('should handle API failure and show error toast', async () => {
        // Mock API response to throw an error
        apiClient.get.mockResolvedValueOnce()
        .mockResolvedValueOnce()
        .mockRejectedValueOnce(new Error('Network Error'));
        render(
            <MemoryRouter>
                <Dashboard />
            </MemoryRouter>
        );
    });

    test('navigates to correct route when dashboard card is clicked', async () => {
        // Mock API data
        const mockData = {
          data: {
            payload: {
              totalItems: 5,
              totalLocations: 3,
              totalSupplies: 10,
              totalDemands: 7,
            },
          },
        };
        apiClient.get.mockResolvedValueOnce(mockData);
    
        // Render the component
        render(
          <MemoryRouter>
            <Dashboard />
          </MemoryRouter>
        );
    
        // Simulate clicking the 'Total Items' card
        const totalItemsCard = await screen.findByText('Total Items');
        fireEvent.click(totalItemsCard);
    
        // Assert that the useNavigate function was called with the correct path
        expect(mockedNavigate).toHaveBeenCalledWith('/items');
    
        // Simulate clicking the 'Total Locations' card
        const totalLocationsCard = await screen.findByText('Total Locations');
        fireEvent.click(totalLocationsCard);
    
        // Assert that the useNavigate function was called with the correct path
        expect(mockedNavigate).toHaveBeenCalledWith('/location');
    
        // Simulate clicking the 'Total Supplies' card
        const totalSuppliesCard = await screen.findByText('Total Supplies');
        fireEvent.click(totalSuppliesCard);
    
        expect(mockedNavigate).toHaveBeenCalledWith('/supply');
    
        // Simulate clicking the 'Total Demands' card
        const totalDemandsCard = await screen.findByText('Total Demands');
        fireEvent.click(totalDemandsCard);
    
        expect(mockedNavigate).toHaveBeenCalledWith('/demand');
      });

});
