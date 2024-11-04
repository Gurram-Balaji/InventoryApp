import React from 'react';
import { render, fireEvent, waitFor, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { MemoryRouter } from 'react-router-dom';
import configureStore from 'redux-mock-store';
import Sidebar from './index';
import { logout, clearUsername } from '../store/authSlice';
import { setUsername } from '../store/usernameSlice';
import apiClient from '../components/baseUrl';

// Mock axios or your API client
jest.mock('../components/baseUrl');

const mockStore = configureStore([]);
jest.mock('../components/baseUrl', () => ({
  get: jest.fn(),
}));


describe('Sidebar Component', () => {
  let store;

  beforeEach(() => {
    store = mockStore({
      auth: { token: 'mockToken' },
      username: '', // Initially empty
    });

    store.clearActions();
  });

  test('renders sidebar and buttons correctly', () => {
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });
    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    expect(getByText(/Home/i)).toBeInTheDocument();
    expect(getByText(/Items/i)).toBeInTheDocument();
    expect(getByText(/Locations/i)).toBeInTheDocument();
    expect(getByText(/Supply/i)).toBeInTheDocument();
    expect(getByText(/Demand/i)).toBeInTheDocument();
    expect(getByText(/Threshold/i)).toBeInTheDocument();
    expect(getByText(/Availability/i)).toBeInTheDocument();
    expect(getByText(/BarChat/i)).toBeInTheDocument();
  });


  test('should dispatch logout and clearUsername actions',async () => {
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });

    const { container } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );
  
    // Find the logout button using the class name
    const logoutButton = container.querySelector('.sc-dstKZu.hNqgaR'); // Use the exact class name as it appears
  
    fireEvent.click(logoutButton);
    
      const actions = store.getActions();
      waitFor(()=>expect(actions).toContainEqual(logout()));
      waitFor(()=>expect(actions).toContainEqual(clearUsername()));
      waitFor(()=>expect(navigate).toHaveBeenCalledWith('/')); // Assert navigation to home page
  
  });
  

  

  test('logs out and navigates to login page when logout is clicked', async () => {
    // Mock the response for the API call
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });

    const { getByRole } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    const logoutButton = getByRole('button'); // Ensure the button name matches your actual component
    fireEvent.click(logoutButton);

    const actions = store.getActions();
    waitFor(()=>expect(actions).toContainEqual(logout()));
    waitFor(()=>expect(actions).toContainEqual(clearUsername()));
    waitFor(()=>expect(window.location.pathname).toBe('/')); // Assert navigation to login page
  });

  test('displays username after API call', async () => {
    // Mock API response
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });

    store = mockStore({
      auth: { token: 'mockToken' },
      username: '', // Initially empty
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    await waitFor(() => expect(apiClient.get).toHaveBeenCalledWith('/auth/name'));

    // Simulate the effect of setting username
    store.dispatch(setUsername('JD')); // Assuming initials 'JD' after processing

    waitFor(() => expect(getByText(/JD/i)).toBeInTheDocument()); // Check if username is displayed
  });

  test('toggles profile details when profile picture is clicked', async () => {
    // Mock the response for the API call
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });

    const { getByAltText, findByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    // Simulate a click on the profile picture
    const profileImg = getByAltText('Profile'); // Ensure this alt text matches your actual component
    fireEvent.click(profileImg);

    // Check if profile details are displayed (adjust based on what you expect to be shown)
    const profileDetails = await findByText(/view profile/i); // Change this to whatever text you expect
    expect(profileDetails).toBeVisible(); // Verify that profile details are visible
  });

  test('handles 403 error: clears username and logs out user', async () => {
    // Mock the response for a 403 error
    apiClient.get.mockRejectedValueOnce({
      response: { status: 403, data: { message: 'Forbidden' } },
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    // Simulate the effect that triggers the API call (you may need to trigger this appropriately)
    // For example, you might need to trigger a useEffect or button click here

    // Wait for any promises to resolve
    await new Promise((resolve) => setTimeout(resolve, 0));

   waitFor(()=> expect(dispatch).toHaveBeenCalledWith(clearUsername()));
    waitFor(()=> expect(dispatch).toHaveBeenCalledWith(logout()));
    // Assert that you are navigating to the login page
    waitFor(()=> expect(window.location.pathname).toBe('/')); // Adjust based on your implementation
  });

  test('handles other errors: logs error message', async () => {
    // Mock the response for a generic error
    const consoleErrorMock = jest.spyOn(console, 'error').mockImplementation(() => {});
    apiClient.get.mockRejectedValueOnce({
      response: { status: 500, data: { message: 'Internal Server Error' } },
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    // Simulate the effect that triggers the API call (you may need to trigger this appropriately)
    
    // Wait for any promises to resolve
    await new Promise((resolve) => setTimeout(resolve, 0));

    expect(consoleErrorMock).toHaveBeenCalledWith('An error occurred:', 'Internal Server Error');
    
    consoleErrorMock.mockRestore(); // Restore the original  console.error
  });

  test('navigates to the correct route when navigation items are clicked', () => {
    
    // Mock the response for the API call
    apiClient.get.mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
  
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    })
    .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    }) .mockResolvedValueOnce({
      data: { payload: 'Test User' },
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/dashboard']}>
          <Sidebar />
        </MemoryRouter>
      </Provider>
    );

    // Click on "Items" navigation link
    fireEvent.click(getByText(/Items/i));
    waitFor(()=>expect(window.location.pathname).toBe('/items')); // Assert navigation occurred

    // Click on "Locations" navigation link
    fireEvent.click(getByText(/Locations/i));
    waitFor(()=>expect(window.location.pathname).toBe('/location')); // Assert navigation occurred

    // Continue for other links as needed
    fireEvent.click(getByText(/Supply/i));
    waitFor(()=>expect(window.location.pathname).toBe('/supply'));

    fireEvent.click(getByText(/Demand/i));
    waitFor(()=>expect(window.location.pathname).toBe('/demand'));

    fireEvent.click(getByText(/Threshold/i));
    waitFor(()=>expect(window.location.pathname).toBe('/threshold'));

    fireEvent.click(getByText(/Availability/i));
    waitFor(()=>expect(window.location.pathname).toBe('/available'));

    fireEvent.click(getByText(/BarChat/i));
    waitFor(()=>expect(window.location.pathname).toBe('/stackedBarchart'));

    // Click on "Items" navigation link
    fireEvent.click(getByText(/home/i));
    waitFor(()=>expect(window.location.pathname).toBe('/dashboard')); 

  });
  
});
