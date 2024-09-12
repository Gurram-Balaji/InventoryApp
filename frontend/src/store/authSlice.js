import { createSlice } from '@reduxjs/toolkit';

const authSlice = createSlice({
  name: 'auth',
  initialState: {
    token: sessionStorage.getItem('token') || null, // Retrieve token from session storage on initial load
  },
  reducers: {
    loginSuccess: (state, action) => {
      state.token = action.payload;
      sessionStorage.setItem('token', action.payload); // Store token in session storage
    },
    logout: (state) => {
      state.token = null;
      sessionStorage.removeItem('token'); // Remove token from session storage
    },
  },
});

export const { loginSuccess, logout } = authSlice.actions;
export default authSlice.reducer;

export const selectToken = (state) => state.auth.token;
