import '@testing-library/jest-dom'; // No need for '/extend-expect'
// src/setupTests.js
class ResizeObserver {
    constructor(callback) {
      this.callback = callback;
    }
    observe() {}
    unobserve() {}
    disconnect() {}
  }
  
  global.ResizeObserver = ResizeObserver;
  
  // Any other setup code for your tests can go here.
  