import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import LoginForm from '../../components/LoginForm'; // Adjust the import based on your file structure

const mockStore = configureStore([]);

describe('LoginForm', () => {
    const mockToggleMode = jest.fn();
    const store = mockStore({
        // Mock your Redux state here if necessary
    });

    test('renders login form by default', () => {
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode="login" toggleMode={mockToggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Assert that login form is shown
        expect(screen.getByText(/log in/i)).toBeInTheDocument();

    });
});
