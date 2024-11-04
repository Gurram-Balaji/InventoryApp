import React from 'react';
import { act, render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReactVirtualizedTable from '../index';
import apiClient from '../../../components/baseUrl';
import { MemoryRouter } from 'react-router-dom'; // For providing routing context
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


  test('fetches and displays demand data correctly', async () => {
    // Mock API call to return the demand data
    const mockDemandData = {
      content: [
        {
          demandId: "66e7c97d1b8ef4959d8cf067",
          itemId: "000001",
          itemDescription: "Shirt",
          locationId: "01501",
          locationDescription: "Central Warehouse - Bangalore",
          demandType: "HARD_PROMISED",
          quantity: 54,
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
      data: { success: true, payload: mockDemandData },
    });

    // Render the component inside a router (if it depends on routing)
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    // Verify that the API call is made with the correct parameters
    await waitFor(() =>
      expect(apiClient.get).toHaveBeenCalledWith('/demand/all?page=0&search=&searchBy=item')
    );

    // Check if the demand data is rendered correctly
    await waitFor(() => {
      // Check for item description and ID
      expect(screen.getByText('Shirt (000001)')).toBeInTheDocument();
      // Check for location description and ID
      expect(screen.getByText('Central Warehouse - Bangalore (01501)')).toBeInTheDocument();
      // Check for demand type
      expect(screen.getByText('HARD PROMISED')).toBeInTheDocument();
      // Check for quantity
      expect(screen.getByText('54')).toBeInTheDocument();
    });
  });

  test('opens add demand dialog on button click', async () => {
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });
    fireEvent.click(screen.getByRole('button', { name: /add demand/i }));

    expect(screen.getByText(/add new demand/i)).toBeInTheDocument(); // Assuming the dialog contains this text
  });


  test('handles errors gracefully when fetching data', async () => {
    apiClient.get.mockRejectedValueOnce(new Error('Network Error'));
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });
    await waitFor(() => expect(screen.getByText(/No demand found./i)).toBeInTheDocument()); // Adjust based on your error handling
  });


  test('fetche fails tost error ', async () => {
    // Mock API call to return the demand data
    apiClient.get
      .mockResolvedValue({
        data: { status: 404, message: "Got an error." },
      });

    // Render the component

    render(<ReactVirtualizedTable />);

    await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/demand/all?page=0&search=&searchBy=item'));
    expect(errorToast).toHaveBeenCalledWith("Got an error.");
  });

  test('opens edit and delete demand dialog on button click', async () => {
    const mockDemandData = {
      content: [
        {
          demandId: "66e7c97d1b8ef4959d8cf067",
          itemId: "000001",
          itemDescription: "Shirt",
          locationId: "01501",
          locationDescription: "Central Warehouse - Bangalore",
          demandType: "HARD_PROMISED",
          quantity: 54,
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
      data: { success: true, payload: mockDemandData },
    })
      .mockResolvedValueOnce({
        data: { success: true, payload: mockDemandData },
      })
      .mockResolvedValueOnce({
        data: { success: true, payload: mockDemandData },
      });
    apiClient.patch.mockResolvedValueOnce({
      data: {
        "success": true,
        "timestamp": "2024-10-17T11:03:53.76047375",
        "message": "Demand Updated.",
        "payload": {
          "demandId": "66e7c97d1b8ef4959d8cf067",
          "demandType": "HARD_PROMISED",
          "quantity": 100,
          "itemId": "000001",
          "locationId": "01501"
        }
      }
    });

    // Render the component
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    // Wait for the demand data to load and the edit button to be available
    await waitFor(() => expect(screen.getByTestId('EditIcon')).toBeInTheDocument());

    // Click the edit button
    fireEvent.click(screen.getByTestId('EditIcon'));

    // Wait for the edit dialog to appear
    await waitFor(() => {
      expect(screen.getByText('Edit Demand...')).toBeInTheDocument();
      expect(screen.getByText('Shirt (000001)')).toBeInTheDocument();
    });

    // Change the quantity value
    fireEvent.change(screen.getByLabelText('Quantity'), { target: { value: 100 } });
    const saveButton = screen.getByRole('button', { name: /Save/i });
    userEvent.click(saveButton); // Simulate clicking the Save button

    // Click the delete button
    fireEvent.click(screen.getByTestId('DeleteIcon'));

    // Wait for the delete confirmation dialog to appear
    await waitFor(() => {
      expect(screen.getByText('Confirm Delete...')).toBeInTheDocument();
      expect(screen.getByText('Central Warehouse - Bangalore (01501)')).toBeInTheDocument();
    });

    // Click the delete button in the confirmation dialog
    fireEvent.click(screen.getByRole('button', { name: /delete/i }));

  });
  test('should show error toast on special characters in search query', () => {
    render(<ReactVirtualizedTable />);

    const searchInput = screen.getByLabelText(/Search Demand/i);
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

    const searchInput = screen.getByLabelText(/Search Demand/i);
    fireEvent.change(searchInput, { target: { value: 'Item' } });
    expect(searchInput.value).toBe('Item');
  });


});
