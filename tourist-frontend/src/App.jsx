import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import Home from './pages/Home.jsx'
import TourDetail from './pages/TourDetail.jsx'

function App() {
  return (
    <BrowserRouter>
      <div>
        <header className="header">
          <div className="header-inner">
            <h1>🌍 旅途旅行社</h1>
            <nav>
              <NavLink to="/" end>全部线路</NavLink>
            </nav>
          </div>
        </header>
        <div className="container">
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/tour/:id" element={<TourDetail />} />
          </Routes>
        </div>
      </div>
    </BrowserRouter>
  )
}

export default App
