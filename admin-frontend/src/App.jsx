import { BrowserRouter, Routes, Route, NavLink } from 'react-router-dom'
import Dashboard from './pages/Dashboard.jsx'
import Tours from './pages/Tours.jsx'
import Guides from './pages/Guides.jsx'
import Dispatch from './pages/Dispatch.jsx'

function App() {
  return (
    <BrowserRouter>
      <div className="app">
        <aside className="sidebar">
          <h1>旅行社管理系统</h1>
          <nav>
            <NavLink to="/" end>数据概览</NavLink>
            <NavLink to="/tours">线路管理</NavLink>
            <NavLink to="/guides">导游管理</NavLink>
            <NavLink to="/dispatch">调度派团</NavLink>
          </nav>
        </aside>
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/tours" element={<Tours />} />
            <Route path="/guides" element={<Guides />} />
            <Route path="/dispatch" element={<Dispatch />} />
          </Routes>
        </main>
      </div>
    </BrowserRouter>
  )
}

export default App
