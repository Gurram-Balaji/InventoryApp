
import React, { useState, useEffect } from 'react';
import { useNavigate } from "react-router-dom";
import LoginForm from '../../components/LoginForm';
import { useSelector } from "react-redux";
import { selectToken } from "../../store/authSlice";
import '../../index.css';


const LoginComponent = ({ mode: initialMode }) => {
	const [mode, setMode] = useState(initialMode);
	const navigate = useNavigate();
	const token = useSelector(selectToken);

	useEffect(() => {
		if (token) {
			navigate("/dashboard");
		}
	}, [token,navigate]);

	const toggleMode = () => {
		setMode(prevMode => (prevMode === 'login' ? 'signup' : 'login'));
	};

	return (
		<div>
			<div className={`form-block-wrapper form-block-wrapper--is-${mode}`}></div>
			<section className={`form-block form-block--is-${mode}`}>
				<header className="form-block__header">
					<h1>{mode === 'login' ? 'Welcome back!' : 'Sign up'}</h1>
					<div className="form-block__toggle-block">
						<span>{mode === 'login' ? "Don't" : 'Already'} have an account? Click here &#8594;</span>
						<input id="form-toggler" type="checkbox" onClick={toggleMode} />
						<label htmlFor="form-toggler"></label>
					</div>
				</header>
				<LoginForm mode={mode} />
			</section>
		</div>
	);
};

export default LoginComponent;
