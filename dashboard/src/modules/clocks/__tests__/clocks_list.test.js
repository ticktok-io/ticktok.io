import React from 'react';
import { render } from "react-testing-library";
import "dom-testing-library/extend-expect";
import ClocksList from "../clocks_list";
import renderer from 'react-test-renderer';

test("Show configured clocks", () => {

  const { getByTestId } = renderer.create(<ClocksList />);


});

