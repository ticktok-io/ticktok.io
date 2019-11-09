import React, {Component} from 'react';
import _ from 'lodash';

export const PAUSE_BTN = 'Pause';
export const RESUME_BTN = 'Resume';

export class ClockActions extends Component {

  render() {
    return (
      <div className="clockActions btn-group" role="group">
        <button
          type="button"
          name="tick"
          className="btn btn-default btn-outline-primary"
          data-toggle="button"
          onClick={() => {
            this.onActionClick("tick")
          }}>
          Tick
        </button>
        <button
          type="button"
          name={this.pauseOrResume()}
          className={this.pauseResumeClassName()}
          data-toggle="button"
          onClick={() => {
            this.onActionClick(this.pauseOrResume())
          }}>
          {_.capitalize(this.pauseOrResume())}
        </button>
      </div>
    );
  }

  pauseResumeClassName() {
    return `btn btn-default ${this.isPaused() ? 'btn-outline-success' : 'btn-outline-danger'}`;
  }

  isPaused() {
    return this.clockLinks().hasOwnProperty('resume')
  }

  clockLinks() {
    return _.chain(this.props.clock.links)
      .keyBy('rel')
      .mapValues('href')
      .value();
  }

  pauseOrResume() {
    return this.isPaused() ? 'resume' : 'pause'
  }

  actionText() {
    return this.isPaused() ? RESUME_BTN : PAUSE_BTN;
  }

  onActionClick(actionName) {
    this.props.invokeAction(this.clockLinks()[actionName], this.props.apiKey);
  }
}
