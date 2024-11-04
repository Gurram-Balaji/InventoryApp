import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import NewDemandForm from '../AddNewDemand'; // Adjust the path as needed
import apiClient from '../../../components/baseUrl';
import { errorToast, successToast } from '../../../components/Toast';

// Mocking the apiClient and toast functions
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl'); // Mock the API client

describe('NewDemandForm', () => {
    const mockFetchRow = jest.fn();
    const mockSetOpenAddDialog = jest.fn();
    const demandOptions = ['HARD_PROMISED', 'SOFT_PROMISED'];

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders all form fields correctly', async () => {
        render(
            <NewDemandForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        expect(screen.getByLabelText(/Search Location/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Search Item/i)).toBeInTheDocument();

        // Alternatively, use getByDisplayValue to find the initial value, e.g., 'HARD_PROMISED'
        await waitFor(() => {
            expect(screen.getByDisplayValue('HARD_PROMISED')).toBeInTheDocument();
        });
        expect(screen.getByLabelText(/Quantity/i)).toBeInTheDocument();
    });

    test('shows validation error when required fields are missing', async () => {
        render(
            <NewDemandForm
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

    test('shows validation error when quantity is negative', async () => {
        const mockLocationData = [{ "locationId": "01501", "locationDesc": "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ "itemId": "000001", "itemDescription": "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });

        render(
            <NewDemandForm
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


        fireEvent.change(screen.getByLabelText(/Quantity/i), { target: { value: -5 } });

        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Quantity should not be negitive.');
        });
    });

    test('submits the form successfully with valid inputs', async () => {
        const mockLocationData = [{ "locationId": "01501", "locationDesc": "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ "itemId": "000001", "itemDescription": "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });
        apiClient.post.mockResolvedValueOnce({
            data: { success: true },
        });
        render(
            <NewDemandForm
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


        // Simulate filling the quantity field
        fireEvent.change(screen.getByLabelText(/Quantity/i), { target: { value: 10 } });

        // Submit the form
        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith('Demand added successfully!');
            expect(mockFetchRow).toHaveBeenCalledWith(1);
            expect(mockSetOpenAddDialog).toHaveBeenCalledWith(false);
        });
    });


    test('shows error when API fails during submission', async () => {
        const mockLocationData = [{ "locationId": "01501", "locationDesc": "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ "itemId": "000001", "itemDescription": "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });
        apiClient.post.mockRejectedValueOnce(new Error('API Error'));

        render(
            <NewDemandForm
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


        // Simulate filling the quantity field
        fireEvent.change(screen.getByLabelText(/Quantity/i), { target: { value: 10 } });

        // Submit the form
        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to add demand');
        });
    });

    test('fetches location options based on user input', async () => {
        const mockLocationData = [{ "locationId": "01501", "locationDesc": "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValue({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });

        render(
            <NewDemandForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const locationInput = screen.getByLabelText(/Search Location/i);
        userEvent.type(locationInput, 'Cen');

        await waitFor(() =>
            expect(apiClient.get).toHaveBeenCalledWith('/locations/ids?search=Cen'));
        await waitFor(() =>
            expect(screen.getByText('Central Warehouse - Bangalore')).toBeInTheDocument()
        );
    });

    test('fetches item options based on user input', async () => {
        const mockItemData = [{ "itemId": "000001", "itemDescription": "Red T-Shirt" }];
        apiClient.get.mockResolvedValue({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });

        render(
            <NewDemandForm
                demandOptions={demandOptions}
                fetchRow={mockFetchRow}
                page={1}
                setOpenAddDialog={mockSetOpenAddDialog}
                openAddDialog={true}
            />
        );

        const itemInput = screen.getByLabelText(/Search Item/i);

        userEvent.type(itemInput, 'Red');

        await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/items/ids?search=Red'));
        await waitFor(() => expect(screen.getByText('Red T-Shirt')).toBeInTheDocument());
    });

    test('closes the dialog when Cancel is clicked', async () => {
        render(
            <NewDemandForm
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


    test('shows 404 statos error when API fails during submission', async () => {
        const mockLocationData = [{ "locationId": "01501", "locationDesc": "Central Warehouse - Bangalore" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockLocationData[0])] } },
        });
        const mockItemData = [{ "itemId": "000001", "itemDescription": "Red T-Shirt" }];
        apiClient.get.mockResolvedValueOnce({
            data: { payload: { content: [JSON.stringify(mockItemData[0])] } },
        });
        apiClient.post.mockResolvedValueOnce({
            data: { status: 404, message: "not found" },
        });

        render(
            <NewDemandForm
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


        // Simulate filling the quantity field
        fireEvent.change(screen.getByLabelText(/Quantity/i), { target: { value: 10 } });

        // Submit the form
        const saveButton = screen.getByText(/Save/i);
        userEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('not found');
        });
    });


});