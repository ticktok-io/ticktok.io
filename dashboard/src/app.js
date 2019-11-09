import React, {Component} from 'react';
import {connect} from 'react-redux';
import ClocksList from "./modules/clocks/clocks_list";

class App extends Component {
  render() {
    const key = new URLSearchParams(this.props.location.search).get('api_key');
    return (
      <div>
        <div className="pos-f-t">
          <nav className="navbar navbar-dark">
            <h4>ticktok.io</h4>
          </nav>
          <div className="main">
            <ClocksList apiKey={key}/>
          </div>
        </div>
      </div>
    );
  }
}

export default connect(null)(App);
