import _ from 'lodash'
import {FETCH_CLOCKS, RESUME_CLOCK} from "./actions";

export default function (state = [], action) {
  switch (action.type) {
    case FETCH_CLOCKS:
      return _.mapKeys(action.payload.data, 'id');
    case RESUME_CLOCK:
      const updatedClock = state[action.payload.id];
      updatedClock.status = action.payload.status;
      return {...state, [updatedClock.id]: updatedClock};
    default:
      return state;
  }
}


