import React from 'react';
import { render, screen } from '@testing-library/react';
import FulfillmentInfo from '../FulfillmentInfo'; // Adjust the path as necessary
import Pickup from '../../screens/assets/pickup.png';
import Shipping from '../../screens/assets/shipping.png';
import Delivery from '../../screens/assets/delivery.png';

describe('FulfillmentInfo', () => {
  test('renders the FulfillmentInfo component with correct images and alt text', () => {
    render(<FulfillmentInfo />);

    // Check that the Pickup image is rendered with correct alt text and src
    const pickupImg = screen.getByAltText('Pickup Allowed');
    expect(pickupImg).toBeInTheDocument();
    expect(pickupImg).toHaveAttribute('src', Pickup);

    // Check that the Shipping image is rendered with correct alt text and src
    const shippingImg = screen.getByAltText('Shipping Allowed');
    expect(shippingImg).toBeInTheDocument();
    expect(shippingImg).toHaveAttribute('src', Shipping);

    // Check that the Delivery image is rendered with correct alt text and src
    const deliveryImg = screen.getByAltText('Delivery Allowed');
    expect(deliveryImg).toBeInTheDocument();
    expect(deliveryImg).toHaveAttribute('src', Delivery);
  });

  test('should render the correct fulfillment labels', () => {
    render(<FulfillmentInfo />);

    // Check that Pick-Up text is rendered
    expect(screen.getByText(/Pick-Up/i)).toBeInTheDocument();
    // Check that Shipping text is rendered
    expect(screen.getByText(/Shipping/i)).toBeInTheDocument();
    // Check that Delivery text is rendered
    expect(screen.getByText(/Delivery/i)).toBeInTheDocument();
  });

  test('should render all images with correct dimensions', () => {
    render(<FulfillmentInfo />);

    // Check Pickup image dimensions
    const pickupImg = screen.getByAltText('Pickup Allowed');
    expect(pickupImg).toHaveAttribute('width', '40');
    expect(pickupImg).toHaveAttribute('height', '40');

    // Check Shipping image dimensions
    const shippingImg = screen.getByAltText('Shipping Allowed');
    expect(shippingImg).toHaveAttribute('width', '40');
    expect(shippingImg).toHaveAttribute('height', '40');

    // Check Delivery image dimensions
    const deliveryImg = screen.getByAltText('Delivery Allowed');
    expect(deliveryImg).toHaveAttribute('width', '40');
    expect(deliveryImg).toHaveAttribute('height', '40');
  });
});
