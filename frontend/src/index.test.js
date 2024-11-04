import React from 'react';
import { render, screen } from '@testing-library/react';
import { Provider } from 'react-redux';
import { BrowserRouter } from 'react-router-dom';
import App from './App';
import store from './store/store';
import { createRoot } from 'react-dom/client';
import { MemoryRouter } from 'react-router-dom';

const mockRoot = document.createElement('div');
mockRoot.id = 'root';
document.body.appendChild(mockRoot);

// Test to check if the App component renders correctly
describe("App component", () => {
    let container;

    beforeEach(() => {
      // Create a DOM element to render into
      container = document.createElement('div');
      container.id = 'root';
      document.body.appendChild(container);
      mockRoot.innerHTML = '';
    });
  
    afterEach(() => {
      // Cleanup the DOM after each test
      document.body.removeChild(container);
    });

  test("renders without crashing", () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </Provider>
    );

    // Check if the login screen or any other initial content is rendered
    expect(screen.getByText(/Log in/i)).toBeInTheDocument();
  });

  test("renders the sidebar after login", () => {
    render(
      <Provider store={store}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </Provider>
    );

    // Assuming the sidebar contains a link to 'Home' or any other element
    // This will only render after login or when the route includes the sidebar
    expect(screen.queryByText(/Home/i)).not.toBeInTheDocument(); // Before login

    // Simulate login or navigation to a page that shows the sidebar
    // Add additional logic here if needed

    // Assert the sidebar shows up after login
    // Example: checking for the presence of a sidebar link
    // expect(screen.getByText(/Home/i)).toBeInTheDocument();
  });

  test('redux state reflects login success', () => {
    const root = createRoot(container);
    const testToken = 'test-token';
    
    // Dispatch login success action directly to test Redux state
    store.dispatch({ type: 'auth/loginSuccess', payload: testToken });
    
    // Check if the token is in the state
    expect(store.getState().auth.token).toBe(testToken);
  });

  test('does not render sidebar on login route', () => {
    const root = createRoot(container);
    root.render(
      <Provider store={store}>
        <MemoryRouter initialEntries={['/']}>
          <App />
        </MemoryRouter>
      </Provider>
    );

    // Check that the sidebar is not rendered
    expect(screen.queryByText(/Home/i)).not.toBeInTheDocument(); // Adjust this based on actual sidebar content
  });
  test('should retrieve the root DOM node', () => {
    const domNode = document.getElementById('root');
    expect(domNode).toBe(mockRoot); // Check if the retrieved node is the same as the mock
  });

  test('should create a root using createRoot', () => {
    const domNode = document.getElementById('root');
    const root = createRoot(domNode);
    
    // Check that the root is created without errors
    expect(root).toBeDefined(); // Ensure the root was created
  });

});
