import { useState, useEffect } from 'react'
import { dispatchApi, tourApi } from '../api.js'

function Dispatch() {
  const [activeTab, setActiveTab] = useState('needing')
  const [toursNeedingGuide, setToursNeedingGuide] = useState([])
  const [allTours, setAllTours] = useState([])
  const [assigningTour, setAssigningTour] = useState(null)
  const [availableGuides, setAvailableGuides] = useState([])
  const [selectedGuide, setSelectedGuide] = useState('')
  const [meetingPoint, setMeetingPoint] = useState('')

  useEffect(() => {
    loadData()
  }, [activeTab])

  const loadData = async () => {
    if (activeTab === 'needing') {
      const data = await dispatchApi.getToursNeedingGuide()
      setToursNeedingGuide(data)
    } else {
      const data = await tourApi.getAll()
      setAllTours(data)
    }
  }

  const openAssignModal = async (tour) => {
    setAssigningTour(tour)
    setSelectedGuide('')
    setMeetingPoint(tour.meetingPoint || '')
    const guides = await dispatchApi.getAvailableGuides(tour.id)
    setAvailableGuides(guides)
  }

  const handleAssign = async () => {
    if (!selectedGuide) {
      alert('请选择导游')
      return
    }
    await dispatchApi.assignGuide(assigningTour.id, {
      guideId: parseInt(selectedGuide),
      meetingPoint
    })
    setAssigningTour(null)
    loadData()
  }

  const handleUnassign = async (tour) => {
    if (confirm(`确定取消导游"${tour.guideName}"的分配吗？`)) {
      await dispatchApi.unassignGuide(tour.id)
      loadData()
    }
  }

  const tours = activeTab === 'needing' ? toursNeedingGuide : allTours

  return (
    <div>
      <div className="page-header">
        <h2>调度派团</h2>
      </div>

      <div className="card">
        <div className="tabs">
          <div className={`tab ${activeTab === 'needing' ? 'active' : ''}`} onClick={() => setActiveTab('needing')}>
            需派导游 ({toursNeedingGuide.length})
          </div>
          <div className={`tab ${activeTab === 'all' ? 'active' : ''}`} onClick={() => setActiveTab('all')}>
            全部线路
          </div>
        </div>

        {tours.length === 0 ? (
          <div className="empty-state">暂无数据</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>线路名称</th>
                <th>目的地</th>
                <th>出团日期</th>
                <th>报名进度</th>
                <th>状态</th>
                <th>已派导游</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {tours.map(tour => (
                <tr key={tour.id}>
                  <td>{tour.name}</td>
                  <td>{tour.destination}</td>
                  <td>{tour.departureDate} ~ {tour.returnDate}</td>
                  <td>{tour.registeredPeople}/{tour.minPeople}人</td>
                  <td>
                    <span className={`status-badge status-${tour.status.toLowerCase()}`}>
                      {tour.status === 'PENDING' ? '待成团' :
                       tour.status === 'CONFIRMED' ? '已成团' :
                       tour.status === 'CANCELLED' ? '已取消' : '已完成'}
                    </span>
                  </td>
                  <td>
                    {tour.guideName ? (
                      <span style={{ color: '#52c41a', fontWeight: 500 }}>{tour.guideName}</span>
                    ) : (
                      <span style={{ color: '#fa8c16' }}>未分配</span>
                    )}
                  </td>
                  <td>
                    {tour.guideId ? (
                      <button className="btn btn-sm btn-danger" onClick={() => handleUnassign(tour)}>取消派团</button>
                    ) : (
                      tour.status !== 'CANCELLED' && tour.status !== 'COMPLETED' && (
                        <button className="btn btn-sm btn-primary" onClick={() => openAssignModal(tour)}>派导游</button>
                      )
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {assigningTour && (
        <div className="modal-overlay" onClick={() => setAssigningTour(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>派导游 - {assigningTour.name}</h3>
            <div style={{ marginBottom: 16, padding: 12, background: '#f9f9f9', borderRadius: 4 }}>
              <div>出团时间：{assigningTour.departureDate} ~ {assigningTour.returnDate}</div>
              <div>目的地：{assigningTour.destination}</div>
            </div>
            <div className="form-group">
              <label>选择导游 *</label>
              {availableGuides.length === 0 ? (
                <div style={{ color: '#fa8c16', padding: '8px 0' }}>
                  该时段暂无空闲导游
                </div>
              ) : (
                <select value={selectedGuide} onChange={e => setSelectedGuide(e.target.value)}>
                  <option value="">请选择导游</option>
                  {availableGuides.map(guide => (
                    <option key={guide.id} value={guide.id}>
                      {guide.name} - {(guide.languages || []).join('/')}
                    </option>
                  ))}
                </select>
              )}
            </div>
            <div className="form-group">
              <label>集合地点</label>
              <input type="text" value={meetingPoint} onChange={e => setMeetingPoint(e.target.value)} placeholder="如：浦东机场T2航站楼" />
            </div>
            <div className="form-actions">
              <button className="btn btn-default" onClick={() => setAssigningTour(null)}>取消</button>
              <button className="btn btn-primary" onClick={handleAssign} disabled={availableGuides.length === 0}>确认派团</button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default Dispatch
