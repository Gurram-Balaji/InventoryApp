import React from "react";
// import ReactDOM from "react-dom";
import "./index.css";
import { Provider } from 'react-redux';

import App from "./App";
import { BrowserRouter } from "react-router-dom";
// import reportWebVitals from './reportWebVitals';
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

