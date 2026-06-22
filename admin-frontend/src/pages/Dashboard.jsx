import { useState, useEffect } from 'react'
import { tourApi, guideApi, dispatchApi } from '../api.js'

function Dashboard() {
  const [stats, setStats] = useState({
    totalTours: 0,
    pendingTours: 0,
    confirmedTours: 0,
    totalGuides: 0,
    toursNeedingGuide: 0
  })
  const [tours, setTours] = useState([])

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    const [allTours, guides, needingGuide] = await Promise.all([
      tourApi.getAll(),
      guideApi.getAll({ active: true }),
      dispatchApi.getToursNeedingGuide()
    ])
    setTours(allTours)
    setStats({
      totalTours: allTours.length,
      pendingTours: allTours.filter(t => t.status === 'PENDING').length,
      confirmedTours: allTours.filter(t => t.status === 'CONFIRMED').length,
      totalGuides: guides.length,
      toursNeedingGuide: needingGuide.length
    })
  }

  return (
    <div>
      <div className="page-header">
        <h2>数据概览</h2>
      </div>

      <div className="stats-grid">
        <div className="stat-card">
          <h3>线路总数</h3>
          <div className="stat-value">{stats.totalTours}</div>
        </div>
        <div className="stat-card">
          <h3>待成团线路</h3>
          <div className="stat-value warning">{stats.pendingTours}</div>
        </div>
        <div className="stat-card">
          <h3>已成团线路</h3>
          <div className="stat-value success">{stats.confirmedTours}</div>
        </div>
        <div className="stat-card">
          <h3>在岗导游</h3>
          <div className="stat-value">{stats.totalGuides}</div>
        </div>
        <div className="stat-card">
          <h3>需派导游线路</h3>
          <div className="stat-value warning">{stats.toursNeedingGuide}</div>
        </div>
      </div>

      <div className="card">
        <h3 style={{ marginBottom: 16 }}>线路报名进度</h3>
        {tours.length === 0 ? (
          <div className="empty-state">暂无线路数据</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>线路名称</th>
                <th>出发日期</th>
                <th>状态</th>
                <th>报名进度</th>
                <th>已分配导游</th>
              </tr>
            </thead>
            <tbody>
              {tours.map(tour => {
                const progress = (tour.registeredPeople / tour.minPeople) * 100
                const progressClass = progress >= 100 ? 'success' : progress >= 70 ? '' : 'warning'
                return (
                  <tr key={tour.id}>
                    <td>{tour.name}</td>
                    <td>{tour.departureDate}</td>
                    <td>
                      <span className={`status-badge status-${tour.status.toLowerCase()}`}>
                        {tour.status === 'PENDING' ? '待成团' :
                         tour.status === 'CONFIRMED' ? '已成团' :
                         tour.status === 'CANCELLED' ? '已取消' : '已完成'}
                      </span>
                    </td>
                    <td style={{ minWidth: 200 }}>
                      {tour.registeredPeople} / {tour.minPeople} 人成团
                      <div className="progress-bar">
                        <div className={`progress-fill ${progressClass}`} style={{ width: `${Math.min(progress, 100)}%` }}></div>
                      </div>
                    </td>
                    <td>{tour.guideName || <span style={{ color: '#fa8c16' }}>未分配</span>}</td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  )
}

export default Dashboard
