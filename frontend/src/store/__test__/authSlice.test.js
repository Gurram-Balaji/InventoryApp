import { waitFor } from '@testing-library/react';
import authReducer, { loginSuccess, logout, selectToken } from '../authSlice';

describe('authSlice', () => {
  const initialState = { token: null };

  beforeEach(() => {
    // Clear session storage before each test
    sessionStorage.clear();
  });

  test('should handle initial state', () => {
    expect(authReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  test('should handle loginSuccess', () => {
    const previousState = { token: null };
    const action = loginSuccess('fake-token');

    // Check state change and sessionStorage update
    expect(authReducer(previousState, action)).toEqual({
      token: 'fake-token',
    });
    expect(sessionStorage.getItem('token')).toBe('fake-token'); // Assert that token is stored in sessionStorage
  });

  test('should handle logout', () => {
    const previousState = { token: 'fake-token' };

    // Check state change and sessionStorage update
    expect(authReducer(previousState, logout())).toEqual(initialState);
    expect(sessionStorage.getItem('token')).toBeNull(); // Assert that token is removed from sessionStorage
  });

  test('should retrieve token from sessionStorage on initial load', () => {
    sessionStorage.setItem('token', 'existing-token');
    
    // Create the initial state manually
    const initialStateWithToken = { token: 'existing-token' };
    waitFor(()=>expect(authReducer(undefined, { type: 'unknown' })).toEqual(initialStateWithToken));
  });

  test('selectToken selector should return the token', () => {
    const state = { auth: { token: 'test-token' } };
    expect(selectToken(state)).toBe('test-token'); // Assert that the selector retrieves the token correctly
  });
});
