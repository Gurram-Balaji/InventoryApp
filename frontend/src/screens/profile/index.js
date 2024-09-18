import React, { useEffect, useState } from 'react';
import apiClient from '../../components/baseUrl';
import { errorToast, successToast } from '../../components/Toast';
import { useDispatch} from 'react-redux';
import { setUsername} from '../../store/usernameSlice';


const ProfileForm = () => {
  // Initialize state for form fields
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: ''
  });

  const [loading, setLoading] = useState(true);
  const dispatch = useDispatch();
  const [showPasswordFields, setShowPasswordFields] = useState(false);

  // Fetch profile data on component mount
  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const response = await apiClient.get('/auth/profile');
        if (response.data.success) {
          const { fullName, email } = response.data.payload;
          setFormData({
            fullName,
            email,
            password: '', 
            confirmPassword: ''
          });
          setLoading(false);
        } else {
          errorToast('Failed to load profile data.');
        }
      } catch (error) {
        errorToast('Error fetching profile data.');
        setLoading(false);
      }
    };

    fetchProfile();
  }, []);

  // Handle input change
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value
    });
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();

    if (formData.password !== formData.confirmPassword) {
      errorToast('Passwords do not match.');
      return;
    }

    try {
      // Send update request to the backend API
      const response = await apiClient.put('/auth/profile', formData);
      if (response.data.status === 404) {
        errorToast(response.data.message);
      } else if (response.data.success) {
        successToast('Profile updated successfully!');
        const { fullName, email } = response.data.payload;
        const username = fullName.match(/\b(\w)/g).join('');
        dispatch(setUsername(username)); // Set the initials as the username
        setFormData({
          fullName,
          email,
          password: '', 
          confirmPassword: ''
        });
      }
    } catch (error) {
      errorToast('Error updating profile.');
    }
  };

  const handleCheckboxChange = (e) => {
    setShowPasswordFields(e.target.checked);
  };

  return (
    <div>
      <style>
        {`
          * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: 'Poppins', sans-serif;
          }

          .profile-form {
            background-color: white;
            padding: 40px;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
            max-width: 400px;
            width: 100%;
          }


          .form-group {
            margin-bottom: 15px;
          }

          .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            color: #555;
          }

          .form-group input {
            width: 100%;
            padding: 12px;
            border: 1px solid #ccc;
            border-radius: 5px;
            font-size: 16px;
            transition: border-color 0.3s;
          }

          .form-group input:focus {
            border-color: #007bff;
            outline: none;
          }

          .Update-button {
            width: 100%;
            padding: 12px;
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            cursor: pointer;
            transition: background-color 0.3s;
          }

          .Update-button:hover {
            background-color: #0056b3;
          }
        `}
      </style>

      <div className="profile-form">
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <h1 style={{ fontSize: '50px' }}>Profile</h1>
          <img src="https://picsum.photos/100" alt="Profile" style={{ borderRadius: '50%', marginTop: '20px' }} />
        </div>

        {loading ? (
          <p>Loading...</p>
        ) : (
          <form onSubmit={handleSubmit}>
      <div className="form-group">
        <label htmlFor="fullName">Full Name</label>
        <input
          type="text"
          id="fullName"
          name="fullName"
          value={formData.fullName}
          onChange={handleChange}
          required
        />
      </div>

      <div className="form-group">
        <label htmlFor="email">Email</label>
        <input
          type="email"
          id="email"
          name="email"
          value={formData.email}
          onChange={handleChange}
          required
        />
      </div>

      <div style={{marginLeft:'20%'}}>
      <label htmlFor="showPassword">
      <input style={{display:'inline-block',marginRight:'5px'}}
          type="checkbox"
          id="showPassword"
          name="showPassword"
          checked={showPasswordFields}
          onChange={handleCheckboxChange}
        />  Change Password?</label>
        
      </div>

      {showPasswordFields && (
        <>
          <div className="form-group">
            <label htmlFor="password">Password</label>
            <input
              type="password"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              required={showPasswordFields}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">Confirm Password</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={formData.confirmPassword}
              onChange={handleChange}
              required={showPasswordFields}
            />
          </div>
        </>
      )}

      <button className="Update-button" type="submit">
        Update Profile
      </button>
    </form>
        )}
      </div>
    </div>
  );
};

export default ProfileForm;
