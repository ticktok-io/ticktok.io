import React from 'react';
import ClocksList from "../clocks_list";
import {cleanup, render, wait} from "react-testing-library";
import "jest-dom/extend-expect"
import axios from 'axios';
import MockAdapter from "axios-mock-adapter";
import {createAppStore} from "../../../store";

const store = createAppStore();
const backend = new MockAdapter(axios);
const apiKey = '121234';

beforeEach(() => {
  backend.reset()
});

test("Show no clocks message", () => {
  givenTheClocks([]);
  const {getByText} = renderClockList();
  expect(getByText(ClocksList.NO_CLOCKS_MSG)).toBeInTheDocument();

});

function givenTheClocks(clocks) {
  backend.onGet(`/api/v1/clocks?access_token=${apiKey}`).reply(200, clocks);
}

let renderClockList = function () {
  return render(<ClocksList store={store} apiKey={apiKey}/>);
};

test("Show fetched clocks", async () => {
  let clocks = [
    {id: '3232', schedule: "every.2.seconds"},
    {id: '1212', schedule: "every.8.seconds"}
  ];
  givenTheClocks(clocks);

  const {queryByText, getAllByTestId} = renderClockList();
  await wait(() => expect(queryByText(ClocksList.NO_CLOCKS_MSG)).not.toBeInTheDocument());
  const rows = getAllByTestId("clock-row");
  expect(rows).toHaveLength(2);
  expect(rows[0]).toHaveTextContent(clocks[0].schedule);
  expect(rows[1]).toHaveTextContent(clocks[1].schedule);
});

afterEach(cleanup);

afterAll(() => {
  backend.restore()
});
