import React from 'react';
import Delivery from '../screens/assets/delivery.png';
import Shipping from '../screens/assets/shipping.png';
import Pickup from '../screens/assets/pickup.png';

const FulfillmentInfo = () => {
  return (
    <div className="fulfillment-container">
      <div className="fulfillment-info">
        <img src={Pickup} alt="Pickup Allowed" width='40' height='40' /> Pick-Up
        <img src={Shipping} alt="Shipping Allowed" width='40' height='40' /> Shipping
        <img src={Delivery} alt="Delivery Allowed" width='40' height='40' /> Delivery
      </div>
    </div>
  );
};

export default FulfillmentInfo;
