import { useState, useEffect } from 'react'
import { useParams, Link, useNavigate } from 'react-router-dom'
import { tourApi, registrationApi } from '../api.js'

function TourDetail() {
  const { id } = useParams()
  const navigate = useNavigate()
  const [tour, setTour] = useState(null)
  const [form, setForm] = useState({
    touristName: '',
    touristPhone: '',
    touristEmail: '',
    touristIdCardNumber: '',
    peopleCount: 1,
    remarks: ''
  })
  const [showSuccess, setShowSuccess] = useState(false)
  const [submitting, setSubmitting] = useState(false)

  useEffect(() => {
    loadTour()
  }, [id])

  const loadTour = async () => {
    try {
      const data = await tourApi.getById(id)
      setTour(data)
    } catch (e) {
      navigate('/')
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!form.touristName.trim()) {
      alert('请输入您的姓名')
      return
    }
    if (!form.touristPhone.trim()) {
      alert('请输入联系电话')
      return
    }

    setSubmitting(true)
    try {
      await registrationApi.create({
        tourRouteId: parseInt(id),
        ...form
      })
      setShowSuccess(true)
    } catch (e) {
    } finally {
      setSubmitting(false)
    }
  }

  if (!tour) {
    return <div className="empty-state">加载中...</div>
  }

  const remaining = tour.maxPeople - tour.registeredPeople
  const canBook = tour.status !== 'CANCELLED' && tour.status !== 'COMPLETED' && remaining > 0
  const totalPrice = tour.price * form.peopleCount

  const getStatusText = (status) => {
    switch (status) {
      case 'PENDING': return '招募中'
      case 'CONFIRMED': return '已成团'
      case 'CANCELLED': return '已取消'
      case 'COMPLETED': return '已完成'
      default: return status
    }
  }

  return (
    <div>
      <Link to="/" className="back-link">← 返回线路列表</Link>

      <div className="tour-detail">
        <div className="tour-detail-hero">
          <h2>{tour.name}</h2>
          <div className="info-row">
            <span>📅 出发：{tour.departureDate}</span>
            <span>🔄 返回：{tour.returnDate}</span>
            <span>📍 {tour.departureCity || '出发城市待定'}</span>
            <span className="status-badge">{getStatusText(tour.status)}</span>
          </div>
        </div>

        <div className="tour-detail-body">
          <div className="section">
            <h3>线路信息</h3>
            <div className="info-grid">
              <div className="info-item">
                <label>目的地</label>
                <div className="value">{tour.destination || '-'}</div>
              </div>
              <div className="info-item">
                <label>价格</label>
                <div className="value" style={{ color: '#f5222d' }}>¥{tour.price} / 人</div>
              </div>
              <div className="info-item">
                <label>已报名</label>
                <div className="value">{tour.registeredPeople} 人</div>
              </div>
              <div className="info-item">
                <label>最低成团</label>
                <div className="value">{tour.minPeople} 人</div>
              </div>
              <div className="info-item">
                <label>剩余名额</label>
                <div className="value">{Math.max(remaining, 0)} 人</div>
              </div>
              <div className="info-item">
                <label>导游</label>
                <div className="value">{tour.guideName || '待分配'}</div>
              </div>
              {tour.meetingPoint && (
                <div className="info-item">
                  <label>集合地点</label>
                  <div className="value">{tour.meetingPoint}</div>
                </div>
              )}
              {tour.meetingTime && (
                <div className="info-item">
                  <label>集合时间</label>
                  <div className="value">{tour.meetingTime.substring(0, 16)}</div>
                </div>
              )}
            </div>
          </div>

          <div className="section">
            <h3>线路描述</h3>
            <p style={{ lineHeight: 1.8, color: '#555' }}>{tour.description || '暂无描述'}</p>
          </div>

          <div className="section">
            <h3>详细行程</h3>
            <div className="itinerary-text">{tour.itinerary || '暂无行程信息'}</div>
          </div>

          {canBook && (
            <div className="booking-section">
              <h3>在线报名</h3>
              <form onSubmit={handleSubmit}>
                <div className="form-grid">
                  <div className="form-group">
                    <label>姓名 *</label>
                    <input type="text" value={form.touristName}
                           onChange={e => setForm({ ...form, touristName: e.target.value })}
                           placeholder="请输入您的姓名" />
                  </div>
                  <div className="form-group">
                    <label>手机号 *</label>
                    <input type="tel" value={form.touristPhone}
                           onChange={e => setForm({ ...form, touristPhone: e.target.value })}
                           placeholder="请输入手机号" />
                  </div>
                  <div className="form-group">
                    <label>邮箱</label>
                    <input type="email" value={form.touristEmail}
                           onChange={e => setForm({ ...form, touristEmail: e.target.value })}
                           placeholder="选填" />
                  </div>
                  <div className="form-group">
                    <label>身份证号</label>
                    <input type="text" value={form.touristIdCardNumber}
                           onChange={e => setForm({ ...form, touristIdCardNumber: e.target.value })}
                           placeholder="选填" />
                  </div>
                  <div className="form-group">
                    <label>报名人数</label>
                    <select value={form.peopleCount}
                            onChange={e => setForm({ ...form, peopleCount: parseInt(e.target.value) })}>
                      {Array.from({ length: Math.min(remaining, 10) }, (_, i) => i + 1).map(n => (
                        <option key={n} value={n}>{n} 人</option>
                      ))}
                    </select>
                  </div>
                  <div className="form-group">
                    <label>备注</label>
                    <input type="text" value={form.remarks}
                           onChange={e => setForm({ ...form, remarks: e.target.value })}
                           placeholder="特殊需求等（选填）" />
                  </div>
                </div>
                <div className="booking-footer">
                  <div className="total-price">
                    总价：<span className="amount">¥{totalPrice.toLocaleString()}</span>
                  </div>
                  <button type="submit" className="btn btn-success" disabled={submitting}>
                    {submitting ? '提交中...' : '立即报名'}
                  </button>
                </div>
              </form>
            </div>
          )}

          {!canBook && tour.status === 'CANCELLED' && (
            <div className="empty-state">
              该线路已取消
              <p>请查看其他线路</p>
            </div>
          )}
          {!canBook && tour.status === 'COMPLETED' && (
            <div className="empty-state">
              该线路已完成
              <p>请查看其他线路</p>
            </div>
          )}
          {!canBook && remaining <= 0 && tour.status !== 'CANCELLED' && tour.status !== 'COMPLETED' && (
            <div className="empty-state">
              该线路名额已满
              <p>请查看其他线路</p>
            </div>
          )}
        </div>
      </div>

      {showSuccess && (
        <div className="modal-overlay" onClick={() => { setShowSuccess(false); navigate('/') }}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <div className="success-modal">
              <div className="success-icon">✓</div>
              <h3>报名成功！</h3>
              <p>
                您已成功报名"{tour.name}"<br />
                报名人数：{form.peopleCount} 人<br />
                总价：¥{totalPrice.toLocaleString()}<br />
                <br />
                {tour.status === 'CONFIRMED' ? (
                  <>该线路已成团，我们会尽快与您联系确认行程细节。</>
                ) : (
                  <>我们会持续跟进报名进度，凑够 {tour.minPeople} 人自动成团后将通知您。</>
                )}
              </p>
              <div className="form-actions">
                <button className="btn btn-primary" onClick={() => { setShowSuccess(false); navigate('/') }}>
                  返回线路列表
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TourDetail
