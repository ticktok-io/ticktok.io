import {applyMiddleware, createStore} from "redux";
import ReduxPromise from "redux-promise";
import reducers from "./reducers";

export function createAppStore() {
  return applyMiddleware(ReduxPromise)(createStore)(reducers);
}
