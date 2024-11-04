import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import EditForm from '../EditFormLocation'; // Adjust the path as needed
import { errorToast, successToast } from '../../../components/Toast';
import apiClient from '../../../components/baseUrl';

// Mocking the toast notifications and apiClient
jest.mock('../../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));

jest.mock('../../../components/baseUrl');

describe('EditForm', () => {
    const mockFetchRow = jest.fn();
    const mockSetOpenEditDialog = jest.fn();
    const mockSetEditData = jest.fn();

    const locationTypeOptions = ['WAREHOUSE', 'STORE', 'DISTRIBUTION_CENTER'];

    const editData = {
        locationId: 'loc123',
        locationDesc: 'Sample Location',
        locationType: 'WAREHOUSE',
        addressLine1: '123 Main St',
        city: 'Sample City',
        state: 'Sample State',
        country: 'Sample Country',
        pinCode: '123456',
        pickupAllowed: true,
        shippingAllowed: false,
        deliveryAllowed: true,
    };

    beforeEach(() => {
        jest.clearAllMocks();
    });

    test('renders the form with prefilled values', () => {
        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={editData}
            />
        );

        expect(screen.getByDisplayValue(editData.locationId)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.locationDesc)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.locationType)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.addressLine1)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.city)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.state)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.country)).toBeInTheDocument();
        expect(screen.getByDisplayValue(editData.pinCode)).toBeInTheDocument();

        const pickupCheckbox = screen.getByLabelText('Pickup Allowed');
        const shippingCheckbox = screen.getByLabelText('Shipping Allowed');
        const deliveryCheckbox = screen.getByLabelText('Delivery Allowed');

        expect(pickupCheckbox).toBeChecked();
        expect(shippingCheckbox).not.toBeChecked();
        expect(deliveryCheckbox).toBeChecked();
    });

    test('triggers onChange when editing description field', () => {
        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={editData}
            />
        );

        const descInput = screen.getByLabelText('Description');
        fireEvent.change(descInput, { target: { value: 'New Location Description' } });

        expect(mockSetEditData).toHaveBeenCalledWith({ ...editData, locationDesc: 'New Location Description' });
    });

    test('validates required fields and shows error toast if any are missing', async () => {
        const incompleteEditData = { ...editData, locationDesc: '' };

        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={incompleteEditData}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Please fill in all required fields.');
        });
    });

    test('calls API and shows success toast on successful save', async () => {
        apiClient.patch.mockResolvedValueOnce({
            data: { success: true, status: 200 },
        });

        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={editData}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(successToast).toHaveBeenCalledWith('Location updated successfully!');
            expect(mockFetchRow).toHaveBeenCalledWith(1);
            expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false);
        });
    });

    test('shows error toast on failed API call', async () => {
        apiClient.patch.mockRejectedValueOnce(new Error('API error'));

        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={editData}
            />
        );

        const saveButton = screen.getByRole('button', { name: /Save/i });
        fireEvent.click(saveButton);

        await waitFor(() => {
            expect(errorToast).toHaveBeenCalledWith('Failed to update location');
            expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false);
        });
    });

    test('closes the dialog when Cancel button is clicked', async () => {
        render(
            <EditForm
                openEditDialog={true}
                setOpenEditDialog={mockSetOpenEditDialog}
                locationTypeOptions={locationTypeOptions}
                fetchRow={mockFetchRow}
                page={1}
                setEditData={mockSetEditData}
                editData={editData}
            />
        );

        const cancelButton = screen.getByRole('button', { name: /Cancel/i });
        fireEvent.click(cancelButton);

        await waitFor(() => {
            expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false);
        });
    });
});


describe('EditForm Component', () => {
    const mockSetOpenEditDialog = jest.fn();
    const mockSetEditData = jest.fn();
    const mockFetchRow = jest.fn();
    const setEditData = jest.fn();

    const mockEditData = {
      locationId: '123',
      locationDesc: 'Test Location',
      locationType: 'WAREHOUSE',
      addressLine1: '123 Main St',
      city: 'New York',
      state: 'NY',
      country: 'USA',
      pinCode: '10001',
      pickupAllowed: true,
      shippingAllowed: false,
      deliveryAllowed: false,
    };
  
    const locationTypeOptions = ['WAREHOUSE', 'STORE', 'DISTRIBUTION_CENTER'];
  
    const renderComponent = async(editData = mockEditData) =>
    await act(()=> render(
        <EditForm
          openEditDialog={true}
          setOpenEditDialog={mockSetOpenEditDialog}
          locationTypeOptions={locationTypeOptions}
          fetchRow={mockFetchRow}
          page={1}
          setEditData={mockSetEditData}
          editData={editData}
        />
      ));
  
    afterEach(() => {
      jest.clearAllMocks();
    });
  
    test('should render form fields with correct values', () => {
      renderComponent();
  
      // Check if fields are rendered with correct initial values
      expect(screen.getByLabelText(/Description/i)).toHaveValue('Test Location');
      expect(screen.getByLabelText(/Address Line 1/i)).toHaveValue('123 Main St');
      expect(screen.getByLabelText(/City/i)).toHaveValue('New York');
      expect(screen.getByLabelText(/State/i)).toHaveValue('NY');
      expect(screen.getByLabelText(/Country/i)).toHaveValue('USA');
      expect(screen.getByLabelText(/Pin Code/i)).toHaveValue('10001');
      expect(screen.getByLabelText(/Pickup Allowed/i).checked).toBe(true);
      expect(screen.getByLabelText(/Shipping Allowed/i).checked).toBe(false);
      expect(screen.getByLabelText(/Delivery Allowed/i).checked).toBe(false);
    });
  
    test('should handle input changes', () => {
      renderComponent();
  
      // Simulate input changes
      fireEvent.change(screen.getByLabelText(/Description/i), { target: { value: 'Updated Location' } });
      fireEvent.change(screen.getByLabelText(/Address Line 1/i), { target: { value: '456 Elm St' } });
      fireEvent.change(screen.getByLabelText(/City/i), { target: { value: 'Los Angeles' } });
  
      // Ensure onChange handlers are called correctly
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        locationDesc: 'Updated Location',
      });
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        addressLine1: '456 Elm St',
      });
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        city: 'Los Angeles',
      });
    });
  
    test('should call API and handle success on save', async () => {
      apiClient.patch.mockResolvedValue({
        data: { success: true },
      });
  
      renderComponent();
  
      fireEvent.click(screen.getByText(/Save/i));
  
      await waitFor(() => {
        expect(apiClient.patch).toHaveBeenCalledWith('/locations/123', mockEditData);
        expect(successToast).toHaveBeenCalledWith('Location updated successfully!');
        expect(mockFetchRow).toHaveBeenCalledWith(1); // Fetch updated data for page 1
      });
    });
  
    test('should handle missing required fields', () => {
      const incompleteEditData = { ...mockEditData, locationDesc: '' };
      renderComponent(incompleteEditData);
  
      fireEvent.click(screen.getByText(/Save/i));
  
      expect(errorToast).toHaveBeenCalledWith('Please fill in all required fields.');
      expect(apiClient.patch).not.toHaveBeenCalled();
    });
  
    test('should handle API failure', async () => {
      apiClient.patch.mockRejectedValue(new Error('API Error'));
  
      renderComponent();
  
      fireEvent.click(screen.getByText(/Save/i));
  
      await waitFor(() => {
        expect(errorToast).toHaveBeenCalledWith('Failed to update location');
        expect(apiClient.patch).toHaveBeenCalled();
      });
    });
  
    test('should handle cancel button click', () => {
      renderComponent();
  
      fireEvent.click(screen.getByText(/Cancel/i));
  
      expect(mockSetOpenEditDialog).toHaveBeenCalledWith(false);
    });
  
    test('should handle 404 error from API', async () => {
      apiClient.patch.mockResolvedValue({
        data: { status: 404, message: 'Location not found' },
      });
  
      renderComponent();
  
      fireEvent.click(screen.getByText(/Save/i));
  
      await waitFor(() => {
        expect(errorToast).toHaveBeenCalledWith('Location not found');
        expect(apiClient.patch).toHaveBeenCalled();
      });
    });
  
    test('should handle fulfillment option changes', () => {
      renderComponent();
  
      // Toggle checkboxes
      fireEvent.click(screen.getByLabelText(/Pickup Allowed/i));
      fireEvent.click(screen.getByLabelText(/Shipping Allowed/i));
      fireEvent.click(screen.getByLabelText(/Delivery Allowed/i));
  
      // Verify state updates
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        pickupAllowed: false,
      });
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        shippingAllowed: true,
      });
      expect(mockSetEditData).toHaveBeenCalledWith({
        ...mockEditData,
        deliveryAllowed: true,
      });
    });

    test('should update addressLine2 on change', () => {
        const { rerender } = render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ addressLine2: '' }} // Initial state
          />
        );
    
        const addressLine2Input = screen.getByLabelText(/address line 2/i);
        fireEvent.change(addressLine2Input, { target: { value: 'New Address Line 2' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          addressLine2: 'New Address Line 2',
        });
      });
    
      test('should update addressLine3 on change', () => {
        const { rerender } = render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ addressLine3: '' }} // Initial state
          />
        );
    
        const addressLine3Input = screen.getByLabelText(/address line 3/i);
        fireEvent.change(addressLine3Input, { target: { value: 'New Address Line 3' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          addressLine3: 'New Address Line 3',
        });
      });
    
      test('should update city on change', () => {
        const { rerender } = render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ city: '' }} // Initial state
          />
        );
    
        const cityInput = screen.getByLabelText(/city/i);
        fireEvent.change(cityInput, { target: { value: 'New City' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          city: 'New City',
        });
      });

      test('should update state on change', () => {
        render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ state: '' }} // Initial state
          />
        );
    
        const stateInput = screen.getByLabelText(/state/i);
        fireEvent.change(stateInput, { target: { value: 'New State' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          state: 'New State',
        });
      });
    
      test('should update country on change', () => {
        render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ country: '' }} // Initial state
          />
        );
    
        const countryInput = screen.getByLabelText(/country/i);
        fireEvent.change(countryInput, { target: { value: 'New Country' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          country: 'New Country',
        });
      });
    
      test('should update pinCode on change', () => {
        render(
          <EditForm
            openEditDialog={true}
            setOpenEditDialog={jest.fn()}
            locationTypeOptions={[]}
            fetchRow={mockFetchRow}
            page={1}
            setEditData={setEditData}
            editData={{ pinCode: '' }} // Initial state
          />
        );
    
        const pinCodeInput = screen.getByLabelText(/pin code/i);
        fireEvent.change(pinCodeInput, { target: { value: '123456' } });
        
        // Check if setEditData was called with the correct argument
        expect(setEditData).toHaveBeenCalledWith({
          pinCode: '123456',
        });
      });
      
  });