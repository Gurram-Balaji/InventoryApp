import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import EditFormDemand from '../EditFormDemand'; // Adjust the path as needed
import { errorToast, successToast } from '../../../components/Toast';
import apiClient from '../../../components/baseUrl';

// Mock the toast functions and the apiClient
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('EditFormDemand', () => {
    const mockFetchRow = jest.fn();
    const mockSetEditData = jest.fn();
    const mockSetOpenEditDialog = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders the dialog and fields with correct values', () => {
        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: 10,
                    demandId: '123',
                }}
            />
        );

        expect(screen.getByText(/Edit Demand/i)).toBeInTheDocument();

        // Verify the input values
        expect(screen.getByLabelText(/Quantity/i)).toHaveValue(10);
        expect(screen.getByLabelText(/Item/i)).toHaveValue('item123');
        expect(screen.getByLabelText(/Location/i)).toHaveValue('location123');
        expect(screen.getByLabelText(/Demand Type/i)).toHaveValue('type A'); // Assuming the replace function works correctly
    });

    test('shows error toast when required fields are empty', async () => {
        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: '',
                    locationId: '',
                    demandType: '',
                    quantity: '',
                    demandId: '123',
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
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '-5',
                    demandId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Quantity should not be negitive.");
        });
    });

    test('calls successToast when demand is updated successfully', async () => {
        apiClient.patch.mockResolvedValueOnce({
            data: {
                success: true,
            },
        });

        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '10',
                    demandId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith("Demand updated successfully!");
            expect(mockFetchRow).toHaveBeenCalledWith(1); // Verify the row refresh is called
        });
    });

    test('shows error toast on API error', async () => {
        apiClient.patch.mockRejectedValueOnce(new Error('API error'));

        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '10',
                    demandId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update demand");
        });
    });

    test('shows error toast on 404 status code', async () => {
        apiClient.patch.mockResolvedValue({
            data: {
                success: true,
                status: 404,
                message: "Failed to update demand"
            },
        });

        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '10',
                    demandId: '123',
                }}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith("Failed to update demand");
        });
    });
    test('calls setOpenEditDialog when Cancel button is clicked', async () => {
        const mockFetchRow = jest.fn();
        const mockSetEditData = jest.fn();
        const mockSetOpenEditDialog = jest.fn();
        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '10',
                    demandId: '123',
                }}
            />
        );

        // Click the Cancel button
        const cancelButton = screen.getByRole('button', { name: /Cancel/i });
        userEvent.click(cancelButton);

        // Verify that setOpenEditDialog was called with false
        await waitFor(()=>expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false));
    });

    test('calls setOpenEditDialog when Save button is clicked', async () => {
        const mockFetchRow = jest.fn();
        const mockSetEditData = jest.fn();
        const mockSetOpenEditDialog = jest.fn();
        apiClient.patch.mockResolvedValueOnce({
            data: {
                success: true,
            },
        });

        render(
            <EditFormDemand
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={{
                    itemId: 'item123',
                    locationId: 'location123',
                    demandType: 'type_A',
                    quantity: '10',
                    demandId: '123',
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

    test('calls success toast and fetchRow on successful update', async () => {
        const defaultEditData = {
            itemId: 'item123',
            locationId: 'location123',
            demandType: 'type_A',
            quantity: '10',
            demandId: '123',
          };
        
        const successfulEditData = {
          ...defaultEditData,
          quantity: '20',
        };
      
        apiClient.patch.mockResolvedValueOnce({
          data: { success: true },
        });
      
        render(
          <EditFormDemand
            openEditDialog={true}
            setOpenEditDialog={mockSetOpenEditDialog}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={mockSetEditData}
            editData={successfulEditData}
          />
        );
      
        const saveButton = screen.getByRole('button', { name: /Save/i });
        userEvent.click(saveButton); // Simulate clicking the Save button
      
        await waitFor(() => {
          expect(successToast).toHaveBeenCalledWith("Demand updated successfully!"); // Check success toast
          expect(mockFetchRow).toHaveBeenCalledWith(1); // Check if fetchRow is called
        });
      });

      test('renders dialog with correct title and reads data', () => {
        const mockFetchRow = jest.fn();
        const mockSetEditData = jest.fn();
        const mockSetOpenEditDialog = jest.fn();
      
        const defaultEditData = {
          itemId: 'item123',
          locationId: 'location123',
          demandType: 'type_A',
          quantity: 10,
          demandId: '123',
        };
      
        render(
          <EditFormDemand
            openEditDialog={true}
            setOpenEditDialog={mockSetOpenEditDialog}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={mockSetEditData}
            editData={defaultEditData}
          />
        );
    
        // Check if dialog title is rendered correctly
        expect(screen.getByText(/Edit Demand.../i)).toBeInTheDocument();
    
        // Verify that the item, location, and demand type fields are read-only and display correct values
        expect(screen.getByLabelText(/Item/i)).toHaveValue(defaultEditData.itemId);
        expect(screen.getByLabelText(/Location/i)).toHaveValue(defaultEditData.locationId);
        expect(screen.getByLabelText(/Demand Type/i)).toHaveValue(defaultEditData.demandType.replace(/_/g, ' '));
    
        // Check if the quantity field is editable
        const quantityField = screen.getByLabelText(/Quantity/i);
        expect(quantityField).toHaveValue(defaultEditData.quantity);
        expect(quantityField).not.toHaveAttribute('readonly'); // Verify it is not read-only
      });
    

  
});
