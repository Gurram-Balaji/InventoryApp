const Input = ({ id, type, label, disabled , ...Input}) => (
	<input className="form-group__input" type={type} id={id} placeholder={label} disabled={disabled} {...Input}/>
);

export default Input;