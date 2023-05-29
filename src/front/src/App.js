import "./App.css";
import React from "react";
import { BrowserRouter, Route, Routes, Navigate } from "react-router-dom";
import NavigationBar from "./components/NavigationBar";
import Home from "./components/Home";
import Login from "./components/Login";
import SideBar from "./components/SideBar";
import CountryListComponent from "./components/country/CountryListComponent";
import CountryComponent from "./components/country/CountryComponent";
import PaintingListComponent from "./components/painting/PaintingListComponent";
import PaintingComponent from "./components/painting/PaintingComponent";
import ArtistListComponent from "./components/artist/ArtistListComponent";
import ArtistComponent from "./components/artist/ArtistComponent";
import MuseumListComponent from "./components/museum/MuseumListComponent";
import MuseumComponent from "./components/museum/MuseumComponent";
import UserListComponent from "./components/user/UserListComponent";
import UserComponent from "./components/user/UserComponent";
import Utils from "./utils/Utils";
import { connect } from "react-redux";
import { useState } from "react";

const ProtectedRoute = ({ children }) => {
  let user = Utils.getUser();
  return user ? children : <Navigate to={"/login"} />;
};

const App = (props) => {
  const [exp, setExpanded] = useState(true);
  return (
    <div className="App">
      <BrowserRouter>
        <NavigationBar toggleSideBar={() => setExpanded(!exp)} />
        <div className="wrapper">
          <SideBar expanded={exp} />
          <div className="container-fluid">
            {props.error_message && (
              <div className="alert alert-danger m-1">
                {props.error_message}
              </div>
            )}
            <Routes>
              <Route path="login" element={<Login />} />
              <Route
                path="home"
                element={
                  <ProtectedRoute>
                    <Home />
                  </ProtectedRoute>
                }
              />
              <Route
                path="countries"
                element={
                  <ProtectedRoute>
                    <CountryListComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="countries/:id"
                element={
                  <ProtectedRoute>
                    <CountryComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="artists"
                element={
                  <ProtectedRoute>
                    <ArtistListComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="artists/:id"
                element={
                  <ProtectedRoute>
                    <ArtistComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="paintings"
                element={
                  <ProtectedRoute>
                    <PaintingListComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="paintings/:id"
                element={
                  <ProtectedRoute>
                    <PaintingComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="museums"
                element={
                  <ProtectedRoute>
                    <MuseumListComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="museums/:id"
                element={
                  <ProtectedRoute>
                    <MuseumComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="users"
                element={
                  <ProtectedRoute>
                    <UserListComponent />
                  </ProtectedRoute>
                }
              />
              <Route
                path="users/:id"
                element={
                  <ProtectedRoute>
                    <UserComponent />
                  </ProtectedRoute>
                }
              />
            </Routes>
          </div>
        </div>
      </BrowserRouter>
    </div>
  );
};

function mapStateToProps(state) {
  const { msg } = state.alert;
  return { error_message: msg };
}

export default connect(mapStateToProps)(App);
