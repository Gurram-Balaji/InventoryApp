import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import Delete from '../DeleteDemand'; // Adjust the path as needed
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';
import { wait } from '@testing-library/user-event/dist/cjs/utils/index.js';

// Mock the toast functions and the apiClient
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('Delete', () => {
    const mockFetchRow = jest.fn();
    const mockSetOpenDeleteDialog = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders the dialog with correct information', () => {
        const selectedRow = {
            demandId: 'demand123',
            locationId: 'location123',
            locationDesc: 'Location Description',
            itemId: 'item123',
            itemDescription: 'Item Description',
        };

        render(
            <Delete
                openDeleteDialog={true}
                selectedRow={selectedRow}
                fetchRow={mockFetchRow}
                page={1}
                setOpenDeleteDialog={mockSetOpenDeleteDialog}
            />
        );

        expect(screen.getByText(/Confirm Delete.../i)).toBeInTheDocument();
        expect(screen.getByText(selectedRow.locationId)).toBeInTheDocument();
        expect(screen.getByText(selectedRow.locationDesc)).toBeInTheDocument();
        expect(screen.getByText(selectedRow.itemId)).toBeInTheDocument();
        expect(screen.getByText(selectedRow.itemDescription)).toBeInTheDocument();
        expect(screen.getByText(/Are you sure you want to delete this demand?/i)).toBeInTheDocument();
    });

    test('calls successToast and fetchRow on successful delete', async () => {
        apiClient.delete.mockResolvedValueOnce({
            data: {  status:200,success: true },
        });

        const selectedRow = {
            demandId: 'demand123',
            locationId: 'location123',
            locationDesc: 'Location Description',
            itemId: 'item123',
            itemDescription: 'Item Description',
        };

        render(
            <Delete
                openDeleteDialog={true}
                selectedRow={selectedRow}
                fetchRow={mockFetchRow}
                page={1}
                setOpenDeleteDialog={mockSetOpenDeleteDialog}
            />
        );

        const deleteButton = screen.getByRole('button', { name: /Delete/i });
        userEvent.click(deleteButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Demand deleted successfully!");
            expect(mockFetchRow).toHaveBeenCalledWith(1);
            expect(mockSetOpenDeleteDialog).toHaveBeenCalledWith(false);
        });
    });

    test('shows error toast when delete returns 404 status', async () => {
        apiClient.delete.mockResolvedValueOnce({
            data: { success: false, status: 404, message: "Demand not found." },
        });

        const selectedRow = {
            demandId: 'demand123',
            locationId: 'location123',
            locationDesc: 'Location Description',
            itemId: 'item123',
            itemDescription: 'Item Description',
        };

        render(
            <Delete
                openDeleteDialog={true}
                selectedRow={selectedRow}
                fetchRow={mockFetchRow}
                page={1}
                setOpenDeleteDialog={mockSetOpenDeleteDialog}
            />
        );

        const deleteButton = screen.getByRole('button', { name: /Delete/i });
        userEvent.click(deleteButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Demand not found.");
            expect(mockSetOpenDeleteDialog).toHaveBeenCalledWith(false);
        });
    });

    test('shows error toast when delete fails', async () => {
        apiClient.delete.mockRejectedValueOnce(new Error('API error'));

        const selectedRow = {
            demandId: 'demand123',
            locationId: 'location123',
            locationDesc: 'Location Description',
            itemId: 'item123',
            itemDescription: 'Item Description',
        };

        render(
            <Delete
                openDeleteDialog={true}
                selectedRow={selectedRow}
                fetchRow={mockFetchRow}
                page={1}
                setOpenDeleteDialog={mockSetOpenDeleteDialog}
            />
        );

        const deleteButton = screen.getByRole('button', { name: /Delete/i });
        userEvent.click(deleteButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to delete Damand");
            expect(mockSetOpenDeleteDialog).toHaveBeenCalledWith(false);
        });
    });

    test('closes the dialog when Cancel button is clicked', async() => {
        const selectedRow = {
            demandId: 'demand123',
            locationId: 'location123',
            locationDesc: 'Location Description',
            itemId: 'item123',
            itemDescription: 'Item Description',
        };

        render(
            <Delete
                openDeleteDialog={true}
                selectedRow={selectedRow}
                fetchRow={mockFetchRow}
                page={1}
                setOpenDeleteDialog={mockSetOpenDeleteDialog}
            />
        );

        const cancelButton = screen.getByRole('button', { name: /Cancel/i });
        userEvent.click(cancelButton);

       await waitFor(()=>expect(mockSetOpenDeleteDialog).toHaveBeenCalledWith(false));
    });
});
