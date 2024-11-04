import React from 'react';
import { render, screen, fireEvent, waitFor, act } from '@testing-library/react';
import { Provider } from 'react-redux';
import configureStore from 'redux-mock-store';
import thunk from 'redux-thunk';
import ProfileForm from './index';
import apiClient from '../../components/baseUrl';
import { errorToast, successToast } from '../../components/Toast';
import { setUsername } from '../../store/usernameSlice';
import { Face } from '@mui/icons-material';

jest.mock('../../components/baseUrl');
jest.mock('../../components/Toast', () => ({
    errorToast: jest.fn(),
    successToast: jest.fn(),
}));
jest.mock('react-redux', () => {
    const actualReactRedux = jest.requireActual('react-redux');
    return {
        ...actualReactRedux,
        useDispatch: jest.fn(),
    };
});

const mockStore = configureStore([]);
const store = mockStore({});

describe('ProfileForm Component', () => {
    let store;

    beforeEach(async () => {
        store = mockStore({
            username: '',
        });

    });

    it('renders the profile form correctly', async () => {
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        expect(screen.getByRole('heading', { name: /Profile/i })).toBeInTheDocument();
        expect(screen.getByLabelText(/Full Name/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/Email/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /Update Profile/i })).toBeInTheDocument();
    });



    it('fetches and displays user profile data', async () => {
        apiClient.get.mockResolvedValue({
            data: {
                success: true,
                payload: {
                    fullName: 'John Doe',
                    email: 'john.doe@example.com',
                },
            },
        });
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        await waitFor(() => expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument());

        await waitFor(() => expect(screen.getByDisplayValue(/John Doe/i)).toBeInTheDocument());
        await waitFor(() => expect(screen.getByDisplayValue(/john.doe@example.com/i)).toBeInTheDocument());
    });

    it('shows error toast if profile data fails to load', async () => {
        apiClient.get.mockResolvedValue({
            data: {
                success: false,
            },
        });
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        await waitFor(() => expect(screen.queryByText(/Loading.../i)).toBeInTheDocument());

        expect(errorToast).toHaveBeenCalledWith('Failed to load profile data.');
    });

    it('updates profile data successfully', async () => {
        apiClient.get.mockResolvedValueOnce({
            data: {
                success: true,
                payload: {
                    fullName: 'John Doe',
                    email: 'john.doe@example.com',
                },
            },
        });
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        await waitFor(() => expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument());

        // Update form fields
        fireEvent.change(screen.getByLabelText(/Full Name/i), { target: { value: 'Jane Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'jane.doe@example.com' } });

        apiClient.put.mockResolvedValue({
            data: {
                "success": true,
                "timestamp": "2024-10-17T12:31:19.291194838",
                "message": "User Found.",
                "payload": {
                    "id": "6703c7fb45c611516e6a55f3",
                    "fullName": "G Balaji",
                    "email": "guram.balaji@nextuple.com",
                    "verificationToken": "6d20c2d8-badf-4057-aea5-405bfed8a75f",
                    "verified": true
                }
            }
        });

        fireEvent.click(screen.getByRole('button', { name: /Update Profile/i }));

        await waitFor(() => expect(successToast).toHaveBeenCalledWith('Profile updated successfully!'));
    });

    it('shows error toast if profile update fails', async () => {
        apiClient.get.mockResolvedValue({
            data: {
                success: true,
                payload: {
                    fullName: 'John Doe',
                    email: 'john.doe@example.com',
                },
            },
        });

        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            );
        });

        await waitFor(() => expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/Full Name/i), { target: { value: 'Jane Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'jane.doe@example.com' } });

        // Mock the profile update failure
        apiClient.put.mockRejectedValueOnce(new Error('Error fetching profile data.'));

        fireEvent.click(screen.getByRole('button', { name: /Update Profile/i }));

        // Wait for error toast to be called
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Error updating profile.'));
    });


    it('shows error toast if passwords do not match', async () => {
        apiClient.get.mockResolvedValue({
            data: {
                success: true,
                payload: {
                    fullName: 'John Doe',
                    email: 'john.doe@example.com',
                },
            },
        });
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        await waitFor(() => expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument());

        fireEvent.change(screen.getByLabelText(/Full Name/i), { target: { value: 'Jane Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'jane.doe@example.com' } });
        fireEvent.click(screen.getByLabelText(/Change Password\?/i));
        fireEvent.change(screen.getByLabelText(/New Password/i), { target: { value: 'newpassword' } });
        fireEvent.change(screen.getByLabelText(/Confirm Password/i), { target: { value: 'wrongpassword' } });

        fireEvent.click(screen.getByRole('button', { name: /Update Profile/i }));

        expect(errorToast).toHaveBeenCalledWith('Passwords do not match.');
    });

    it('displays loading initially', () => {
        render(
            <Provider store={store}>
                <ProfileForm />
            </Provider>
        );

        expect(screen.getByText(/Loading.../i)).toBeInTheDocument();
    });

    it('updates profile data error 404', async () => {
        apiClient.get.mockResolvedValueOnce({
            data: {
                success: true,
                payload: {
                    fullName: 'John Doe',
                    email: 'john.doe@example.com',
                },
            },
        });
        await act(async () => {
            render(
                <Provider store={store}>
                    <ProfileForm />
                </Provider>
            )
        });
        await waitFor(() => expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument());

        // Update form fields
        fireEvent.change(screen.getByLabelText(/Full Name/i), { target: { value: 'Jane Doe' } });
        fireEvent.change(screen.getByLabelText(/Email/i), { target: { value: 'jane.doe@example.com' } });

        fireEvent.click(screen.getByRole('button', { name: /Update Profile/i }));
        apiClient.put.mockResolvedValue({
            data: {
                status: 404,
                success: false,
                payload: "Error updating profile.",
            },
        });
        await waitFor(() => expect(errorToast).toHaveBeenCalledWith('Error updating profile.'));
    });

});
