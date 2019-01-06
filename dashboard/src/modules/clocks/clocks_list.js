import React, {Component} from 'react';
import {fetchClocks, resumeClock} from "./actions";
import {connect} from 'react-redux';
import _ from 'lodash';

const ACTIVE = 'ACTIVE';

export class ClocksList extends Component {

  static NO_CLOCKS_MSG = "No clocks are configured yet!";

  constructor(props) {
    super(props);
    this.onPauseResumeClick = this.onPauseResumeClick.bind(this);
  }

  componentDidMount() {
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
            <button
              type="button"
              className={this.actionClassName(clock)}
              data-toggle="button"
              onClick={this.onPauseResumeClick(clock.id)}>
              {this.actionText(clock)}
            </button>
          </td>
        </tr>
      );
    });
  }

  clockRowClass(clock) {
    return `clock-row ${clock.status === ACTIVE ? '' : 'paused'}`;
  }

  actionClassName(clock) {
    return `btn ${clock.status === ACTIVE ? 'btn-outline-primary' : 'btn-outline-warning'}`;
  }

  actionText(clock) {
    return clock.status === ACTIVE ? 'Pause' : 'Resume';
  }

  onPauseResumeClick(id) {
    console.log('>>>>', id);
    resumeClock(id, this.props.apiKey);
  }
}

function mapStateToProps(state) {
  return {clocks: state.clocks}
}

export default connect(mapStateToProps, {fetchClocks, resumeClock})(ClocksList)
