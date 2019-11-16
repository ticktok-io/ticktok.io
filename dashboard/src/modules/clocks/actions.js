import axios from 'axios';

export const FETCH_CLOCKS = 'fetch_clocks';
export const UPDATE_STATUS = 'UPDATE_STATUS';

export const ACTIVE = 'ACTIVE';
export const PAUSED = 'PAUSED';


export function fetchClocks(apiKey) {
  const request = axios.get(`/api/v1/clocks?access_token=${apiKey}`);

  return {
    type: FETCH_CLOCKS,
    payload: request
  };
}

export function invokeAction(actionUrl, apiKey) {
  const url = new URL(actionUrl);
  url.searchParams.append('access_token', apiKey);
  const request = axios.put(url.pathname + '?' + url.searchParams).then((res) => {
    return res.data
  });

  return {
    type: UPDATE_STATUS,
    payload: request
  };
}
