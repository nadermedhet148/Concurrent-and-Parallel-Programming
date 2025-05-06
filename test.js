import http from 'k6/http'
import { check, sleep } from 'k6'

export let options = {
  vus: 10000,
  iterations: 100000,
};
export default function () {
  let res = http.get('http://localhost:8080/')

  check(res, { 'success login': (r) => r.status === 200 })

}
