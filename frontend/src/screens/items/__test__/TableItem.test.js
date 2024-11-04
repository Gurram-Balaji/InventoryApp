import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import TableItem from '../TableItem'; // Adjust the import based on your directory structure
import { Riple } from 'react-loading-indicators';

describe('TableItem Component', () => {
  const mockHandleEditOpen = jest.fn();
  const mockHandleDeleteOpen = jest.fn();

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading indicator when loading is true', () => {
    render(
      <TableItem 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={true} 
        items={[]} 
      />
    );

    expect(screen.getByRole('status')).toBeInTheDocument(); // Riple component has a role of "status"
  });

  test('displays "No items found" when loading is false and items are empty', () => {
    render(
      <TableItem 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        items={[]} 
      />
    );

    expect(screen.getByText(/No items found/i)).toBeInTheDocument();
  });

  test('renders item data correctly when loading is false and items are provided', () => {
    const mockItemData = [
      {
        itemid: "000001",
        itemDescription: "Sample Item",
        category: "Electronics",
        type: "HSN123",
        status: "ACTIVE",
        price: 500,
        fulfillment: "Available",
      },
    ];

    render(
      <TableItem 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        items={mockItemData} 
      />
    );

    expect(screen.getByText('000001')).toBeInTheDocument();
    expect(screen.getByText('Sample Item')).toBeInTheDocument();
    expect(screen.getByText('Electronics')).toBeInTheDocument();
    expect(screen.getByText('HSN123')).toBeInTheDocument();
    expect(screen.getByText('ACTIVE')).toBeInTheDocument();
    expect(screen.getByText('₹500')).toBeInTheDocument();
    expect(screen.getByText('Available')).toBeInTheDocument();
  });

  test('triggers handleEditOpen when edit button is clicked', () => {
    const mockItemData = [
      {
        itemid: "000001",
        itemDescription: "Sample Item",
        category: "Electronics",
        type: "HSN123",
        status: "ACTIVE",
        price: 500,
        fulfillment: "Available",
      },
    ];

    render(
      <TableItem 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        items={mockItemData} 
      />
    );

    fireEvent.click(screen.getByTestId('EditIcon')); // Adjust if necessary to target the button
    expect(mockHandleEditOpen).toHaveBeenCalledWith(mockItemData[0]);
  });

  test('triggers handleDeleteOpen when delete button is clicked', () => {
    const mockItemData = [
      {
        itemid: "000001",
        itemDescription: "Sample Item",
        category: "Electronics",
        type: "HSN123",
        status: "ACTIVE",
        price: 500,
        fulfillment: "Available",
      },
    ];

    render(
      <TableItem 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        items={mockItemData} 
      />
    );

    fireEvent.click(screen.getByTestId('DeleteIcon')); // Adjust if necessary to target the button
    expect(mockHandleDeleteOpen).toHaveBeenCalledWith(mockItemData[0]);
  });
});
