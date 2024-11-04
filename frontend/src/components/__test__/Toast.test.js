
import { toast } from 'react-toastify'; // Import toast from react-toastify
import { errorToast, successToast } from '../Toast'; // Adjust the import path

jest.mock('react-toastify', () => ({
  toast: {
    error: jest.fn(),
    success: jest.fn(),
  },
}));

describe('Toast Functions', () => {
  afterEach(() => {
    jest.clearAllMocks(); // Clear mock calls between tests
  });

  test('errorToast should call toast.error with the correct message and options', () => {
    const message = 'This is an error message';

    errorToast(message);

    expect(toast.error).toHaveBeenCalledWith(message, {
      autoClose: 3000,
    });
  });

  test('successToast should call toast.success with the correct message and options', () => {
    const message = 'This is a success message';

    successToast(message);

    expect(toast.success).toHaveBeenCalledWith(message, {
      autoClose: 3000,
    });
  });
});
