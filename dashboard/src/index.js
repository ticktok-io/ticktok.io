import React from 'react';
import ReactDOM from 'react-dom';
import {Provider} from 'react-redux';
import {BrowserRouter, Route} from 'react-router-dom';

import App from './app';
import './index.css';
import {createAppStore} from "./store";

ReactDOM.render(
  <Provider store={createAppStore()}>
    <BrowserRouter>
      <Route path="/" component={App}/>
    </BrowserRouter>
  </Provider>
  ,document.querySelector('.container'));


