import React from 'react';
import { render, fireEvent, screen, waitFor } from '@testing-library/react';
import NewLocationForm from '../AddNewLocation';
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';

// Mock the Toast notifications
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

// Mock the API client
jest.mock('../../../components/baseUrl');

describe('NewLocationForm', () => {
    let fetchRow;
    let setOpenAddDialog;

    beforeEach(() => {
        fetchRow = jest.fn();
        setOpenAddDialog = jest.fn();
    });

    test('renders NewLocationForm component', () => {
        render(<NewLocationForm locationTypeOptions={[]} fetchRow={fetchRow} page={1} setOpenAddDialog={setOpenAddDialog} openAddDialog={true} />);
        
        expect(screen.getByText(/add new location/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/location id/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/address line 1/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/city/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/state/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/country/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/pin code/i)).toBeInTheDocument();
    });

    test('displays error when required fields are empty on save', async () => {
        render(<NewLocationForm locationTypeOptions={[]} fetchRow={fetchRow} page={1} setOpenAddDialog={setOpenAddDialog} openAddDialog={true} />);

        fireEvent.click(screen.getByText(/save/i));

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });
    });

    test('calls API and shows success toast on successful submission', async () => {
        apiClient.post.mockResolvedValueOnce({
            data: { success: true },
        });

        render(<NewLocationForm locationTypeOptions={[]} fetchRow={fetchRow} page={1} setOpenAddDialog={setOpenAddDialog} openAddDialog={true} />);

        // Fill in the required fields
        fireEvent.change(screen.getByLabelText(/location id/i), { target: { value: '123' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Test Location' } });
        fireEvent.change(screen.getByLabelText(/address line 1/i), { target: { value: '123 Main St' } });
        fireEvent.change(screen.getByLabelText(/city/i), { target: { value: 'Anytown' } });
        fireEvent.change(screen.getByLabelText(/state/i), { target: { value: 'CA' } });
        fireEvent.change(screen.getByLabelText(/country/i), { target: { value: 'USA' } });
        fireEvent.change(screen.getByLabelText(/pin code/i), { target: { value: '12345' } });

        fireEvent.click(screen.getByText(/save/i));

        await waitFor(() => {
            expect(apiClient.post).toHaveBeenCalledWith('/locations', expect.any(Object));
            expect(successToast).toHaveBeenCalledWith("Location added successfully!");
            expect(fetchRow).toHaveBeenCalledWith(1);
            expect(setOpenAddDialog).toHaveBeenCalledWith(false);
        });
    });

    test('shows error toast on API error', async () => {
        apiClient.post.mockRejectedValueOnce(new Error('API error'));

        render(<NewLocationForm locationTypeOptions={[]} fetchRow={fetchRow} page={1} setOpenAddDialog={setOpenAddDialog} openAddDialog={true} />);

        // Fill in the required fields
        fireEvent.change(screen.getByLabelText(/location id/i), { target: { value: '123' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Test Location' } });
        fireEvent.change(screen.getByLabelText(/address line 1/i), { target: { value: '123 Main St' } });
        fireEvent.change(screen.getByLabelText(/city/i), { target: { value: 'Anytown' } });
        fireEvent.change(screen.getByLabelText(/state/i), { target: { value: 'CA' } });
        fireEvent.change(screen.getByLabelText(/country/i), { target: { value: 'USA' } });
        fireEvent.change(screen.getByLabelText(/pin code/i), { target: { value: '12345' } });

        fireEvent.click(screen.getByText(/save/i));

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to add location");
        });
    });

    test('resets form fields after submission', async () => {
        apiClient.post.mockResolvedValueOnce({
            data: { success: true },
        });

        render(<NewLocationForm locationTypeOptions={[]} fetchRow={fetchRow} page={1} setOpenAddDialog={setOpenAddDialog} openAddDialog={true} />);

        // Fill in the required fields
        fireEvent.change(screen.getByLabelText(/location id/i), { target: { value: '123' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Test Location' } });
        fireEvent.change(screen.getByLabelText(/address line 1/i), { target: { value: '123 Main St' } });
        fireEvent.change(screen.getByLabelText(/city/i), { target: { value: 'Anytown' } });
        fireEvent.change(screen.getByLabelText(/state/i), { target: { value: 'CA' } });
        fireEvent.change(screen.getByLabelText(/country/i), { target: { value: 'USA' } });
        fireEvent.change(screen.getByLabelText(/pin code/i), { target: { value: '12345' } });

        fireEvent.click(screen.getByText(/save/i));

        await waitFor(() => {
            expect(screen.getByLabelText(/location id/i).value).toBe('');
            expect(screen.getByLabelText(/description/i).value).toBe('');
            expect(screen.getByLabelText(/address line 1/i).value).toBe('');
            expect(screen.getByLabelText(/city/i).value).toBe('');
            expect(screen.getByLabelText(/state/i).value).toBe('');
            expect(screen.getByLabelText(/country/i).value).toBe('');
            expect(screen.getByLabelText(/pin code/i).value).toBe('');
        });
    });
});


describe('NewLocationForm onChange Handlers', () => {
    const mockLocationTypeOptions = ['WAREHOUSE', 'STORE', 'OFFICE'];
    const mockFetchRow = jest.fn();
    const setOpenAddDialog = jest.fn();
    const page = 1;
    const openAddDialog = true;

    const setup = () => {
        render(<NewLocationForm 
            locationTypeOptions={mockLocationTypeOptions} 
            fetchRow={mockFetchRow} 
            page={page} 
            setOpenAddDialog={setOpenAddDialog} 
            openAddDialog={openAddDialog} 
        />);
    };

    test('should update locationId on input change', () => {
        setup();
        const locationIdInput = screen.getByLabelText('Location ID');
        fireEvent.change(locationIdInput, { target: { value: 'LOC001' } });
        expect(locationIdInput.value).toBe('LOC001');
    });

    test('should update locationDesc on input change', () => {
        setup();
        const locationDescInput = screen.getByLabelText('Description');
        fireEvent.change(locationDescInput, { target: { value: 'New Location' } });
        expect(locationDescInput.value).toBe('New Location');
    });

    test('should update addressLine1 on input change', () => {
        setup();
        const addressLine1Input = screen.getByLabelText('Address Line 1');
        fireEvent.change(addressLine1Input, { target: { value: '123 Main St' } });
        expect(addressLine1Input.value).toBe('123 Main St');
    });

    test('should update addressLine2 on input change', () => {
        setup();
        const addressLine2Input = screen.getByLabelText('Address Line 2');
        fireEvent.change(addressLine2Input, { target: { value: 'Suite 400' } });
        expect(addressLine2Input.value).toBe('Suite 400');
    });

    test('should update addressLine3 on input change', () => {
        setup();
        const addressLine3Input = screen.getByLabelText('Address Line 3');
        fireEvent.change(addressLine3Input, { target: { value: 'Building A' } });
        expect(addressLine3Input.value).toBe('Building A');
    });

    test('should update city on input change', () => {
        setup();
        const cityInput = screen.getByLabelText('City');
        fireEvent.change(cityInput, { target: { value: 'New York' } });
        expect(cityInput.value).toBe('New York');
    });

    test('should update state on input change', () => {
        setup();
        const stateInput = screen.getByLabelText('State');
        fireEvent.change(stateInput, { target: { value: 'NY' } });
        expect(stateInput.value).toBe('NY');
    });

    test('should update country on input change', () => {
        setup();
        const countryInput = screen.getByLabelText('Country');
        fireEvent.change(countryInput, { target: { value: 'USA' } });
        expect(countryInput.value).toBe('USA');
    });

    test('should update pinCode on input change', () => {
        setup();
        const pinCodeInput = screen.getByLabelText('Pin Code');
        fireEvent.change(pinCodeInput, { target: { value: '10001' } });
        expect(pinCodeInput.value).toBe('10001');
    });

    test('should update pickupAllowed on checkbox change', () => {
        setup();
        const pickupCheckbox = screen.getByLabelText('Pickup Allowed');
        fireEvent.click(pickupCheckbox); // Toggle checkbox
        expect(pickupCheckbox.checked).toBe(false); // It was initially checked (true), now false
    });

    test('should update shippingAllowed on checkbox change', () => {
        setup();
        const shippingCheckbox = screen.getByLabelText('Shipping Allowed');
        fireEvent.click(shippingCheckbox); // Toggle checkbox
        expect(shippingCheckbox.checked).toBe(false); // It was initially checked (true), now false
    });

    test('should update deliveryAllowed on checkbox change', () => {
        setup();
        const deliveryCheckbox = screen.getByLabelText('Delivery Allowed');
        fireEvent.click(deliveryCheckbox); // Toggle checkbox
        expect(deliveryCheckbox.checked).toBe(false); // It was initially checked (true), now false
    });
});