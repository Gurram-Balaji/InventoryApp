import React from 'react';
import { act, render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReactVirtualizedTable from '../index';
import apiClient from '../../../components/baseUrl';
import { errorToast } from '../../../components/Toast';
import { MemoryRouter } from 'react-router-dom'; // For providing routing context
import userEvent from '@testing-library/user-event';

// Mock the apiClient
jest.mock('../../../components/baseUrl');

jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

describe('ReactVirtualizedTable Component', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('fetches and displays location data correctly', async () => {
        // Mock API call to return the location data
        const mockLocationData = {
            content: [
                {
                    id: "1",
                    locationId: "01501",
                    locationDesc: "Central Warehouse - Bangalore",
                    locationType: "WAREHOUSE",
                    addressLine1: "123 Main St",
                    addressLine2: "",
                    addressLine3: "",
                    city: "Bangalore",
                    state: "Karnataka",
                    country: "India",
                    pinCode: "560001",
                    pickupAllowed: true,
                    shippingAllowed: true,
                    deliveryAllowed: true,
                },
            ],
            page: {
                size: 8,
                number: 0,
                totalElements: 1,
                totalPages: 1,
            },
        };

        // Mock the API call response
        apiClient.get.mockResolvedValue({
            data: { success: true, payload: mockLocationData },
        });

        // Render the component inside a router (if it depends on routing)
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        // Verify that the API call is made with the correct parameters
        await waitFor(() =>
            expect(apiClient.get).toHaveBeenCalledWith('/locations?page=0&search=')
        );

        // Check if the location data is rendered correctly
        await waitFor(() => expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument());
        expect(screen.getByText('WAREHOUSE')).toBeInTheDocument();
        expect(screen.getByText('123 Main St Bangalore Karnataka India 560001')).toBeInTheDocument();
    });

    test('opens add location dialog on button click', async () => {
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });
        fireEvent.click(screen.getByRole('button', { name: /add location/i }));

        expect(screen.getByText(/add new location/i)).toBeInTheDocument(); // Assuming the dialog contains this text
    });

    test('handles errors gracefully when fetching data', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Network Error'));
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });
        await waitFor(() => expect(screen.getByText(/No locations found/i)).toBeInTheDocument()); // Adjust based on your error handling
    });

    test('fetch fails and shows toast error', async () => {
        // Mock API call to return an error response
        apiClient.get.mockResolvedValue({
            data: { status: 404, message: "Got an error." },
        });

        // Render the component
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/locations?page=0&search='));
        expect(errorToast).toHaveBeenCalledWith("Got an error.");
    });

    test('opens edit and delete location dialog on button click', async () => {
        const mockLocationData = {
            content: [
                {
                    id: "1",
                    locationId: "01501",
                    locationDesc: "Central Warehouse - Bangalore",
                    locationType: "WAREHOUSE",
                    addressLine1: "123 Main St",
                    addressLine2: "",
                    addressLine3: "",
                    city: "Bangalore",
                    state: "Karnataka",
                    country: "India",
                    pinCode: "560001",
                    pickupAllowed: true,
                    shippingAllowed: true,
                    deliveryAllowed: true,
                },
            ],
            page: {
                size: 8,
                number: 0,
                totalElements: 1,
                totalPages: 1,
            },
        };

        // Mock the API response
        apiClient.get.mockResolvedValueOnce({
            data: { success: true, payload: mockLocationData },
        });

        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        // Wait for the location data to load and the edit button to be available
        await waitFor(() => expect(screen.getByTestId('EditIcon')).toBeInTheDocument());

        // Click the edit button
        fireEvent.click(screen.getByTestId('EditIcon'));

        // Wait for the edit dialog to appear
        await waitFor(() => {
            expect(screen.getByText('Edit Location...')).toBeInTheDocument();
            expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument();
        });

        // Click the delete button
        fireEvent.click(screen.getByTestId('DeleteIcon'));

        // Wait for the delete confirmation dialog to appear
        await waitFor(() => {
            expect(screen.getByText('Confirm Delete...')).toBeInTheDocument();
        });
    });

    test('should show error toast on special characters in search query', () => {
        render(<ReactVirtualizedTable />);

        const searchInput = screen.getByLabelText(/Search Location/i);
        fireEvent.change(searchInput, { target: { value: '@@@' } });

        expect(errorToast).toHaveBeenCalledWith("Error: Search query contains special characters!");
    });

    test('handles API error responses correctly', async () => {
        apiClient.get.mockRejectedValue(new Error('Network error'));

        render(<ReactVirtualizedTable />);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith(expect.any(Error));
        });
    });

    test('handles search query changes correctly', async () => {
        render(<ReactVirtualizedTable />);

        const searchInput = screen.getByLabelText(/Search Location/i);
        fireEvent.change(searchInput, { target: { value: 'Warehouse' } });
        expect(searchInput.value).toBe('Warehouse');
    });
});
