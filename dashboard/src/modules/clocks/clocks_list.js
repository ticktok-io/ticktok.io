import React, {Component} from 'react';
import {ACTIVE, fetchClocks, pauseResumeClock} from "./actions";
import {connect} from 'react-redux';
import _ from 'lodash';

export const PAUSED_BTN = 'Pause';
export const RESUME_BTN = 'Resume';

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
              onClick={() => {
                this.onPauseResumeClick(clock)
              }}>
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
    return clock.status === ACTIVE ? PAUSED_BTN : RESUME_BTN;
  }

  onPauseResumeClick(clock) {
    this.props.pauseResumeClock(clock, this.props.apiKey);
  }
}

function mapStateToProps(state) {
  return {clocks: state.clocks}
}

export default connect(mapStateToProps, {fetchClocks, pauseResumeClock})(ClocksList)
