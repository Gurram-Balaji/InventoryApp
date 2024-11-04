import React from 'react';
import { render, screen } from '@testing-library/react';
import Input from '../Input';

afterEach(() => {
  jest.clearAllTimers(); // Clear any timers to prevent leaks
});


describe('Input Component', () => {
  test('renders the input with correct placeholder', () => {
    render(<Input id="username" type="text" label="Username" />);
    
    const inputElement = screen.getByPlaceholderText('Username');
    expect(inputElement).toBeInTheDocument();
  });

  test('renders the input with correct type', () => {
    render(<Input id="password" type="password" label="Password" />);
    
    const inputElement = screen.getByPlaceholderText('Password');
    expect(inputElement).toHaveAttribute('type', 'password');
  });

  test('renders the input with correct id', () => {
    render(<Input id="email" type="email" label="Email" />);
    
    const inputElement = screen.getByPlaceholderText('Email');
    expect(inputElement).toHaveAttribute('id', 'email');
  });

  test('disables the input when disabled prop is true', () => {
    render(<Input id="disabledInput" type="text" label="Disabled Input" disabled />);
    
    const inputElement = screen.getByPlaceholderText('Disabled Input');
    expect(inputElement).toBeDisabled();
  });

  test('accepts additional props and renders correctly', () => {
    render(<Input id="testInput" type="text" label="Test Input" data-testid="custom-test" />);
    
    const inputElement = screen.getByTestId('custom-test');
    expect(inputElement).toBeInTheDocument();
  });
});
