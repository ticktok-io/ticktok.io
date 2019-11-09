import _ from 'lodash'
import {FETCH_CLOCKS, UPDATE_STATUS} from "./actions";

export default function (state = [], action) {
  switch (action.type) {
    case FETCH_CLOCKS:
      return _.mapKeys(action.payload.data, 'id');
    case UPDATE_STATUS:
      const updatedClock = state[action.payload.id];
      updatedClock.status = action.payload.status;
      updatedClock.links = action.payload.links;
      return {...state, [updatedClock.id]: updatedClock};
    default:
      return state;
  }
}


