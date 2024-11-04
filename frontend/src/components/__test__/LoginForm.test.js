import React from 'react';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import LoginForm from '../LoginForm';
import user from '@testing-library/user-event';
import { errorToast, successToast } from '../Toast';
import apiClient from '../baseUrl';

const mockStore = configureStore([]);
const store = mockStore({});
jest.mock('../Toast');
jest.mock('../baseUrl', () => ({
    post: jest.fn(),
}));

jest.mock('../../store/usernameSlice', () => ({
    loginSuccess: jest.fn()
}));
afterEach(() => {
    jest.clearAllMocks();
});
describe('LoginForm - handleLogin', () => {

    test("Handle login with empty email and password", async () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        const submitBtn = screen.getByRole('button', { name: /log in/i });
        user.click(submitBtn);

        await waitFor(() => expect(errorToast).toHaveBeenCalledTimes(1));
        expect(errorToast).toHaveBeenCalledWith('Please enter both email and password.');
    });

    test("Handle signup with empty fields", async () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        await waitFor(() => expect(errorToast).toHaveBeenCalledTimes(1));
        expect(errorToast).toHaveBeenCalledWith('Password is weak. Please include at least 8 characters, a lowercase letter, an uppercase letter, a number, a special character(@$!%*?&).');
    });

    test("Handle signup with invalid credentials", async () => {
        const toggleMode = jest.fn();
        const dispatch = jest.fn();
        const navigation = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} dispatch={dispatch} navigation={navigation} />
                </MemoryRouter>
            </Provider>
        );

        const fullNameInput = screen.getByTestId('fullname-signup-input');
        user.type(fullNameInput, 'John Doe');
        const emailInput = screen.getByTestId('email-signup-input');
        user.type(emailInput, 'invalid@email.com');
        const passwordInput = screen.getByTestId('cpassword-signup-input');
        user.type(passwordInput, 'short');
        const confirmPasswordInput = screen.getByTestId('rpassword-signup-input');
        user.type(confirmPasswordInput, 'short');

        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        await waitFor(() => expect(errorToast).toHaveBeenCalledTimes(2));
        expect(errorToast).toHaveBeenCalledWith('Password is weak. Please include at least 8 characters, a lowercase letter, an uppercase letter, a number, a special character(@$!%*?&).');
    });

    test("Find login inputs to be in the document and not disabled", () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        const input1 = screen.getByPlaceholderText('Email');
        const input2 = screen.getByPlaceholderText('Password');
        const input3 = screen.getByPlaceholderText('Your fullname');
        const input4 = screen.getByPlaceholderText('Your email');
        const input5 = screen.getByPlaceholderText('Create password');
        const input6 = screen.getByPlaceholderText('Repeat password');

        expect(input1).not.toBeDisabled();
        expect(input2).not.toBeDisabled();
        expect(input3).toBeDisabled();
        expect(input4).toBeDisabled();
        expect(input5).toBeDisabled();
        expect(input6).toBeDisabled();
    });

    test("Find signup inputs to be in the document and not disabled", () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        const input1 = screen.getByPlaceholderText('Email');
        const input2 = screen.getByPlaceholderText('Password');
        const input3 = screen.getByPlaceholderText('Your fullname');
        const input4 = screen.getByPlaceholderText('Your email');
        const input5 = screen.getByPlaceholderText('Create password');
        const input6 = screen.getByPlaceholderText('Repeat password');

        expect(input1).toBeDisabled();
        expect(input2).toBeDisabled();
        expect(input3).not.toBeDisabled();
        expect(input4).not.toBeDisabled();
        expect(input5).not.toBeDisabled();
        expect(input6).not.toBeDisabled();
    });


    test("On toggle button click, loginIn and signUp inputs to be in the document and disabled and not disabled fields", async () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        const email_login = screen.getByTestId('email-signin-input');
        user.click(email_login);
        user.keyboard("test@email.com");
        const password_login = screen.getByTestId('password-signin-input');
        user.click(password_login);
        user.keyboard("password");
        const submit_btn = screen.getByRole('button', {
            name: /log in/i
        });
        user.click(submit_btn);

        await waitFor(() => expect(submit_btn).toHaveTextContent('Loading...'), { timeout: 500 });
    });



    test('should handle successful login', async () => {
        // Mock apiClient response for successful login
        apiClient.post.mockResolvedValueOnce({
            data: {
                status: 200,
                payload: { token: 'fake-jwt-token' }
            }
        });

        const toggleMode = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Fill out form fields
        fireEvent.change(screen.getByTestId('email-signin-input'), {
            target: { value: 'test@example.com' }
        });
        fireEvent.change(screen.getByTestId('password-signin-input'), {
            target: { value: 'password123' }
        });

        // Submit the form
        fireEvent.click(screen.getByRole('button', { name: /log in/i }));

        // Wait for async operations to complete
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1), { timeout: 500 });
        expect(apiClient.post).toHaveBeenCalledWith('/auth/signin', {
            email: 'test@example.com',
            password: 'password123'
        });
    });

    test('should show error toast on invalid credentials', async () => {
        // Mock apiClient response for invalid credentials
        apiClient.post.mockResolvedValueOnce({
            data: {
                status: 404,
                message: 'Invalid credentials'
            }
        });

        const toggleMode = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );


        // Fill out form fields
        fireEvent.change(screen.getByTestId('email-signin-input'), {
            target: { value: 'test@example.com' }
        });
        fireEvent.change(screen.getByTestId('password-signin-input'), {
            target: { value: 'passwordWrong' }
        });
        // Submit the form
        fireEvent.click(screen.getByRole('button', { name: /log in/i }));

        // Wait for async operations to complete
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1));

        // Ensure the error toast was called with the correct message
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Invalid credentials'));
    });

    test('should show generic error toast on network failure', async () => {
        // Mock apiClient response for a network error
        apiClient.post.mockRejectedValueOnce(new Error('Network Error'));

        const toggleMode = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"login"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );


        // Fill out form fields
        fireEvent.change(screen.getByTestId('email-signin-input'), {
            target: { value: 'test@example.com' }
        });
        fireEvent.change(screen.getByTestId('password-signin-input'), {
            target: { value: 'passwordWrong' }
        });

        // Submit the form
        fireEvent.click(screen.getByRole('button', { name: /log in/i }));

        // Wait for async operations to complete
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1));

        // Ensure the generic error toast was called
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Something went wrong. Please try again.'));
    });

    test('should show error when fields are empty', async () => {
        const toggleMode = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Fill out form fields
        fireEvent.change(screen.getByTestId('cpassword-signup-input'), {
            target: { value: 'passWord123$' }
        });
        fireEvent.change(screen.getByTestId('rpassword-signup-input'), {
            target: { value: 'passWord123$' }
        });


        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        await waitFor(() => expect(errorToast).toHaveBeenCalledTimes(1));
        expect(errorToast).toHaveBeenCalledWith('Please fill in all fields.');
    });

    test('should show error when passwords do not match', async () => {
        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Typing into the form fields
        await user.type(screen.getByTestId('fullname-signup-input'), 'Test User');
        await user.type(screen.getByTestId('email-signup-input'), 'test@example.com');
        await user.type(screen.getByTestId('cpassword-signup-input'), 'passWord123$');  // First password
        await user.type(screen.getByTestId('rpassword-signup-input'), 'passWor#d456');  // Mismatching password

        // Clicking the sign-up button
        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        // Waiting for the errorToast call and verifying the message
        await waitFor(() => expect(errorToast).toHaveBeenCalledTimes(1));
        expect(errorToast).toHaveBeenCalledWith("Passwords do not match");
    });

    test('should handle successful signup when success is true', async () => {
        // Mock successful API response
        const mockApiResponse = {
            data: {
                status: 200,
                success: true,
                payload: 'Signup successful!',
                message: 'User created successfully.'
            },
        };
        apiClient.post.mockResolvedValueOnce(mockApiResponse);

        const toggleMode = jest.fn();
        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Fill out signup form fields
        await user.type(screen.getByTestId('fullname-signup-input'), 'Test User');
        await user.type(screen.getByTestId('email-signup-input'), 'test@example.com');
        await user.type(screen.getByTestId('cpassword-signup-input'), 'passW$ord123');
        await user.type(screen.getByTestId('rpassword-signup-input'), 'passW$ord123'); // Matching password

        // Submit the form
        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        // Wait for async operations to complete
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1));

        // Assert that the API was called with correct request data
        expect(apiClient.post).toHaveBeenCalledWith('/auth/signup', {
            fullName: 'Test User',
            email: 'test@example.com',
            password: 'passW$ord123',
        });

        // Assert that successToast and toggleMode were called
        expect(successToast).toHaveBeenCalledWith('Signup successful!');
        expect(successToast).toHaveBeenCalledWith('User created successfully.');
        expect(toggleMode).toHaveBeenCalled();
    });


    test('should handle signup error', async () => {
        // Mock API response for signup error
        apiClient.post.mockResolvedValueOnce({
            data: {
                status: 404,
                message: 'Email already in use.'
            }
        });

        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Fill out signup form fields
        await user.type(screen.getByTestId('fullname-signup-input'), 'Test User');
        await user.type(screen.getByTestId('email-signup-input'), 'test@example.com');
        await user.type(screen.getByTestId('cpassword-signup-input'), 'passW$ord123');
        await user.type(screen.getByTestId('rpassword-signup-input'), 'passW$ord123'); // Matching password

        // Submit the form
        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        // Wait for the API call and validate inputs
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1));
        expect(apiClient.post).toHaveBeenCalledWith('/auth/signup', {
            fullName: 'Test User',
            email: 'test@example.com',
            password: 'passW$ord123',
        });

        // Check error toast
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Email already in use.'));
    });

    test('should handle API failure and show error toast', async () => {
        // Mock API response to throw an error
        apiClient.post.mockRejectedValueOnce(new Error('Network Error'));

        const toggleMode = jest.fn();

        render(
            <Provider store={store}>
                <MemoryRouter>
                    <LoginForm mode={"signup"} toggleMode={toggleMode} />
                </MemoryRouter>
            </Provider>
        );

        // Fill out signup form fields
        await user.type(screen.getByTestId('fullname-signup-input'), 'Test User');
        await user.type(screen.getByTestId('email-signup-input'), 'test@example.com');
        await user.type(screen.getByTestId('cpassword-signup-input'), 'passW$ord123');
        await user.type(screen.getByTestId('rpassword-signup-input'), 'passW$ord123'); // Matching password

        // Submit the form
        const submitBtn = screen.getByRole('button', { name: /sign up/i });
        user.click(submitBtn);

        // Wait for the error to be caught and validate error toast
        await waitFor(() => expect(apiClient.post).toHaveBeenCalledTimes(1));
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Something went wrong..'));
    });



});