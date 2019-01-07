import axios from 'axios';

export const FETCH_CLOCKS = 'fetch_clocks';
export const RESUME_CLOCK = 'resume_clocks';

export const ACTIVE = 'ACTIVE';
export const PAUSED = 'PAUSED';
/*const mockClocks = [
  {id: "1", name: "kuku", schedule: "every.4.seconds", status: "ACTIVE"},
  {id: "2", name: "popov", schedule: "every.11.seconds", status: "ACTIVE"},
  {id: "3", name: "shushu", schedule: "every.40.seconds", status: "PAUSED"},
];*/

export function fetchClocks(apiKey) {
  const request = axios.get(`/api/v1/clocks?access_token=${apiKey}`);
  // const request = Promise.resolve({data: mockClocks});

  return {
    type: FETCH_CLOCKS,
    payload: request
  };
}

export function resumeClock(id, apiKey) {
  const request = axios.put(`/api/v1/clocks/${id}/resume?access_token=${apiKey}`);

  return {
    type: RESUME_CLOCK,
    payload: {id: id, status: ACTIVE}
  };
}
