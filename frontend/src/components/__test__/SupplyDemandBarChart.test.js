import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import SupplyDemandBarChart from '../SupplyDemandBarChart'; // Adjust the import path as needed
import apiClient from '../baseUrl'; // Adjust the import path as needed
import { errorToast } from '../Toast'; // Adjust the import path as needed
import user from '@testing-library/user-event';

const mockStore = configureStore([]);
const store = mockStore({});
jest.mock('../Toast');
jest.mock('../baseUrl', () => ({
    get: jest.fn(),
}));

afterEach(() => {
    jest.clearAllMocks();
});


describe('SupplyDemandBarChart', () => {

    test('should render the component', () => {
        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        expect(screen.getByText(/items supply vs. demand/i)).toBeInTheDocument();
    });

    test('should fetch and display supply and demand data', async () => {
        const mockData = {
            data: {
                status: 200,
                success: true,
                payload: {
                    locationName: 'Location A',
                    scatterDataDTO: [
                        {
                            itemPrice: 100,
                            supplyQuantity: 50,
                            demandQuantity: 30,
                            itemName: 'Item 1'
                        },
                        {
                            itemPrice: 200,
                            supplyQuantity: 20,
                            demandQuantity: 60,
                            itemName: 'Item 2'
                        },
                    ],
                },
            },
        };

        apiClient.get.mockResolvedValueOnce(mockData);

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        await waitFor(() => expect(screen.getByText(/location a/i)).toBeInTheDocument(), { timeout: 1000 });

        expect(screen.getByText(/item 1/i)).toBeInTheDocument();
        expect(screen.getByText(/item 2/i)).toBeInTheDocument();
    });

    test('should show error toast on 404 response', async () => {
        const mockError = {
            data: {
                status: 404,
                message: 'Data not found'
            }
        };

        apiClient.get.mockResolvedValueOnce(mockError);

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Data not found'), { timeout: 1000 });
    });

    test('should handle network error gracefully', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Network Error'));

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Failed to fetch locations'), { timeout: 1000 });
    });

    test('should fetch and set initial locations without search term', async () => {
        const mockLocationData = {
            data: {
                payload: {
                    content: [
                        JSON.stringify({ locationId: 1, locationDesc: 'Location A' }),
                        JSON.stringify({ locationId: 2, locationDesc: 'Location B' })
                    ]
                }
            }
        };

        apiClient.get.mockResolvedValueOnce(mockLocationData);

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        // Wait for the locations to be fetched and rendered
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith(`/locations/ids?search=`));
    });

    test('should fetch and filter locations based on search term', async () => {
        const mockSearchData = {
            data: {
                payload: {
                    content: [
                        JSON.stringify({ locationId: 3, locationDesc: 'Search Location' })
                    ]
                }
            }
        };

        apiClient.get.mockResolvedValueOnce(mockSearchData);

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        const searchInput = screen.getByLabelText(/search location/i);
        
        // Simulate user typing 'Search' in the Autocomplete input
        await user.type(searchInput, 'Search');
        
        // Wait for the API call and results to show up
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith(`/locations/ids?search=Search`));

    });

    test('should handle empty response (no locations found)', async () => {
        const mockEmptyResponse = {
            data: {
                payload: {
                    content: []
                }
            }
        };

        apiClient.get.mockResolvedValueOnce(mockEmptyResponse);

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        // Wait for the empty locations response
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith(`/locations/ids?search=`));
        expect(screen.queryByText('Location A')).not.toBeInTheDocument();
        expect(screen.queryByText('Location B')).not.toBeInTheDocument();
    });

    test('should show error toast when fetching locations fails', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Network Error'));

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );

        // Wait for the error to be handled and the toast to be shown
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Failed to fetch locations'));
    });
  
    test('Featchlocations based on search term', async () => {

       // Mock the first API call to fetch locations successfully
    apiClient.get.mockResolvedValueOnce({
        data: {
          payload: {
            content: ['{"locationId": "1", "locationDesc": "Location 1"}'],
          },
        },
      });
        
       // Mock the second API call to fetch supply data successfully
    apiClient.get.mockResolvedValueOnce({
        data: {
          success: true,
          payload: {
            scatterDataDTO: [
              {
                itemName: 'Item 1',
                supplyQuantity: 100,
                demandQuantity: 80,
                itemPrice: 200,
              },
            ],
            locationName: 'Location 1',
          },
        },
      });

        render(
            <Provider store={store}>
                <SupplyDemandBarChart />
            </Provider>
        );
        const locationInput = await screen.findByLabelText(/search location/i);
        fireEvent.change(locationInput, { target: { value: 'Location 1' } });
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith(`/locations/ids?search=`));
    });
    
});
