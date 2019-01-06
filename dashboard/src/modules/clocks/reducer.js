import {FETCH_CLOCKS} from "./actions";


const mockClocks = [
  {id: "1", name: "kuku", schedule:"every.4.seconds", status: "ACTIVE"},
  {id: "2", name: "popov", schedule:"every.11.seconds", status: "ACTIVE"},
  {id: "3", name: "shushu", schedule:"every.40.seconds", status: "DISABLED"},
];


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


