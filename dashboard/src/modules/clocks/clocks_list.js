import React, {Component} from 'react';
import {ACTIVE, fetchClocks, invokeAction} from "./actions";
import {connect} from 'react-redux';
import _ from 'lodash';
import {ClockActions} from "./clock_actions";


export class ClocksList extends Component {

  static NO_CLOCKS_MSG = "No clocks are configured yet";

  componentDidMount() {
    this._tick();
    this.timer = setInterval(this._tick, 2000)
  }

  _tick = () => {
    this.props.fetchClocks(this.props.apiKey);
  };

  componentWillUnmount() {
    clearInterval(this.timer)
  }

  render() {
    return (
      <div>
        <table className="table table-bordered">
          <thead className="thead-light">
          <tr>
            <th scope="col">Name</th>
            <th scope="col">Schedule</th>
            <th scope="col" className="actions">Actions</th>
          </tr>
          </thead>
          <tbody>
          {this.renderClocks()}
          </tbody>
        </table>
        {this.showNoClocksMessageIfNeeded()}
      </div>
    );
  }

  showNoClocksMessageIfNeeded() {
    if (this.props.clocks === undefined || Object.keys(this.props.clocks).length === 0) {
      return (
        <div className="noClocksMsg" key="no-clock-msg">
          {ClocksList.NO_CLOCKS_MSG}
        </div>
      );
    }
    return (<div/>)
  }

  renderClocks() {
    return _.map(this.props.clocks, clock => {
      return (
        <tr data-testid="clock-row" className={this.clockRowClass(clock)} key={clock.id}>
          <td>{clock.name}</td>
          <td>{clock.schedule}</td>
          <td>
            <ClockActions
              apiKey={this.props.apiKey}
              clock={clock}
              invokeAction={this.props.invokeAction}/>
          </td>
        </tr>
      );
    });
  }

  clockRowClass(clock) {
    return `clock-row ${clock.status === ACTIVE ? '' : 'paused'}`;
  }
}

function mapStateToProps(state) {
  return {clocks: state.clocks}
}

export default connect(mapStateToProps, {fetchClocks, invokeAction})(ClocksList)
