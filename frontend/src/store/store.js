
import { configureStore } from '@reduxjs/toolkit';
import authReducer from './authSlice';
import usernameReducer from './usernameSlice';


const store = configureStore({
  reducer: {
    auth: authReducer,
    username: usernameReducer, 
  },
});

export default store;
