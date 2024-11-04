import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import NewItemForm from '../AddNewItem';
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';

jest.mock('../../../components/baseUrl'); // Mock the apiClient
jest.mock('../../../components/Toast'); // Mock Toast notifications

describe('NewItemForm', () => {
    const setOpenAddDialogMock = jest.fn();
    const fetchItemsMock = jest.fn();
    const statusOptions = ['ACTIVE', 'INACTIVE'];
    const page = 1;

    beforeEach(() => {
        render(
            <NewItemForm
                statusOptions={statusOptions}
                fetchItems={fetchItemsMock}
                page={page}
                setOpenAddDialog={setOpenAddDialogMock}
                openAddDialog={true}
            />
        );
    });

    afterEach(() => {
        jest.clearAllMocks();
    });

    test('renders the component with initial state', () => {
        expect(screen.getByLabelText(/item id/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/description/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/category/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/type/i)).toBeInTheDocument();
        expect(screen.getByText(/ACTIVE/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/price/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/pickup allowed/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/shipping allowed/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/delivery allowed/i)).toBeInTheDocument();
    });

    test('shows error toast when required fields are not filled', async () => {
        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });
    });

    test('shows error toast when price is negative', async () => {
        fireEvent.change(screen.getByLabelText(/item id/i), { target: { value: 'item-1' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Item Description' } });
        fireEvent.change(screen.getByLabelText(/category/i), { target: { value: 'Category' } });
        fireEvent.change(screen.getByLabelText(/type/i), { target: { value: 'Type' } });
        fireEvent.change(screen.getByLabelText(/price/i), { target: { value: '-10' } });

        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Price Should not be negitive.");
        });
    });

    test('submits valid data and calls fetchItems', async () => {
        fireEvent.change(screen.getByLabelText(/item id/i), { target: { value: 'item-1' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Item Description' } });
        fireEvent.change(screen.getByLabelText(/category/i), { target: { value: 'Category' } });
        fireEvent.change(screen.getByLabelText(/type/i), { target: { value: 'Type' } });
        fireEvent.change(screen.getByLabelText(/price/i), { target: { value: '10' } });

        apiClient.post.mockResolvedValueOnce({ data: { success: true } });

        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Item added successfully!");
            expect(fetchItemsMock).toHaveBeenCalledWith(page);
            expect(setOpenAddDialogMock).toHaveBeenCalledWith(false); // Close the dialog
        });
    });

    test('shows error toast on API failure', async () => {
        fireEvent.change(screen.getByLabelText(/item id/i), { target: { value: 'item-1' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Item Description' } });
        fireEvent.change(screen.getByLabelText(/category/i), { target: { value: 'Category' } });
        fireEvent.change(screen.getByLabelText(/type/i), { target: { value: 'Type' } });
        fireEvent.change(screen.getByLabelText(/price/i), { target: { value: '10' } });

        apiClient.post.mockRejectedValueOnce(new Error("API Error"));

        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to add item");
        });
    });

    test('toggles checkboxes', () => {
        const pickupCheckbox = screen.getByLabelText(/pickup allowed/i);
        const shippingCheckbox = screen.getByLabelText(/shipping allowed/i);
        const deliveryCheckbox = screen.getByLabelText(/delivery allowed/i);

        // Initially checked
        expect(pickupCheckbox).toBeChecked();
        expect(shippingCheckbox).toBeChecked();
        expect(deliveryCheckbox).toBeChecked();

        // Uncheck pickupAllowed
        fireEvent.click(pickupCheckbox);
        expect(pickupCheckbox).not.toBeChecked();

        // Uncheck shippingAllowed
        fireEvent.click(shippingCheckbox);
        expect(shippingCheckbox).not.toBeChecked();

        // Uncheck deliveryAllowed
        fireEvent.click(deliveryCheckbox);
        expect(deliveryCheckbox).not.toBeChecked();
    });

    test('closes dialog when cancel button is clicked', () => {
        const cancelButton = screen.getByRole('button', { name: /cancel/i });
        fireEvent.click(cancelButton);

        expect(setOpenAddDialogMock).toHaveBeenCalledWith(false); // Check dialog closed
    });

    test('changes status and submits successfully', async () => {
        fireEvent.change(screen.getByLabelText(/item id/i), { target: { value: 'item-1' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Item Description' } });
        fireEvent.change(screen.getByLabelText(/category/i), { target: { value: 'Category' } });
        fireEvent.change(screen.getByLabelText(/type/i), { target: { value: 'Type' } });
        fireEvent.change(screen.getByLabelText(/price/i), { target: { value: '10' } });

        apiClient.post.mockResolvedValueOnce({ data: { success: true } });

        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Item added successfully!");
            expect(fetchItemsMock).toHaveBeenCalledWith(page); // Check fetchItems was called
            expect(setOpenAddDialogMock).toHaveBeenCalledWith(false); // Check dialog closed
        });
    });

    test('shows error toast when API returns a 404 status', async () => {
        fireEvent.change(screen.getByLabelText(/item id/i), { target: { value: 'item-1' } });
        fireEvent.change(screen.getByLabelText(/description/i), { target: { value: 'Item Description' } });
        fireEvent.change(screen.getByLabelText(/category/i), { target: { value: 'Category' } });
        fireEvent.change(screen.getByLabelText(/type/i), { target: { value: 'Type' } });
        fireEvent.change(screen.getByLabelText(/price/i), { target: { value: '10' } });
    
        // Mocking the API response to return a 404 error
        apiClient.post.mockResolvedValueOnce({
            data: {
                status: 404,
                message: "Item not found"
            }
        });
    
        const saveButton = screen.getByRole('button', { name: /save/i });
        fireEvent.click(saveButton);
    
        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Item not found");
        });
    });

    
});
