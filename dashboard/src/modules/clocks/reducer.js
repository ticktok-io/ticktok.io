import {FETCH_CLOCKS} from "./actions";


export default function (state = [], action) {
  switch (action.type) {
    case FETCH_CLOCKS:
      const { payload } = action;
      if(payload.data) {
        return payload.data;
      }
      return state;
    default:
      return state;
  }
}
