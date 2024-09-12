import axios from 'axios';
import store from '../store/store'; // assuming you have access to the Redux store
import { selectToken } from '../store/authSlice'; // assuming you have this selector

const apiClient = axios.create({
  baseURL: 'http://localhost:8081/',
});

// Add a request interceptor to dynamically set the Authorization header
apiClient.interceptors.request.use((config) => {
  const state = store.getState(); // access the Redux store
  const token = selectToken(state); // get the token from the store

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

export default apiClient;
