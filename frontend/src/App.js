import { Route, Routes, useLocation } from "react-router";
import Sidebar from "./sidebar";
import Home from "./screens/Home";
import Items from "./screens/items";
import Supply from "./screens/supply";
import Location from "./screens/location";
import Threshold from "./screens/threshold";
import Demand from "./screens/demand";
import Available from "./screens/availability";
import Profile from "./screens/profile";


import styled from "styled-components";
import { AnimatePresence } from "framer-motion";
import LoginComponent from './screens/login/LoginPage';
import PrivateRoute from "./components/PrivateRoute";
import "react-toastify/dist/ReactToastify.css";
import { ToastContainer } from "react-toastify";
import StackedBarChart from "./screens/stackedBarChat";

const Pages = styled.div`
  width: 100vw;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;

  h1 {
    font-size: calc(2rem + 2vw);
    background: linear-gradient(to right, #803bec 30%, #1b1b1b 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
  }
`;

function App() {
  const location = useLocation();
  
  return (
    <>
     <PrivateRoute><Sidebar /></PrivateRoute> 
      <Pages>
        <AnimatePresence> 
          <Routes location={location} key={location.pathname}>
            <Route exact path="/" element={<LoginComponent mode={"login"} />} />
            <Route path="/dashboard" element={<PrivateRoute><Home /></PrivateRoute>}/>
            <Route path="/items" element={<PrivateRoute><Items /></PrivateRoute>} />
            <Route path="/location" element={<PrivateRoute><Location /></PrivateRoute>} />
            <Route path="/supply" element={<PrivateRoute><Supply /></PrivateRoute>} />
            <Route path="/demand" element={<PrivateRoute><Demand /></PrivateRoute>} />
            <Route path="/threshold" element={<PrivateRoute><Threshold /></PrivateRoute>} />
            <Route path="/available" element={<PrivateRoute><Available /></PrivateRoute>} />
            <Route path="/stackedBarchat" element={<PrivateRoute><StackedBarChart/></PrivateRoute>} />
            <Route path="/profile" element={<PrivateRoute><Profile/></PrivateRoute>} />


          </Routes>
          <ToastContainer />
        </AnimatePresence>
      </Pages>
    </>
  );
}

export default App;
