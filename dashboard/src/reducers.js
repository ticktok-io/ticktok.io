import { combineReducers } from 'redux';
import ClockReducer from './modules/clocks/reducer'

const rootReducer = combineReducers({
  clocks: ClockReducer
});

export default rootReducer;
