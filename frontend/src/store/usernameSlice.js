import { createSlice } from '@reduxjs/toolkit';

const initialState = ''; // Default username is an empty string

const usernameSlice = createSlice({
  name: 'username',
  initialState,
  reducers: {
    setUsername: (state, action) => action.payload, // Update the username
    clearUsername: () => '', // Reset the username
  },
});

// Export actions
export const { setUsername, clearUsername } = usernameSlice.actions;

// Export reducer
export default usernameSlice.reducer;

// Export selector for accessing the username
export const selectUsername = (state) => state.username;
