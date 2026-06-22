import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
})

api.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const message = error.response?.data?.message || '请求失败'
    alert(message)
    return Promise.reject(error)
  }
)

export default api

export const tourApi = {
  getAll: (status) => api.get('/tours', { params: { status } }),
  getPending: () => api.get('/tours/pending'),
  getById: (id) => api.get(`/tours/${id}`),
  create: (data) => api.post('/tours', data),
  update: (id, data) => api.put(`/tours/${id}`, data),
  delete: (id) => api.delete(`/tours/${id}`),
  cancel: (id) => api.post(`/tours/${id}/cancel`)
}

export const guideApi = {
  getAll: (params) => api.get('/guides', { params }),
  getById: (id) => api.get(`/guides/${id}`),
  searchByLanguages: (languages) => api.get('/guides/search', { params: { languages } }),
  create: (data) => api.post('/guides', data),
  update: (id, data) => api.put(`/guides/${id}`, data),
  delete: (id) => api.delete(`/guides/${id}`)
}

export const registrationApi = {
  getByTour: (tourId) => api.get('/registrations', { params: { tourRouteId: tourId } }),
  getActiveByTour: (tourId) => api.get(`/registrations/tour/${tourId}/active`),
  getById: (id) => api.get(`/registrations/${id}`),
  create: (data) => api.post('/registrations', data),
  confirm: (id) => api.post(`/registrations/${id}/confirm`),
  cancel: (id) => api.post(`/registrations/${id}/cancel`)
}

export const dispatchApi = {
  getToursNeedingGuide: () => api.get('/dispatch/tours-needing-guide'),
  getAvailableGuides: (tourId) => api.get(`/dispatch/tours/${tourId}/available-guides`),
  assignGuide: (tourId, data) => api.post(`/dispatch/tours/${tourId}/assign-guide`, data),
  unassignGuide: (tourId) => api.post(`/dispatch/tours/${tourId}/unassign-guide`)
}
