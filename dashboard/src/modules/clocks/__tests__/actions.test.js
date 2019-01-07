import axios from 'axios';
import MockAdapter from "axios-mock-adapter";
import {ACTIVE, PAUSED, pauseResumeClock} from '../actions';
import {wait} from "dom-testing-library";

const apiKey = "4321";
const backend = new MockAdapter(axios);

test('pause on toggle active clock', async () => {
  backend.onPut(`/api/v1/clocks/111/pause?access_token=${apiKey}`).reply(200);
  const action = pauseResumeClock({id: "111", status: ACTIVE}, apiKey);
  await wait(() => expect(backend.history.put.length).toBe(1));
  expect(action.payload.status).toBe(PAUSED);
});
