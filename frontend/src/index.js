import React from "react";
import { Provider } from 'react-redux';
import App from "./App";
import { BrowserRouter } from "react-router-dom";
import { createRoot } from "react-dom/client";
import store from './store/store';

const domNode = document.getElementById("root");
const root = createRoot(domNode);
root.render(
  <Provider store={store}>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </Provider>
);