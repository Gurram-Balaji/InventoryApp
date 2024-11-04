import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EditFormThreshold from '../EditFormThreshold'; // Adjust the path as needed
import { errorToast, successToast } from '../../../components/Toast';
import apiClient from '../../../components/baseUrl';

// Mock the toast functions and the apiClient
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('EditFormThreshold', () => {
    const mockFetchRow = jest.fn();
    const mockSetEditData = jest.fn();
    const mockSetOpenEditDialog = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders the dialog and fields with correct values', () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
                }}
            />
        );

        expect(screen.getByText(/Edit Threshold.../i)).toBeInTheDocument();

        // Verify the input values
        expect(screen.getByLabelText(/Min Threshold/i)).toHaveValue(5);
        expect(screen.getByLabelText(/Max Threshold/i)).toHaveValue(15);
        expect(screen.getByLabelText(/Item/i)).toHaveValue('item123');
        expect(screen.getByLabelText(/Location/i)).toHaveValue('location123');
    });

    test('shows error toast when required fields are empty', async () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: '',
                    maxThreshold: '',
                    thresholdId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });
    });

    test('shows error toast when min threshold is greater than max threshold', async () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 20,
                    maxThreshold: 10,
                    thresholdId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update threshold");
        });
    });

    test('calls successToast when threshold is updated successfully', async () => {
        apiClient.patch.mockResolvedValueOnce({
            data: {
                success: true,
            },
        });

        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Threshold updated successfully!");
            expect(mockFetchRow).toHaveBeenCalledWith(1); // Verify the row refresh is called
        });
    });

    test('shows error toast on API error', async () => {
        apiClient.patch.mockRejectedValueOnce(new Error('API error'));

        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update threshold");
        });
    });

    test('shows error toast on 404 status code', async () => {
        apiClient.patch.mockResolvedValue({
            data: {
                success: true,
                status: 404,
                message: "Failed to update threshold"
            },
        });

        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update threshold");
        });
    });

    test('calls setOpenEditDialog when Cancel button is clicked', async () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
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
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 15,
                    thresholdId: '123',
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

    test('renders the dialog and fields with correct values', () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: 10,
                    thresholdId: '456',
                }}
            />
        );

        expect(screen.getByText(/Edit Threshold/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Min Threshold/i)).toHaveValue(5);
        expect(screen.getByLabelText(/Max Threshold/i)).toHaveValue(10);
    });

    test('updates minThreshold on change', async () => {
        const { rerender } = render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: '',
                    maxThreshold: 10,
                    thresholdId: '456',
                }}
            />
        );

        const minThresholdField = screen.getByLabelText(/Min Threshold/i);
        userEvent.clear(minThresholdField);
        userEvent.type(minThresholdField, '8');

        await waitFor(()=>expect(mockSetEditData).toHaveBeenCalledWith(expect.objectContaining({ minThreshold: '8' })));
    });

    test('updates maxThreshold on change', async () => {
        const { rerender } = render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    minThreshold: 5,
                    maxThreshold: '',
                    thresholdId: '456',
                }}
            />
        );

        const maxThresholdField = screen.getByLabelText(/Max Threshold/i);
        userEvent.clear(maxThresholdField);
        userEvent.type(maxThresholdField, '2');

        await waitFor(()=>expect(mockSetEditData).toHaveBeenCalledWith(expect.objectContaining({ maxThreshold: '2' })));
    });

    // Existing tests for handling the Save and Cancel actions...

    test('shows error toast when required fields are empty', async () => {
        render(
            <EditFormThreshold
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: '',
                    locationId: '',
                    minThreshold: '',
                    maxThreshold: '',
                    thresholdId: '456',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Please fill in all required fields.");
        });
    });
    
});
