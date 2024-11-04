import React from 'react';
import { render, fireEvent, screen, act, waitFor } from '@testing-library/react';
import EditFormItem from '../EditFormItem';
import apiClient from '../../../components/baseUrl';
import { successToast, errorToast } from '../../../components/Toast';

// Mock the Toast notifications
jest.mock('../../../components/Toast', () => ({
    successToast: jest.fn(),
    errorToast: jest.fn(),
}));



// Mock the apiClient
jest.mock('../../../components/baseUrl');

describe('EditFormItem Component', () => {
    const mockFetchItems = jest.fn();
    const mockSetOpenEditDialog = jest.fn();
    const mockSetEditData = jest.fn();

    const editData = {
        itemid: '1',
        itemDescription: 'Test Item',
        category: 'Test Category',
        type: 'HSN123',
        price: 100,
        status: 'Active',
        pickupAllowed: true,
        shippingAllowed: false,
        deliveryAllowed: true,
    };

    const statusOptions = ['Active', 'Inactive'];

    beforeEach(async () => {
        await act(() => render(
            <EditFormItem
                openEditDialog={true}
                editData={editData}
                statusOptions={statusOptions}
                fetchItems={mockFetchItems}
                page={1}
                setOpenEditDialog={mockSetOpenEditDialog}
                setEditData={mockSetEditData}
            />
        ));
    });

    afterEach(() => {
        jest.clearAllMocks(); // Clear mocks after each test
    });

    test('renders correctly with provided edit data', () => {
        expect(screen.getByLabelText(/Item ID/i)).toHaveValue(editData.itemid);
        expect(screen.getByLabelText(/Description/i)).toHaveValue(editData.itemDescription);
        expect(screen.getByLabelText(/Category/i)).toHaveValue(editData.category);
        expect(screen.getByLabelText(/HSN Code/i)).toHaveValue(editData.type);
        expect(screen.getByLabelText(/Price/i)).toHaveValue(editData.price);
        expect(screen.getByLabelText(/Pickup Allowed/i)).toBeChecked();
        expect(screen.getByLabelText(/Shipping Allowed/i)).not.toBeChecked();
        expect(screen.getByLabelText(/Delivery Allowed/i)).toBeChecked();
    });

    test('calls setOpenEditDialog when Cancel button is clicked', async () => {
        fireEvent.click(screen.getByText(/Cancel/i));
        await waitFor(() => expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false));
    });



    test('calls apiClient.patch and shows success toast on successful update', async () => {
        apiClient.patch.mockResolvedValueOnce({ data: { success: true } });

        fireEvent.click(screen.getByText(/Save/i));

        expect(apiClient.patch).toHaveBeenCalledWith(`/items/${editData.itemid}`, editData);
        await waitFor(() => expect(successToast).toHaveBeenCalledWith("Item updated successfully!"));
        expect(mockFetchItems).toHaveBeenCalledWith(1); // Ensure fetchItems is called with the correct page
    });

    test('shows error toast when API returns a 404 error', async () => {
        apiClient.patch.mockResolvedValueOnce({ data: { status: 404, message: 'Item not found' } });

        fireEvent.click(screen.getByText(/Save/i));

        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Item not found'));
    });

    test('All Items change  event is triggered when category is changed', async () =>{
        // Simulate leaving the item ID field empty
        fireEvent.change(screen.getByLabelText(/Item ID/i), { target: { value: 're34' } });
        fireEvent.change(screen.getByLabelText(/Description/i), { target: { value: 'Test Item' } });
        fireEvent.change(screen.getByLabelText(/Category/i), { target: { value: 'Test Category' } });
        fireEvent.change(screen.getByLabelText(/Price/i), { target: { value: '100' } });
      
        // Trigger form submission
        fireEvent.click(screen.getByText(/Save/i));
        expect(apiClient.patch).toHaveBeenCalled();
      });
      

    test('shows error toast when API call fails', async () => {
        apiClient.patch.mockRejectedValueOnce(new Error('Network Error'));

        fireEvent.click(screen.getByText(/Save/i));

        await waitFor(() => expect(errorToast).toHaveBeenCalledWith("Failed to update item"));
    });


    test('updates price field correctly', () => {

        // Simulate user changing the price input field
        const priceInput = screen.getByLabelText(/Price/i);
        fireEvent.change(priceInput, { target: { value: '60' } });

        // Ensure setEditData was called with the correct value (as a number)
        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            price: 60,  // Expected updated price value as a number
        });


    });


});



describe('EditFormItem no inputs given', () => {
    const mockFetchItems = jest.fn();
    const mockSetOpenEditDialog = jest.fn();
    const mockSetEditData = jest.fn();

    const editData = {
        itemid: '1',
        itemDescription: 'Test Item',
        category: '',
        type: '',
        price: 100,
        status: 'Active',
        pickupAllowed: true,
        shippingAllowed: false,
        deliveryAllowed: true,
    };

    const statusOptions = ['Active', 'Inactive'];

    beforeEach(async () => {
        await act(() => render(
            <EditFormItem
                openEditDialog={true}
                editData={editData}
                statusOptions={statusOptions}
                fetchItems={mockFetchItems}
                page={1}
                setOpenEditDialog={mockSetOpenEditDialog}
                setEditData={mockSetEditData}
            />
        ));
    });

    test('shows error toast when required fields are missing', async () => {

        fireEvent.click(screen.getByText(/Save/i));

        // Assert that errorToast is called with the correct message
        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });

        // Ensure no API call is made
        expect(apiClient.patch).not.toHaveBeenCalled();
    });

});

describe('EditFormItem input field changes and validations', () => {
    const mockFetchItems = jest.fn();
    const mockSetOpenEditDialog = jest.fn();
    const mockSetEditData = jest.fn();

    const editData = {
        itemid: '1',
        itemDescription: 'Test Item',
        category: 'Test Category',
        type: 'HSN123',
        price: 100,
        status: 'Active',
        pickupAllowed: true,
        shippingAllowed: false,
        deliveryAllowed: true,
    };

    const statusOptions = ['Active', 'Inactive'];

    beforeEach(async () => {
        await act(() => render(
            <EditFormItem
                openEditDialog={true}
                editData={editData}
                statusOptions={statusOptions}
                fetchItems={mockFetchItems}
                page={1}
                setOpenEditDialog={mockSetOpenEditDialog}
                setEditData={mockSetEditData}
            />
        ));
    });

    afterEach(() => {
        jest.clearAllMocks(); // Clear mocks after each test
    });

    test('updates category field correctly', () => {
        const categoryInput = screen.getByLabelText(/Category/i);
        fireEvent.change(categoryInput, { target: { value: 'New Category' } });

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            category: 'New Category',
        });
    });

    test('updates HSN code field correctly', () => {
        const hsnCodeInput = screen.getByLabelText(/HSN Code/i);
        fireEvent.change(hsnCodeInput, { target: { value: 'HSN456' } });

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            type: 'HSN456',
        });
    });


    test('updates pickup allowed field correctly', () => {
        const pickupAllowedCheckbox = screen.getByLabelText(/Pickup Allowed/i);
        fireEvent.click(pickupAllowedCheckbox);

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            pickupAllowed: !editData.pickupAllowed,
        });
    });

    test('updates shipping allowed field correctly', () => {
        const shippingAllowedCheckbox = screen.getByLabelText(/Shipping Allowed/i);
        fireEvent.click(shippingAllowedCheckbox);

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            shippingAllowed: !editData.shippingAllowed,
        });
    });

    test('updates delivery allowed field correctly', () => {
        const deliveryAllowedCheckbox = screen.getByLabelText(/Delivery Allowed/i);
        fireEvent.click(deliveryAllowedCheckbox);

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            deliveryAllowed: !editData.deliveryAllowed,
        });
    });


    test('does not allow negative price', () => {
        const priceInput = screen.getByLabelText(/Price/i);
        fireEvent.change(priceInput, { target: { value: '-50' } });

        expect(mockSetEditData).toHaveBeenCalledWith({
            ...editData,
            price: 0, // Non-negative value
        });
    });
});

