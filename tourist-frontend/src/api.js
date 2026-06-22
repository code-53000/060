import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || '请求失败，请稍后重试'
    alert(message)
    return Promise.reject(error)
  }
)

export default api

export const tourApi = {
  getAll: () => api.get('/tours'),
  getPending: () => api.get('/tours/pending'),
  getById: (id) => api.get(`/tours/${id}`)
}

export const registrationApi = {
  create: (data) => api.post('/registrations', data)
}
