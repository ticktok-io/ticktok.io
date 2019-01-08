import axios from 'axios';

export const FETCH_CLOCKS = 'fetch_clocks';
export const UPDATE_STATUS = 'UPDATE_STATUS';

export const ACTIVE = 'ACTIVE';
export const PAUSED = 'PAUSED';

const mockClocks = [
  {id: "1", name: "kuku", schedule: "every.4.seconds", status: "ACTIVE"},
  {id: "2", name: "popov", schedule: "every.11.seconds", status: "ACTIVE"},
  {id: "3", name: "shushu", schedule: "every.40.seconds", status: "PAUSED"},
];

export function fetchClocks(apiKey) {
    // const request = Promise.resolve({data: mockClocks});
    const request = axios.get(`/api/v1/clocks?access_token=${apiKey}`);

  return {
    type: FETCH_CLOCKS,
    payload: request
  };
}

export function pauseResumeClock(clock, apiKey) {
  const action = clock.status === ACTIVE ? 'pause' : 'resume';
  const request = axios.put(`/api/v1/clocks/${clock.id}/${action}?access_token=${apiKey}`).then((res) => {
    return {id: clock.id, status: clock.status === ACTIVE ? PAUSED : ACTIVE}
  });

  return {
    type: UPDATE_STATUS,
    payload: request
  };
}
