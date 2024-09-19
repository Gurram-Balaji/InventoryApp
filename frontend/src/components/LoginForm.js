
import React, { useState } from 'react';
import { useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import { loginSuccess } from '../store/authSlice';
import Input from './Input';
import { errorToast, successToast } from './Toast';
import apiClient from './baseUrl';

const LoginForm = ({ mode, toggleMode }) => {
	const [email, setEmail] = useState('');
	const [password, setPassword] = useState('');
	const [SignUpemail, setSignUpEmail] = useState('');
	const [SignUpfullName, setSignUpFullName] = useState('');
	const [SignUppassword, setSignUpPassword] = useState('');
	const [SignUpconfirmPassword, setSignUpConfirmPassword] = useState('');
	const [loading, setLoading] = useState(false);
	const navigation = useNavigate();
	const dispatch = useDispatch();

	const handleLogin = async (event) => {
		event.preventDefault();
		setLoading(true);

		try {


			if (!email || !password) {
				errorToast('Please enter both email and password.');
				return;
			}
			const response = await apiClient.post('/auth/signin', { email, password });
			if (response.data.status === 404)
				errorToast(response.data.message);
			else {
				dispatch(loginSuccess(response.data.payload));  // Save token in Redux store
				navigation('/dashboard');
			}
		} catch (error) {
			errorToast('Something went wrong..');
		}finally {
			setLoading(false);
		  }
	};
	const handleSignup = async (event) => {
		event.preventDefault();
		setLoading(true);
		try {
			const messages = [];

			if (SignUppassword.length < 8) {
				messages.push('at least 8 characters');
			}
			if (!/[a-z]/.test(SignUppassword)) {
				messages.push('a lowercase letter');
			}
			if (!/[A-Z]/.test(SignUppassword)) {
				messages.push('an uppercase letter');
			}
			if (!/\d/.test(SignUppassword)) {
				messages.push('a number');
			}
			if (!/[@$!%*?&]/.test(SignUppassword)) {
				messages.push('a special character(@$!%*?&)');
			}

			// Set the message to indicate which criteria are missing
			if (messages.length > 0) {
				errorToast(`Password is weak. Please include ${messages.join(', ')}.`);
				return;
			}
			if (!SignUpfullName || !SignUpemail || !SignUppassword || !SignUpconfirmPassword) {
				errorToast('Please fill in all fields.');
				return;
			}

			if (SignUppassword !== SignUpconfirmPassword) {
				errorToast("Passwords do not match");
				return
			}

			const response = await apiClient.post('/auth/signup', {
				fullName: SignUpfullName,
				email: SignUpemail,
				password: SignUppassword,
			});
			if (response.data.status === 404) {
				errorToast(response.data.message);
				return;
			}
			else if (response.data.success === true) {
				successToast(response.data.payload);
				successToast(response.data.message);

				toggleMode();
				setSignUpEmail("");
				setSignUpFullName("");
				setSignUpPassword("");
				setSignUpConfirmPassword("");
			}
		} catch (error) {
			errorToast('Something went wrong..');
		}finally {
			setLoading(false);
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
			<button className="button button--primary full-width"  disabled={loading}>
			{loading ? 'Loading...' : (mode === 'login' ? 'Log In' : 'Sign Up')}
			</button>
		</form>

	);
};

export default LoginForm;
