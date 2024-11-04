import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import NewThresholdForm from '../AddNewThreshold'; // Adjust the path as needed
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';

// Mocking the apiClient and toast functions
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('NewThresholdForm', () => {
    const mockFetchRow = jest.fn();
    const mockSetOpenAddDialog = jest.fn();
    const demandOptions = ['HARD_PROMISED', 'SOFT_PROMISED'];

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders all form fields correctly', async () => {
        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        expect(screen.getByLabelText(/Search Location/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Search Item/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Min Threshold/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Max Threshold/i)).toBeInTheDocument();
    });

    test('shows validation error when required fields are missing', async () => {
        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Please fill in all required fields.');
        });
    });

   

    test('submits the form successfully with valid inputs', async () => {
        const mockLocationData = [{ locationId: "01501", locationDesc: "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ itemId: "000001", itemDescription: "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });
        apiClient.post.mockResolvedValueOnce({
            data: { success: true },
        });

        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        // Simulate filling the 'Search Location' field
        const locationInput = screen.getByLabelText(/Search Location/i);
        userEvent.type(locationInput, 'Cen');
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Cen'));
        await waitFor(() => expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument());
        fireEvent.click(screen.getByText('Central Warehouse - Bangalore'));

        // Simulate filling the 'Search Item' field
        const itemInput = screen.getByLabelText(/Search Item/i);
        userEvent.type(itemInput, 'Red');
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/items/ids?search=Red'));
        await waitFor(() => expect(screen.getByText('Red T-Shirt')).toBeInTheDocument());
        fireEvent.click(screen.getByText('Red T-Shirt'));

        // Fill in valid thresholds
        fireEvent.change(screen.getByLabelText(/Min Threshold/i), { target: { value: 10 } });
        fireEvent.change(screen.getByLabelText(/Max Threshold/i), { target: { value: 20 } });

        // Submit the form
        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith('Threshold added successfully!');
            expect(mockFetchRow).toHaveBeenCalledWith(1);
            expect(mockSetOpenAddDialog).toHaveBeenCalledWith(false);
        });
    });

    test('shows error when API fails during submission', async () => {
        const mockLocationData = [{ locationId: "01501", locationDesc: "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ itemId: "000001", itemDescription: "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });
        apiClient.post.mockRejectedValueOnce(new Error('API Error'));

        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        // Simulate filling the 'Search Location' field
        const locationInput = screen.getByLabelText(/Search Location/i);
        userEvent.type(locationInput, 'Cen');
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Cen'));
        await waitFor(() => expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument());
        fireEvent.click(screen.getByText('Central Warehouse - Bangalore'));

        // Simulate filling the 'Search Item' field
        const itemInput = screen.getByLabelText(/Search Item/i);
        userEvent.type(itemInput, 'Red');
        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/items/ids?search=Red'));
        await waitFor(() => expect(screen.getByText('Red T-Shirt')).toBeInTheDocument());
        fireEvent.click(screen.getByText('Red T-Shirt'));

        // Fill in valid thresholds
        fireEvent.change(screen.getByLabelText(/Min Threshold/i), { target: { value: 10 } });
        fireEvent.change(screen.getByLabelText(/Max Threshold/i), { target: { value: 20 } });

        // Submit the form
        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to add threshold');
        });
    });

    test('fetches location options based on user input', async () => {
        const mockLocationData = [{ locationId: "01501", locationDesc: "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValue({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });

        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const locationInput = screen.getByLabelText(/Search Location/i);
        userEvent.type(locationInput, 'Cen');

        await waitFor(() => {
            expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Cen');
            expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument();
        });
    });

    test('fetches item options based on user input', async () => {
        const mockItemData = [{ itemId: "000001", itemDescription: "Red T-Shirt" }];
        apiClient.get.mockResolvedValue({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });

        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const itemInput = screen.getByLabelText(/Search Item/i);
        userEvent.type(itemInput, 'Red');

        await waitFor(() => {
            expect(apiClient.get).toHaveBeenCalledWith('/items/ids?search=Red');
            expect(screen.getByText('Red T-Shirt')).toBeInTheDocument();
        });
    });


    test('closes the dialog when Cancel is clicked', async () => {
        render(
            <NewThresholdForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const cancelButton = screen.getByText(/Cancel/i);
        userEvent.click(cancelButton);
        await waitFor(() => expect(mockSetOpenAddDialog).toHaveBeenCalledWith(false));
    });

    
});
