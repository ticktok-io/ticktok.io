import axios from 'axios';

export const FETCH_CLOCKS = 'fetch_clocks';

export function fetchClocks(apiKey) {
  const request = axios.get(`/api/v1/clocks?access_token=${apiKey}`);

  return {
    type: FETCH_CLOCKS,
    payload: request
  };
}
