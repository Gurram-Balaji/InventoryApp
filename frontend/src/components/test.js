import React, { useState } from 'react';

const SayHiComponent = () => {
  // State to manage showing additional message
  const [showMessage, setShowMessage] = useState(false);

  // Function to toggle the additional message
  const handleClick = () => {
    setShowMessage(!showMessage);
  };

  return (
   
    <div>
      <h1>Hi!{ console.log("Token in PrivateRoute:")}</h1>
      <button onClick={handleClick}>
        {
 
  showMessage ? 'Hide Message' : 'Show Message'}
      </button>
      {showMessage && <p>Welcome to the React world!</p>}
    </div>
  );
};

export default SayHiComponent;
