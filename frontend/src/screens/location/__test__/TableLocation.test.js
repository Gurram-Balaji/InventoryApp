import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import TableLocation from '../TableLocation'; // Adjust the import based on your directory structure
import { Riple } from 'react-loading-indicators';

describe('TableLocation Component', () => {
  const mockHandleEditOpen = jest.fn();
  const mockHandleDeleteOpen = jest.fn();

  afterEach(() => {
    jest.clearAllMocks();
  });

  test('renders loading indicator when loading is true', () => {
    render(
      <TableLocation 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={true} 
        location={[]} 
      />
    );

    expect(screen.getByRole('status')).toBeInTheDocument(); // Riple component has a role of "status"
  });

  test('displays "No locations found" when loading is false and location is empty', () => {
    render(
      <TableLocation 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        location={[]} 
      />
    );

    expect(screen.getByText(/No locations found/i)).toBeInTheDocument();
  });

  test('renders location data correctly when loading is false and location is provided', () => {
    const mockLocationData = [
      {
        locationId: "01501",
        locationDesc: "Main Warehouse",
        locationType: "WAREHOUSE",
        AddressLines: "123 Warehouse St, Cityville, State, 12345",
        fulfillment: [],
      },
    ];

    render(
      <TableLocation 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        location={mockLocationData} 
      />
    );

    expect(screen.getByText('01501')).toBeInTheDocument();
    expect(screen.getByText('Main Warehouse')).toBeInTheDocument();
    expect(screen.getByText('WAREHOUSE')).toBeInTheDocument();
    expect(screen.getByText('123 Warehouse St, Cityville, State, 12345')).toBeInTheDocument();
  });

  test('triggers handleEditOpen when edit button is clicked', () => {
    const mockLocationData = [
      {
        locationId: "01501",
        locationDesc: "Main Warehouse",
        locationType: "WAREHOUSE",
        AddressLines: "123 Warehouse St, Cityville, State, 12345",
        fulfillment: [],
      },
    ];

    render(
      <TableLocation 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        location={mockLocationData} 
      />
    );

    fireEvent.click(screen.getAllByRole('button')[0]); // Click the first EditIcon button
    expect(mockHandleEditOpen).toHaveBeenCalledWith(mockLocationData[0]);
  });

  test('triggers handleDeleteOpen when delete button is clicked', () => {
    const mockLocationData = [
      {
        locationId: "01501",
        locationDesc: "Main Warehouse",
        locationType: "WAREHOUSE",
        AddressLines: "123 Warehouse St, Cityville, State, 12345",
        fulfillment: [],
      },
    ];

    render(
      <TableLocation 
        handleEditOpen={mockHandleEditOpen} 
        handleDeleteOpen={mockHandleDeleteOpen} 
        loading={false} 
        location={mockLocationData} 
      />
    );

    fireEvent.click(screen.getAllByRole('button')[1]); // Click the first DeleteIcon button
    expect(mockHandleDeleteOpen).toHaveBeenCalledWith(mockLocationData[0]);
  });
});
