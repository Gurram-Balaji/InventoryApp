
import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginSuccess } from '../store/authSlice';
import Input from './Input';
import { errorToast } from './Toast';
import apiClient from './baseUrl';


const LoginForm = ({ mode }) => {
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');
	const [SignUpemail, setSignUpEmail] = useState('');
	const [SignUpfullName, setSignUpFullName] = useState('');
	const [SignUppassword, setSignUpPassword] = useState('');
	const [SignUpconfirmPassword, setSignUpConfirmPassword] = useState('');
	const navigation = useNavigate();
	const dispatch = useDispatch();


	const handleLogin = async (event) => {
		event.preventDefault();
		try {
			if (!email || !password) {
				errorToast('Please enter both email and password.');
				return;
			}
			const response = await apiClient.post('/auth/signin', { email, password });
			if(response.data.status===404)
				errorToast(response.data.message);
			else{
			dispatch(loginSuccess(response.data.payload));  // Save token in Redux store
			navigation('/dashboard');
			}
		} catch (error) {
			errorToast('Something went wrong..');
		}
	};
	const handleSignup = async (event) => {
		event.preventDefault();
		try {
			if (!SignUpfullName || !SignUpemail || !SignUppassword || !SignUpconfirmPassword) {
				errorToast('Please fill in all fields.');
				return;
			}

			if (SignUppassword !== SignUpconfirmPassword) {
				errorToast("Passwords do not match");
				return
			}

			const response = await apiClient.post('/auth/signup', {
				fullname: SignUpfullName,
				email: SignUpemail,
				password: SignUppassword,
			});
			if (response.data.status === 404) {
				errorToast(response.data.message);
				return;
			}
			else if (response.data.success === true) {
				dispatch(loginSuccess(response.data.payload));
				navigation('/dashboard');
			}
		} catch (error) {
			errorToast('Something went wrong..');
		}
	};

	return (
		<form onSubmit={mode === 'login' ? handleLogin : handleSignup}>
			<div className="form-block__input-wrapper">
				<div className="form-group form-group--login">
					<Input type="text" id="Email" label="Email" value={email} onChange={(e) => setEmail(e.target.value)} disabled={mode === 'signup'} />
					<Input type="password" id="password" value={password} onChange={(e) => setPassword(e.target.value)} label="password" disabled={mode === 'signup'} />

				</div>
				<div className="form-group form-group--signup">
					<Input type="text" id="fullname" label="Full name" disabled={mode === 'login'} value={SignUpfullName} onChange={(e) => setSignUpFullName(e.target.value)} />
					<Input type="email" id="email" label="Email" disabled={mode === 'login'} value={SignUpemail} onChange={(e) => setSignUpEmail(e.target.value)} />
					<Input type="password" id="createpassword" label="Password" disabled={mode === 'login'} value={SignUppassword} onChange={(e) => setSignUpPassword(e.target.value)} />
					<Input type="password" id="repeatpassword" label="Repeat password" disabled={mode === 'login'} value={SignUpconfirmPassword} onChange={(e) => setSignUpConfirmPassword(e.target.value)} />
				</div>
			</div>
			<button className="button button--primary full-width">
				{mode === 'login' ? 'Log In' : 'Sign Up'}
			</button>
		</form>
	);
};

export default LoginForm;
