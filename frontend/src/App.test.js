import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { ToastContainer } from 'react-toastify';
import App from './App';
import store from './store/store'; // Assuming your store is set up correctly

// Mock components for testing purposes
jest.mock('./screens/Home', () => () => <div>Home Component</div>);
jest.mock('./screens/items', () => () => <div>Items Component</div>);
jest.mock('./screens/supply', () => () => <div>Supply Component</div>);
jest.mock('./screens/location', () => () => <div>Location Component</div>);
jest.mock('./screens/threshold', () => () => <div>Threshold Component</div>);
jest.mock('./screens/demand', () => () => <div>Demand Component</div>);
jest.mock('./screens/availability', () => () => <div>Availability Component</div>);
jest.mock('./screens/profile', () => () => <div>Profile Component</div>);
jest.mock('./screens/stackedBarChat', () => () => <div>Stacked Bar Chart Component</div>);
jest.mock('./screens/login/LoginPage', () => () => <div>Login Page</div>);
jest.mock('./screens/email/VerifyEmail', () => () => <div>Verify Email Page</div>);
jest.mock('./components/PrivateRoute', () => ({ children }) => <div>{children}</div>);
jest.mock('./sidebar', () => () => <div>Sidebar Component</div>);

describe('App Component', () => {
  test('renders LoginComponent on root path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Verify the login page is displayed
    expect(screen.getByText('Login Page')).toBeInTheDocument();
    // Sidebar should not be displayed
    expect(screen.queryByText('Sidebar Component')).not.toBeInTheDocument();
  });

  test('renders VerifyEmail component on /verify-email path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/verify-email']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Verify the verify email page is displayed
    expect(screen.getByText('Verify Email Page')).toBeInTheDocument();
    // Sidebar should not be displayed
    expect(screen.queryByText('Sidebar Component')).not.toBeInTheDocument();
  });

  test('renders Sidebar and Home component on /dashboard path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/dashboard']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Sidebar should be displayed
    expect(screen.getByText('Sidebar Component')).toBeInTheDocument();
    // Home page should be displayed
    expect(screen.getByText('Home Component')).toBeInTheDocument();
  });

  test('renders Sidebar and Items component on /items path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/items']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Sidebar should be displayed
    expect(screen.getByText('Sidebar Component')).toBeInTheDocument();
    // Items page should be displayed
    expect(screen.getByText('Items Component')).toBeInTheDocument();
  });

  test('renders Sidebar and Supply component on /supply path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/supply']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Sidebar should be displayed
    expect(screen.getByText('Sidebar Component')).toBeInTheDocument();
    // Supply page should be displayed
    expect(screen.getByText('Supply Component')).toBeInTheDocument();
  });

  test('renders Sidebar and StackedBarChart component on /stackedBarchart path', () => {
    render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/stackedBarchart']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Sidebar should be displayed
    expect(screen.getByText('Sidebar Component')).toBeInTheDocument();
    // Stacked Bar Chart page should be displayed
    expect(screen.getByText('Stacked Bar Chart Component')).toBeInTheDocument();
  });

  test('renders ToastContainer', () => {
    render(
      <Provider store={store}>
        <MemoryRouter>
          <App />
        </MemoryRouter>
      </Provider>
    );
  });
});
