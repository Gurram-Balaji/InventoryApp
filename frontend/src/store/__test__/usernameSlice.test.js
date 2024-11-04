import usernameReducer, { setUsername, clearUsername } from '../usernameSlice';

describe('usernameSlice', () => {
  const initialState = ''; // Default initial state is an empty string

  // Test for initial state
  test('should handle initial state', () => {
    expect(usernameReducer(undefined, { type: 'unknown' })).toEqual(initialState);
  });

  // Test for setUsername action
  test('should handle setUsername', () => {
    const previousState = '';
    const newUsername = 'JohnDoe';
    const action = setUsername(newUsername);
    expect(usernameReducer(previousState, action)).toEqual(newUsername);
  });

  // Test for clearUsername action
  test('should handle clearUsername', () => {
    const previousState = 'JohnDoe';
    expect(usernameReducer(previousState, clearUsername())).toEqual(initialState);
  });
});
