import axios from 'axios';
import store from '../store/store'; // access Redux store
import { selectToken, logout } from '../store/authSlice';
import { clearUsername } from '../store/usernameSlice';
import { errorToast } from '../components/Toast'; // assuming this exists for toast messages

const apiClient = axios.create({
  baseURL: 'http://localhost:8080/', // Update with your base URL
});

// Add a request interceptor to dynamically set the Authorization header
apiClient.interceptors.request.use((config) => {
  const state = store.getState();
  const token = selectToken(state);

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

// Add a response interceptor to handle errors globally
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const dispatch = store.dispatch; // access dispatch

    if (error.response) {
      // Handle 403 Forbidden (unauthorized access)
      if (error.response.status === 403) {
        errorToast("Your session has expired, please log in again.");
        dispatch(clearUsername()); // Clear the username if needed
        dispatch(logout()); // Log out the user

        // Throw the error for the component to catch and handle the navigation
        throw new Error("Session expired");
      } else {
        // Handle other errors
        errorToast(error.response.data.message || "An error occurred");
      }
    } else if (error.request) {
      errorToast("Network error, please try again later.");
    } else {
      errorToast("An unexpected error occurred");
    }

    return Promise.reject(error);
  }
);

export default apiClient;
