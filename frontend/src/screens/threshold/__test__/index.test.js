import React from 'react';
import { act, render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReactVirtualizedTable from '../index'; // Adjust the import path as needed
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';
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

    test('fetches and displays threshold data correctly', async () => {
        // Mock API call to return the threshold data
        const mockThresholdData = {
            content: [
                {
                    thresholdId: "1",
                    itemId: "000001",
                    itemDescription: "Item A",
                    locationId: "01501",
                    locationDescription: "Warehouse A",
                    minThreshold: 5,
                    maxThreshold: 10,
                },
            ],
            page: {
                totalElements: 1,
            },
        };

        // Mock the API call response
        apiClient.get.mockResolvedValue({
            data: { success: true, payload: mockThresholdData },
        });

        // Render the component
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        // Verify that the API call is made with the correct parameters
        await waitFor(() =>
            expect(apiClient.get).toHaveBeenCalledWith('/atpThresholds/all?page=0&search=&searchBy=item')
        );

        // Check if the threshold data is rendered correctly
        await waitFor(() => {
            expect(screen.getByText('Item A (000001)')).toBeInTheDocument();
            expect(screen.getByText('Warehouse A (01501)')).toBeInTheDocument();
            expect(screen.getByText('5')).toBeInTheDocument();
            expect(screen.getByText('10')).toBeInTheDocument();
        });
    });

    test('opens add threshold dialog on button click', async () => {
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        // Simulate button click
        fireEvent.click(screen.getByRole('button', { name: /add threshold/i }));

        // Check if the add new threshold dialog is displayed
        await waitFor(() => expect(screen.getByText(/add new threshold/i)).toBeInTheDocument()); // Adjust based on your dialog text
    });

    test('handles errors gracefully when fetching threshold data', async () => {
        apiClient.get.mockRejectedValueOnce(new Error('Network Error'));
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        await waitFor(() => expect(errorToast).toHaveBeenCalledWith(expect.any(Error)));
    });

    test('fetch fails toast error', async () => {
        // Mock API call to return an error response
        apiClient.get.mockResolvedValue({
            data: { status: 404, message: "Got an error." },
        });

        // Render the component
        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/atpThresholds/all?page=0&search=&searchBy=item'));
        expect(errorToast).toHaveBeenCalledWith("Got an error.");
    });

    test('should show error toast on special characters in search query', async () => {
        render(<ReactVirtualizedTable />);

        const searchInput = screen.getByLabelText(/Search Threshold/i);
        fireEvent.change(searchInput, { target: { value: '@@@' } });

        expect(errorToast).toHaveBeenCalledWith("Error: Search query contains special characters!");
    });

    test('handles search query changes correctly', async () => {
        render(<ReactVirtualizedTable />);

        const searchInput = screen.getByLabelText(/Search Threshold/i);
        fireEvent.change(searchInput, { target: { value: 'Item' } });
        expect(searchInput.value).toBe('Item');
    });


    test('opens edit dialog on row click', async () => {
        const mockThresholdData = {
            content: [
                {
                    thresholdId: "1",
                    itemId: "000001",
                    itemDescription: "Item A",
                    locationId: "01501",
                    locationDescription: "Warehouse A",
                    minThreshold: 5,
                    maxThreshold: 10,
                },
            ],
            page: {
                totalElements: 1,
            },
        };

        // Mock the API call response
        apiClient.get.mockResolvedValue({
            data: { success: true, payload: mockThresholdData },
        });

        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        const row = await screen.findByText('Item A (000001)'); // Adjust based on your data rendering
        fireEvent.click(row);

        expect(screen.getByTestId('EditIcon')).toBeInTheDocument(); // Check for edit dialog
    });

    test('opens delete dialog on delete button click', async () => {
        const mockThresholdData = {
            content: [
                {
                    thresholdId: "1",
                    itemId: "000001",
                    itemDescription: "Item A",
                    locationId: "01501",
                    locationDescription: "Warehouse A",
                    minThreshold: 5,
                    maxThreshold: 10,
                },
            ],
            page: {
                totalElements: 1,
            },
        };

        // Mock the API call response
        apiClient.get.mockResolvedValue({
            data: { success: true, payload: mockThresholdData },
        });

        await act(async () => {
            render(<ReactVirtualizedTable />);
        });

        const deleteButton = screen.getByTestId('DeleteIcon'); // Adjust based on your delete button
        fireEvent.click(deleteButton);

        expect(screen.getByText(/are you sure you want to delete/i)).toBeInTheDocument(); // Check for delete confirmation dialog
    });


});
