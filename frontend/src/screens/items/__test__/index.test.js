import React from 'react';
import { act, render, screen, fireEvent, waitFor } from '@testing-library/react';
import ReactVirtualizedTable from '../index';
import apiClient from '../../../components/baseUrl';
import { errorToast } from '../../../components/Toast';
import NewItemForm from '../AddNewItem';
import EditFormItem from '../EditFormItem';
import DeleteItem from '../DeleteItem';
import TableItem from '../TableItem';

// Mock the apiClient
jest.mock('../../../components/baseUrl');
jest.mock('../../../components/Toast', () => ({
  errorToast: jest.fn(),
}));

describe('ReactVirtualizedTable Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  test('fetches and displays items data correctly', async () => {
    // Mock API call to return the items data
    const mockItemsData = {
      content: [
        {
          id: "1",
          itemId: "000001",
          itemDescription: "Shirt",
          category: "Apparel",
          type: "CLOTHING",
          price: 100,
          pickupAllowed: true,
          shippingAllowed: true,
          deliveryAllowed: true,
          status: "ACTIVE",
        },
      ],
      page: {
        totalElements: 1,
      },
    };

    // Mock the API call response
    apiClient.get.mockResolvedValue({
      data: { success: true, payload: mockItemsData },
    });

    // Render the component
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    // Check for item description and ID
    await waitFor(() => {
      expect(screen.getByText('Shirt')).toBeInTheDocument();
      expect(screen.getByText('ACTIVE')).toBeInTheDocument();
      expect(screen.getByText('₹100')).toBeInTheDocument();
    });
  });

  test('opens add item dialog on button click', async () => {
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });
    
    fireEvent.click(screen.getByRole('button', { name: /add item/i }));
    
    expect(screen.getByText(/add new item/i)).toBeInTheDocument(); // Assuming the dialog contains this text
  });

  test('handles errors gracefully when fetching data', async () => {
    apiClient.get.mockRejectedValueOnce(new Error('Network Error'));
    await act(async () => {
      render(<ReactVirtualizedTable />);
    });
    await waitFor(() => expect(screen.getByText(/no items found/i)).toBeInTheDocument()); // Adjust based on your error handling
  });

  test('fetch fails and shows toast error', async () => {
    apiClient.get.mockResolvedValue({
      data: { status: 404, message: "Got an error." },
    });

    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/items?page=0&search='));
    expect(errorToast).toHaveBeenCalledWith("Got an error.");
  });

  test('opens edit and delete item dialog on button click', async () => {
    const mockItemsData = {
      content: [
        {
          id: "1",
          itemId: "000001",
          itemDescription: "Shirt",
          category: "Apparel",
          type: "CLOTHING",
          price: 100,
          pickupAllowed: true,
          shippingAllowed: true,
          deliveryAllowed: true,
          status: "ACTIVE",
        },
      ],
      page: {
        totalElements: 1,
      },
    };

    apiClient.get.mockResolvedValueOnce({
      data: { success: true, payload: mockItemsData },
    });

    await act(async () => {
      render(<ReactVirtualizedTable />);
    });

    await waitFor(() => expect(screen.getByText('Shirt')).toBeInTheDocument());

    fireEvent.click(screen.getByTestId('EditIcon'));

    await waitFor(() => {
      expect(screen.getByText('Edit Item...')).toBeInTheDocument();
      expect(screen.getByText('Shirt')).toBeInTheDocument();
    });

    fireEvent.click(screen.getByTestId('DeleteIcon'));

    await waitFor(() => {
      expect(screen.getByText('Confirm Delete...')).toBeInTheDocument();
    });
  });

  test('should show error toast on special characters in search query', () => {
    render(<ReactVirtualizedTable />);

    const searchInput = screen.getByLabelText(/search items/i);
    fireEvent.change(searchInput, { target: { value: '@@@' } });

    expect(errorToast).toHaveBeenCalledWith("Error: Search query contains special characters!");
  });

  test('handles search query changes correctly', async () => {
    render(<ReactVirtualizedTable />);

    const searchInput = screen.getByLabelText(/search items/i);
    fireEvent.change(searchInput, { target: { value: 'Item' } });
    expect(searchInput.value).toBe('Item');
  });
});
