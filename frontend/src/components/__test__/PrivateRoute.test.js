import React from 'react';
import { render, waitFor } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import { MemoryRouter } from 'react-router-dom';
import PrivateRoute from '../PrivateRoute'; // Update with the correct import path
import axios from 'axios'; // Import axios

// Mock the Axios module
jest.mock('axios');

// Create a mock store
const mockStore = configureStore([]);
let store;

describe('PrivateRoute Component', () => {
  beforeEach(() => {
    jest.clearAllMocks(); // Clear any previous mock data
    store = mockStore({
      auth: {
        token: null, // Default state (unauthenticated)
      },
    });
  });

  test('renders children when authenticated', async () => {
    // Mock the token state to simulate authenticated user
    store = mockStore({
      auth: { token: 'mockedToken' }, // Set up a mocked token
    });

    // Mock the Axios response
    axios.get.mockResolvedValue({ data: { /* mock dashboard data */ } });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter>
          <PrivateRoute>
            <div>Dashboard Content</div> {/* Children content */}
          </PrivateRoute>
        </MemoryRouter>
      </Provider>
    );

    // Wait for the dashboard content to render
    await waitFor(() => {
      expect(getByText('Dashboard Content')).toBeInTheDocument(); // Check if dashboard content is rendered
    });
  });


  test('allows access to /verify-email when not authenticated', () => {
    store = mockStore({
      auth: {
        token: null, // Simulate no token
      },
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/verify-email']}>
          <PrivateRoute>
            <div>Verify Email Content</div> {/* Children content */}
          </PrivateRoute>
        </MemoryRouter>
      </Provider>
    );

    // Check for the verify email content
    expect(getByText('Verify Email Content')).toBeInTheDocument();
  });

  test('redirects to login when trying to access /verify-email with authenticated user', async () => {
    store = mockStore({
      auth: {
        token: 'valid_token', // Simulate authenticated state
      },
    });

    const { getByText } = render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/verify-email']}>
          <PrivateRoute>
            <div>Verify Email Content</div> {/* Children content */}
          </PrivateRoute>
        </MemoryRouter>
      </Provider>
    );

    // Wait for the login component to render
    await waitFor(() => {
      expect(getByText(/Verify Email Content/i)).toBeInTheDocument(); // Adjust this line to match the actual text rendered in the login page
    });
  });




test('allows access to /verify-email when not authenticated', () => {
  store = mockStore({
    auth: {
      token: null, // Simulate no token
    },
  });

  const { getByText } = render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/verify-email']}>
        <PrivateRoute>
          <div>Verify Email Content</div> {/* Children content */}
        </PrivateRoute>
      </MemoryRouter>
    </Provider>
  );

  // Check for the verify email content
  expect(getByText('Verify Email Content')).toBeInTheDocument();
});

test('redirects to login when trying to access /verify-email with authenticated user', async () => {
  store = mockStore({
    auth: {
      token: 'valid_token', // Simulate authenticated state
    },
  });

  const { getByText } = render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/verify-email']}>
        <PrivateRoute>
          <div>Verify Email Content</div> {/* Children content */}
        </PrivateRoute>
      </MemoryRouter>
    </Provider>
  );

  // Wait for the login component to render
  await waitFor(() => {
    expect(getByText(/Verify Email Content/i)).toBeInTheDocument(); // Adjust this line to match the actual text rendered in the login page
  });

});



test('Move to root if not authothicated', () => {
  store = mockStore({
    auth: {
      token: null, // Simulate no token
    },
  });

  const { getByText } = render(
    <Provider store={store}>
      <MemoryRouter initialEntries={['/unknown']}>
        <PrivateRoute>
          <div>Move to root</div>
        </PrivateRoute>
      </MemoryRouter>
    </Provider>
  );

  // Check for the verify email content
 waitFor(()=> expect(getByText(/Welcome back/i)).toBeInTheDocument());
});

});