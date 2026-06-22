import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { tourApi } from '../api.js'

function Home() {
  const [tours, setTours] = useState([])
  const [filteredTours, setFilteredTours] = useState([])
  const [destinations, setDestinations] = useState([])
  const [selectedDest, setSelectedDest] = useState('')
  const navigate = useNavigate()

  useEffect(() => {
    loadTours()
  }, [])

  useEffect(() => {
    if (selectedDest) {
      setFilteredTours(tours.filter(t => t.destination === selectedDest))
    } else {
      setFilteredTours(tours)
    }
  }, [selectedDest, tours])

  const loadTours = async () => {
    const data = await tourApi.getAll()
    const available = data.filter(t => t.status !== 'CANCELLED' && t.status !== 'COMPLETED')
    setTours(available)
    setFilteredTours(available)
    const dests = [...new Set(available.map(t => t.destination).filter(Boolean))]
    setDestinations(dests)
  }

  const getProgressClass = (registered, min) => {
    const percent = (registered / min) * 100
    return percent >= 100 ? 'success' : percent >= 70 ? '' : 'warning'
  }

  const getStatusText = (status) => {
    switch (status) {
      case 'PENDING': return '招募中'
      case 'CONFIRMED': return '已成团'
      default: return status
    }
  }

  return (
    <div>
      <div className="hero">
        <h2>发现精彩世界</h2>
        <p>精选优质线路，专业导游带团，让每一次旅行都成为美好回忆。在线报名，凑够人数自动成团！</p>
      </div>

      <h2 className="page-title">热门线路</h2>

      <div className="filter-bar">
        <select value={selectedDest} onChange={e => setSelectedDest(e.target.value)}>
          <option value="">全部目的地</option>
          {destinations.map(d => (
            <option key={d} value={d}>{d}</option>
          ))}
        </select>
      </div>

      {filteredTours.length === 0 ? (
        <div className="empty-state">
          暂无线路
          <p>请稍后再来查看新线路</p>
        </div>
      ) : (
        <div className="tour-grid">
          {filteredTours.map(tour => {
            const remaining = tour.maxPeople - tour.registeredPeople
            const disabled = tour.status === 'CANCELLED' || remaining <= 0
            return (
              <div key={tour.id} className="tour-card" onClick={() => !disabled && navigate(`/tour/${tour.id}`)}>
                <div className={`tour-card-header destination-${tour.destination || ''}`}>
                  <span className="destination-tag">{tour.destination || '旅游'}</span>
                  <span className="status-badge">{getStatusText(tour.status)}</span>
                  <div style={{ position: 'absolute', bottom: 16, right: 16, fontSize: 28 }}>
                    {tour.destination === '日本' && '🗼'}
                    {tour.destination === '泰国' && '🏝️'}
                    {tour.destination === '韩国' && '🏯'}
                    {tour.destination === '欧洲' && '🏰'}
                    {tour.destination === '云南' && '🏔️'}
                    {tour.destination === '三亚' && '🌴'}
                    {!['日本', '泰国', '韩国', '欧洲', '云南', '三亚'].includes(tour.destination) && '✈️'}
                  </div>
                </div>
                <div className="tour-card-body">
                  <h3>{tour.name}</h3>
                  <p className="desc">{tour.description}</p>
                  <div className="tour-meta">
                    <span className="date">📅 {tour.departureDate}</span>
                    <span>余位 {Math.max(remaining, 0)} 个</span>
                  </div>
                  <div className="tour-footer">
                    <div className="progress-section">
                      <div className="progress-text">{tour.registeredPeople}/{tour.minPeople} 人成团</div>
                      <div className="progress-bar">
                        <div className={`progress-fill ${getProgressClass(tour.registeredPeople, tour.minPeople)}`}
                             style={{ width: `${Math.min((tour.registeredPeople / tour.minPeople) * 100, 100)}%` }}></div>
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div className="price">¥{tour.price}<small>/人</small></div>
                    </div>
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      )}
    </div>
  )
}

export default Home
