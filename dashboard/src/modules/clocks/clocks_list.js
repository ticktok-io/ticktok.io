import React, {Component} from 'react';
import {fetchClocks} from "./actions";
import {connect} from 'react-redux';
import _ from 'lodash';

export class ClocksList extends Component {

  static NO_CLOCKS_MSG = "No clocks are configured yet!";

  componentDidMount() {
    this.timer = setInterval(this._tick, 2000)
  }

  _tick = () => {
    this.props.fetchClocks(this.props.apiKey);
  };

  componentWillUnmount() {
    clearInterval(this.timer)
  }

  eli() {

  }

  render() {
    return (
      <div>
        <table className="table">
          <thead className="thead-light">
          <tr>
            <th scope="col">Name</th>
            <th scope="col">Schedule</th>
            <th scope="col"/>
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
    if (this.props.clocks === undefined || this.props.clocks.length === 0) {
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
            <button type="button" className={this.actionClassName(clock)}
                    data-toggle="button">{this.actionText(clock)}</button>
          </td>
        </tr>
      );
    });
  }

  clockRowClass(clock) {
    return `clock-row ${clock.status === 'ACTIVE' ? '' : 'paused'}`;
  }

  actionClassName(clock) {
    return `btn ${clock.status === 'ACTIVE' ? 'btn-outline-primary' : 'btn-outline-warning'}`;
  }

  actionText(clock) {
    return clock.status === 'ACTIVE' ? 'Pause' : 'Resume';
  }
}

function mapStateToProps(state) {
  return {clocks: state.clocks}
}

export default connect(mapStateToProps, {fetchClocks})(ClocksList)
