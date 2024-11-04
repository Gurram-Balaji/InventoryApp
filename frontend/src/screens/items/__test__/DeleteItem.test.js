import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import DeleteItem from '../DeleteItem'; // Adjust the import path as necessary
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';

// Mock the toast functions
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('DeleteItem Component', () => {
    const setOpenDeleteDialogMock = jest.fn();
    const fetchItemsMock = jest.fn();
    const selectedItemMock = {
        itemid: 'item1',
        itemDescription: 'Item Description',
    };

    beforeEach(() => {
        jest.clearAllMocks(); // Clear previous mocks before each test
    });

    test('renders the dialog with selected item details', () => {
        render(<DeleteItem openDeleteDialog={true} selectedItem={selectedItemMock} fetchItems={fetchItemsMock} page={1} setOpenDeleteDialog={setOpenDeleteDialogMock} />);

        expect(screen.getByText(/confirm delete/i)).toBeInTheDocument();
        expect(screen.getByText(selectedItemMock.itemid)).toBeInTheDocument();
        expect(screen.getByText(selectedItemMock.itemDescription)).toBeInTheDocument();
    });

    test('calls successToast and fetchItems when deletion is successful', async () => {
        apiClient.delete.mockResolvedValueOnce({ data: { success: true } });

        render(<DeleteItem openDeleteDialog={true} selectedItem={selectedItemMock} fetchItems={fetchItemsMock} page={1} setOpenDeleteDialog={setOpenDeleteDialogMock} />);
        
        fireEvent.click(screen.getByRole('button', { name: /delete/i }));

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Item deleted successfully!");
            expect(fetchItemsMock).toHaveBeenCalledWith(1);
            expect(setOpenDeleteDialogMock).toHaveBeenCalledWith(false);
        });
    });

    test('calls errorToast when API returns 404 status', async () => {
        apiClient.delete.mockResolvedValueOnce({ data: { status: 404, message: "Item not found" } });

        render(<DeleteItem openDeleteDialog={true} selectedItem={selectedItemMock} fetchItems={fetchItemsMock} page={1} setOpenDeleteDialog={setOpenDeleteDialogMock} />);
        
        fireEvent.click(screen.getByRole('button', { name: /delete/i }));

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Item not found");
            expect(setOpenDeleteDialogMock).toHaveBeenCalledWith(false);
        });
    });

    test('calls errorToast on failed delete request', async () => {
        apiClient.delete.mockRejectedValueOnce(new Error("Failed to delete"));

        render(<DeleteItem openDeleteDialog={true} selectedItem={selectedItemMock} fetchItems={fetchItemsMock} page={1} setOpenDeleteDialog={setOpenDeleteDialogMock} />);
        
        fireEvent.click(screen.getByRole('button', { name: /delete/i }));

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to delete item");
            expect(setOpenDeleteDialogMock).toHaveBeenCalledWith(false);
        });
    });

    test('closes dialog when cancel button is clicked', () => {
        render(<DeleteItem openDeleteDialog={true} selectedItem={selectedItemMock} fetchItems={fetchItemsMock} page={1} setOpenDeleteDialog={setOpenDeleteDialogMock} />);
        
        fireEvent.click(screen.getByRole('button', { name: /cancel/i }));

        expect(setOpenDeleteDialogMock).toHaveBeenCalledWith(false);
    });
});
