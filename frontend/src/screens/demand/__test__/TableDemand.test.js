import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import TableDemand from '../TableDemand'; // Adjust the import based on your directory structure
import { Riple } from 'react-loading-indicators';

describe('TableDemand Component', () => {
  const mockHandleEditOpen = jest.fn();
  const mockHandleDeleteOpen = jest.fn();

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading indicator when loading is true', () => {
    render(
      <TableDemand 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={true} 
        demand={[]} 
      />
    );

    expect(screen.getByRole('status')).toBeInTheDocument(); // Riple component has a role of "status"
  });

  test('displays "No demand found" when loading is false and demand is empty', () => {
    render(
      <TableDemand 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        demand={[]} 
      />
    );

    expect(screen.getByText(/No demand found/i)).toBeInTheDocument();
  });

  test('renders demand data correctly when loading is false and demand is provided', () => {
    const mockDemandData = [
      {
        demandId: "66e7c97d1b8ef4959d8cf067",
        itemId: "000001",
        locationId: "01501",
        demandType: "HARD_PROMISED",
        quantity: 54,
      },
    ];

    render(
      <TableDemand 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        demand={mockDemandData} 
      />
    );

    expect(screen.getByText('000001')).toBeInTheDocument();
    expect(screen.getByText('01501')).toBeInTheDocument();
    expect(screen.getByText('HARD PROMISED')).toBeInTheDocument();
    expect(screen.getByText('54')).toBeInTheDocument();
  });

  test('triggers handleEditOpen when edit button is clicked', () => {
    const mockDemandData = [
      {
        demandId: "66e7c97d1b8ef4959d8cf067",
        itemId: "000001",
        locationId: "01501",
        demandType: "HARD_PROMISED",
        quantity: 54,
      },
    ];

    render(
      <TableDemand 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        demand={mockDemandData} 
      />
    );

    fireEvent.click(screen.getByTestId('EditIcon'));
    expect(mockHandleEditOpen).toHaveBeenCalledWith(mockDemandData[0]);
  });

  test('triggers handleDeleteOpen when delete button is clicked', () => {
    const mockDemandData = [
      {
        demandId: "66e7c97d1b8ef4959d8cf067",
        itemId: "000001",
        locationId: "01501",
        demandType: "HARD_PROMISED",
        quantity: 54,
      },
    ];

    render(
      <TableDemand 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        demand={mockDemandData} 
      />
    );

    fireEvent.click(screen.getByTestId('DeleteIcon'));
    expect(mockHandleDeleteOpen).toHaveBeenCalledWith(mockDemandData[0]);
  });
});
