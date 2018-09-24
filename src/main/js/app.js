import React, {Component} from 'react';
import {connect} from 'react-redux';

class App extends Component {
  render() {
    // const key = new URLSearchParams(this.props.location.search).get('key');
    return (
      <div>
        <div className="pos-f-t">
          <nav className="navbar navbar-dark bg-dark">
            <h4>Ticktok.io playground</h4>
          </nav>
          <h1 className="greeting" id="greeting">Hello Worlsss</h1>
        </div>
      </div>
    );
  }
}

export default connect(null)(App);
