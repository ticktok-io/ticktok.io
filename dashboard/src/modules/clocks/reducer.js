import _ from 'lodash'
import {FETCH_CLOCKS, RESUME_CLOCK} from "./actions";

export default function (state = [], action) {
  console.log(action.type);
  switch (action.type) {
    case FETCH_CLOCKS:
      return _.mapKeys(action.payload.data, 'id');
    case RESUME_CLOCK:
      console.log('==>', action);
      const updatedClock = state[action.payload.id];
      updatedClock.status = action.payload.status;
      return {...state, updatedClock};
    default:
      return state;
  }
}


