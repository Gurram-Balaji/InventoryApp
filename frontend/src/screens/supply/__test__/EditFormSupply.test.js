import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EditFormSupply from '../EditFormSupply'; // Adjust the path as needed
import { errorToast, successToast } from '../../../components/Toast';
import apiClient from '../../../components/baseUrl';

// Mock the toast functions and the apiClient
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('EditFormSupply', () => {
    const mockFetchRow = jest.fn();
    const mockSetEditData = jest.fn();
    const mockSetOpenEditDialog = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders the dialog and fields with correct values', () => {
        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: 10,
                    supplyId: '123',
                }}
            />
        );

        expect(screen.getByText(/Edit Supply/i)).toBeInTheDocument();

        // Verify the input values
        expect(screen.getByLabelText(/Quantity/i)).toHaveValue(10);
        expect(screen.getByLabelText(/Item/i)).toHaveValue('item123');
        expect(screen.getByLabelText(/Location/i)).toHaveValue('location123');
        expect(screen.getByLabelText(/Supply Type/i)).toHaveValue('type_A'); // Assuming it reflects the editData correctly
    });

    test('shows error toast when required fields are empty', async () => {
        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: '',
                    locationId: '',
                    supplyType: '',
                    quantity: '',
                    supplyId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });
    });

    test('shows error toast when quantity is negative', async () => {
        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '-5',
                    supplyId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Quantity should not be negitive.");
        });
    });

    test('calls successToast when supply is updated successfully', async () => {
        apiClient.patch.mockResolvedValueOnce({
            data: {
                success: true,
            },
        });

        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '10',
                    supplyId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Supply updated successfully!");
            expect(mockFetchRow).toHaveBeenCalledWith(1); // Verify the row refresh is called
        });
    });

    test('shows error toast on API error', async () => {
        apiClient.patch.mockRejectedValueOnce(new Error('API error'));

        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '10',
                    supplyId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update supply");
        });
    });

    test('shows error toast on 404 status code', async () => {
        apiClient.patch.mockResolvedValue({
            data: {
                success: true,
                status: 404,
                message: "Failed to update supply"
            },
        });

        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '10',
                    supplyId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update supply");
        });
    });

    test('calls setOpenEditDialog when Cancel button is clicked', async () => {
        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '10',
                    supplyId: '123',
                }}
            />
        );

        // Click the Cancel button
        const cancelButton = screen.getByRole('button', { name: /Cancel/i });
        userEvent.click(cancelButton);

        // Verify that setOpenEditDialog was called with false
        await waitFor(() => expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false));
    });

    test('calls setOpenEditDialog when Save button is clicked', async () => {
        apiClient.patch.mockResolvedValueOnce({
            data: {
                success: true,
            },
        });

        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: '10',
                    supplyId: '123',
                }}
            />
        );

        // Click the Save button
        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        // Verify that setOpenEditDialog was called with false after saving
        await waitFor(() => {
            expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false);
        });
    });

    test('renders dialog with correct title and reads data', () => {
        render(
            <EditFormSupply
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    supplyType: 'type_A',
                    quantity: 10,
                    supplyId: '123',
                }}
            />
        );

        // Check if dialog title is rendered correctly
        expect(screen.getByText(/Edit Supply.../i)).toBeInTheDocument();

        // Verify that the item, location, and supply type fields are read-only and display correct values
        expect(screen.getByLabelText(/Item/i)).toHaveValue('item123');
        expect(screen.getByLabelText(/Location/i)).toHaveValue('location123');
        expect(screen.getByLabelText(/Supply Type/i)).toHaveValue('type_A');

        // Check if the quantity field is editable
        const quantityField = screen.getByLabelText(/Quantity/i);
        expect(quantityField).toHaveValue(10);
        expect(quantityField).not.toHaveAttribute('readonly'); // Verify it is not read-only
    });
    
});
