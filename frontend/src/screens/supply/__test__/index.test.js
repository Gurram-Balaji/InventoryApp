import React from 'react';
import { act, render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReactVirtualizedTable from '../index';
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

  test('fetches and displays supply data correctly', async () => {
    // Mock API call to return the supply data
    const mockSupplyData = {
      content: [
        {
          supplyId: "12345",
          itemId: "000001",
          itemDescription: "Shirt",
          locationId: "01501",
          locationDescription: "Central Warehouse - Bangalore",
          supplyType: "ONHAND",
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
      data: { success: true, payload: mockSupplyData },
    });

    // Render the component
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    // Verify that the API call is made with the correct parameters
    await waitFor(() =>
      expect(apiClient.get).toHaveBeenCalledWith('/supply/all?page=0&search=&searchBy=item')
    );

    // Check if the supply data is rendered correctly
    await waitFor(() => {
      expect(screen.getByText('Shirt (000001)')).toBeInTheDocument();
      expect(screen.getByText('Central Warehouse - Bangalore (01501)')).toBeInTheDocument();
      expect(screen.getByText('ONHAND')).toBeInTheDocument();
      expect(screen.getByText('54')).toBeInTheDocument();
    });
  });

  test('opens add supply dialog on button click', async () => {
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });
    fireEvent.click(screen.getByRole('button', { name: /add supply/i }));

    expect(screen.getByText(/add new supply/i)).toBeInTheDocument(); // Adjust based on your dialog text
  });

  test('handles errors gracefully when fetching supply data', async () => {
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
    render(<ReactVirtualizedTable />);

    await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/supply/all?page=0&search=&searchBy=item'));
    expect(errorToast).toHaveBeenCalledWith("Got an error.");
  });


  test('should show error toast on special characters in search query', () => {
    render(<ReactVirtualizedTable />);

    const searchInput = screen.getByLabelText(/Search Supply/i);
    fireEvent.change(searchInput, { target: { value: '@@@' } });

    expect(errorToast).toHaveBeenCalledWith("Error: Search query contains special characters!");
  });

  test('handles search query changes correctly', async () => {
    render(<ReactVirtualizedTable />);

    const searchInput = screen.getByLabelText(/Search Supply/i);
    fireEvent.change(searchInput, { target: { value: 'Item' } });
    expect(searchInput.value).toBe('Item');
  });
});
