import { useState, useEffect } from 'react'
import { tourApi, registrationApi } from '../api.js'

function Tours() {
  const [tours, setTours] = useState([])
  const [statusFilter, setStatusFilter] = useState('')
  const [showModal, setShowModal] = useState(false)
  const [editingTour, setEditingTour] = useState(null)
  const [showRegistrations, setShowRegistrations] = useState(null)
  const [registrations, setRegistrations] = useState([])
  const [form, setForm] = useState({
    name: '',
    description: '',
    itinerary: '',
    departureDate: '',
    returnDate: '',
    price: '',
    minPeople: '',
    maxPeople: '',
    departureCity: '',
    destination: '',
    meetingPoint: ''
  })

  useEffect(() => {
    loadTours()
  }, [statusFilter])

  const loadTours = async () => {
    const data = await tourApi.getAll(statusFilter || undefined)
    setTours(data)
  }

  const openCreateModal = () => {
    setEditingTour(null)
    setForm({
      name: '', description: '', itinerary: '',
      departureDate: '', returnDate: '', price: '',
      minPeople: '', maxPeople: '', departureCity: '',
      destination: '', meetingPoint: ''
    })
    setShowModal(true)
  }

  const openEditModal = (tour) => {
    setEditingTour(tour)
    setForm({
      name: tour.name,
      description: tour.description || '',
      itinerary: tour.itinerary || '',
      departureDate: tour.departureDate,
      returnDate: tour.returnDate,
      price: tour.price,
      minPeople: tour.minPeople,
      maxPeople: tour.maxPeople,
      departureCity: tour.departureCity || '',
      destination: tour.destination || '',
      meetingPoint: tour.meetingPoint || ''
    })
    setShowModal(true)
  }

  const openRegistrations = async (tour) => {
    setShowRegistrations(tour)
    const data = await registrationApi.getByTour(tour.id)
    setRegistrations(data)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const data = { ...form, price: parseFloat(form.price), minPeople: parseInt(form.minPeople), maxPeople: parseInt(form.maxPeople) }
    if (editingTour) {
      await tourApi.update(editingTour.id, data)
    } else {
      await tourApi.create(data)
    }
    setShowModal(false)
    loadTours()
  }

  const handleDelete = async (tour) => {
    if (confirm(`确定删除线路"${tour.name}"吗？`)) {
      await tourApi.delete(tour.id)
      loadTours()
    }
  }

  const handleCancel = async (tour) => {
    if (confirm(`确定取消线路"${tour.name}"吗？`)) {
      await tourApi.cancel(tour.id)
      loadTours()
    }
  }

  const confirmRegistration = async (id) => {
    if (confirm('确定确认该报名吗？')) {
      await registrationApi.confirm(id)
      const data = await registrationApi.getByTour(showRegistrations.id)
      setRegistrations(data)
      loadTours()
    }
  }

  const cancelRegistration = async (id) => {
    if (confirm('确定取消该报名吗？')) {
      await registrationApi.cancel(id)
      const data = await registrationApi.getByTour(showRegistrations.id)
      setRegistrations(data)
      loadTours()
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>线路管理</h2>
        <div>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
            style={{ marginRight: 10, padding: '8px 12px', borderRadius: 4, border: '1px solid #d9d9d9' }}
          >
            <option value="">全部状态</option>
            <option value="PENDING">待成团</option>
            <option value="CONFIRMED">已成团</option>
            <option value="CANCELLED">已取消</option>
            <option value="COMPLETED">已完成</option>
          </select>
          <button className="btn btn-primary" onClick={openCreateModal}>新建线路</button>
        </div>
      </div>

      <div className="card">
        {tours.length === 0 ? (
          <div className="empty-state">暂无线路数据</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>线路名称</th>
                <th>目的地</th>
                <th>出发日期</th>
                <th>价格</th>
                <th>成团进度</th>
                <th>状态</th>
                <th>导游</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {tours.map(tour => (
                <tr key={tour.id}>
                  <td>{tour.name}</td>
                  <td>{tour.destination}</td>
                  <td>{tour.departureDate}</td>
                  <td>¥{tour.price}</td>
                  <td>
                    {tour.registeredPeople}/{tour.minPeople}人
                    <div className="progress-bar">
                      <div className={`progress-fill ${tour.registeredPeople >= tour.minPeople ? 'success' : ''}`}
                           style={{ width: `${Math.min((tour.registeredPeople / tour.minPeople) * 100, 100)}%` }}></div>
                    </div>
                  </td>
                  <td>
                    <span className={`status-badge status-${tour.status.toLowerCase()}`}>
                      {tour.status === 'PENDING' ? '待成团' :
                       tour.status === 'CONFIRMED' ? '已成团' :
                       tour.status === 'CANCELLED' ? '已取消' : '已完成'}
                    </span>
                  </td>
                  <td>{tour.guideName || '-'}</td>
                  <td>
                    <button className="btn btn-sm btn-default" onClick={() => openRegistrations(tour)}>报名名单</button>
                    {' '}
                    <button className="btn btn-sm btn-primary" onClick={() => openEditModal(tour)}>编辑</button>
                    {' '}
                    {tour.status !== 'CONFIRMED' && tour.status !== 'COMPLETED' && (
                      <>
                        <button className="btn btn-sm btn-danger" onClick={() => handleDelete(tour)}>删除</button>
                        {' '}
                      </>
                    )}
                    {tour.status !== 'COMPLETED' && tour.status !== 'CANCELLED' && (
                      <button className="btn btn-sm btn-danger" onClick={() => handleCancel(tour)}>取消</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {showModal && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>{editingTour ? '编辑线路' : '新建线路'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>线路名称 *</label>
                <input type="text" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label>出发城市</label>
                  <input type="text" value={form.departureCity} onChange={e => setForm({ ...form, departureCity: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>目的地</label>
                  <input type="text" value={form.destination} onChange={e => setForm({ ...form, destination: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>出发日期 *</label>
                  <input type="date" value={form.departureDate} onChange={e => setForm({ ...form, departureDate: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>返回日期 *</label>
                  <input type="date" value={form.returnDate} onChange={e => setForm({ ...form, returnDate: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>价格(元) *</label>
                  <input type="number" step="0.01" value={form.price} onChange={e => setForm({ ...form, price: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>集合地点</label>
                  <input type="text" value={form.meetingPoint} onChange={e => setForm({ ...form, meetingPoint: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>最低成团人数 *</label>
                  <input type="number" value={form.minPeople} onChange={e => setForm({ ...form, minPeople: e.target.value })} required />
                </div>
                <div className="form-group">
                  <label>最大人数 *</label>
                  <input type="number" value={form.maxPeople} onChange={e => setForm({ ...form, maxPeople: e.target.value })} required />
                </div>
              </div>
              <div className="form-group">
                <label>线路描述</label>
                <textarea value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
              </div>
              <div className="form-group">
                <label>详细行程</label>
                <textarea value={form.itinerary} onChange={e => setForm({ ...form, itinerary: e.target.value })} />
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn-default" onClick={() => setShowModal(false)}>取消</button>
                <button type="submit" className="btn btn-primary">保存</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showRegistrations && (
        <div className="modal-overlay" onClick={() => setShowRegistrations(null)}>
          <div className="modal" style={{ maxWidth: 800 }} onClick={e => e.stopPropagation()}>
            <h3>报名名单 - {showRegistrations.name}</h3>
            {registrations.length === 0 ? (
              <div className="empty-state">暂无报名记录</div>
            ) : (
              <table>
                <thead>
                  <tr>
                    <th>游客姓名</th>
                    <th>手机号</th>
                    <th>报名人数</th>
                    <th>总价</th>
                    <th>状态</th>
                    <th>报名时间</th>
                    <th>操作</th>
                  </tr>
                </thead>
                <tbody>
                  {registrations.map(reg => (
                    <tr key={reg.id}>
                      <td>{reg.touristName}</td>
                      <td>{reg.touristPhone || '-'}</td>
                      <td>{reg.peopleCount}</td>
                      <td>¥{reg.totalPrice}</td>
                      <td>
                        <span className={`status-badge status-${reg.status === 'CONFIRMED' ? 'confirmed' : reg.status === 'CANCELLED' ? 'cancelled' : 'pending'}`}>
                          {reg.status === 'PENDING' ? '待确认' :
                           reg.status === 'CONFIRMED' ? '已确认' :
                           reg.status === 'CANCELLED' ? '已取消' : '已退款'}
                        </span>
                      </td>
                      <td>{reg.createdAt?.substring(0, 16)}</td>
                      <td>
                        {reg.status === 'PENDING' && (
                          <button className="btn btn-sm btn-success" onClick={() => confirmRegistration(reg.id)}>确认</button>
                        )}
                        {' '}
                        {(reg.status === 'PENDING' || reg.status === 'CONFIRMED') && (
                          <button className="btn btn-sm btn-danger" onClick={() => cancelRegistration(reg.id)}>取消</button>
                        )}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
            <div className="form-actions">
              <button className="btn btn-default" onClick={() => setShowRegistrations(null)}>关闭</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Tours
