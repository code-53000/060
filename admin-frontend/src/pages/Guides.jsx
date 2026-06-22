import { useState, useEffect } from 'react'
import { guideApi } from '../api.js'

function Guides() {
  const [guides, setGuides] = useState([])
  const [showModal, setShowModal] = useState(false)
  const [editingGuide, setEditingGuide] = useState(null)
  const [form, setForm] = useState({
    name: '', phone: '', email: '',
    languages: '', licenseNumber: '', notes: '', active: true
  })

  useEffect(() => {
    loadGuides()
  }, [])

  const loadGuides = async () => {
    const data = await guideApi.getAll()
    setGuides(data)
  }

  const openCreateModal = () => {
    setEditingGuide(null)
    setForm({ name: '', phone: '', email: '', languages: '', licenseNumber: '', notes: '', active: true })
    setShowModal(true)
  }

  const openEditModal = (guide) => {
    setEditingGuide(guide)
    setForm({
      name: guide.name,
      phone: guide.phone || '',
      email: guide.email || '',
      languages: (guide.languages || []).join(', '),
      licenseNumber: guide.licenseNumber || '',
      notes: guide.notes || '',
      active: guide.active
    })
    setShowModal(true)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const data = {
      ...form,
      languages: form.languages.split(',').map(s => s.trim()).filter(s => s),
      active: form.active
    }
    if (editingGuide) {
      await guideApi.update(editingGuide.id, data)
    } else {
      await guideApi.create(data)
    }
    setShowModal(false)
    loadGuides()
  }

  const handleDelete = async (guide) => {
    if (confirm(`确定删除导游"${guide.name}"吗？`)) {
      try {
        await guideApi.delete(guide.id)
        loadGuides()
      } catch (e) {}
    }
  }

  return (
    <div>
      <div className="page-header">
        <h2>导游管理</h2>
        <button className="btn btn-primary" onClick={openCreateModal}>添加导游</button>
      </div>

      <div className="card">
        {guides.length === 0 ? (
          <div className="empty-state">暂无导游数据</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>姓名</th>
                <th>语种</th>
                <th>联系电话</th>
                <th>邮箱</th>
                <th>导游证号</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {guides.map(guide => (
                <tr key={guide.id}>
                  <td>{guide.name}</td>
                  <td>
                    {(guide.languages || []).map(lang => (
                      <span key={lang} className="tag">{lang}</span>
                    ))}
                  </td>
                  <td>{guide.phone || '-'}</td>
                  <td>{guide.email || '-'}</td>
                  <td>{guide.licenseNumber || '-'}</td>
                  <td>
                    <span className={`status-badge ${guide.active ? 'status-confirmed' : 'status-cancelled'}`}>
                      {guide.active ? '在岗' : '停用'}
                    </span>
                  </td>
                  <td>
                    <button className="btn btn-sm btn-primary" onClick={() => openEditModal(guide)}>编辑</button>
                    {' '}
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(guide)}>删除</button>
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
            <h3>{editingGuide ? '编辑导游' : '添加导游'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>姓名 *</label>
                <input type="text" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
              </div>
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12 }}>
                <div className="form-group">
                  <label>联系电话</label>
                  <input type="text" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} />
                </div>
                <div className="form-group">
                  <label>邮箱</label>
                  <input type="email" value={form.email} onChange={e => setForm({ ...form, email: e.target.value })} />
                </div>
              </div>
              <div className="form-group">
                <label>语种（用逗号分隔，如：中文, 英语）</label>
                <input type="text" value={form.languages} onChange={e => setForm({ ...form, languages: e.target.value })} placeholder="中文, 英语" />
              </div>
              <div className="form-group">
                <label>导游证号</label>
                <input type="text" value={form.licenseNumber} onChange={e => setForm({ ...form, licenseNumber: e.target.value })} />
              </div>
              <div className="form-group">
                <label>备注</label>
                <textarea value={form.notes} onChange={e => setForm({ ...form, notes: e.target.value })} />
              </div>
              <div className="form-group">
                <label>
                  <input type="checkbox" checked={form.active} onChange={e => setForm({ ...form, active: e.target.checked })} />
                  {' '}在岗
                </label>
              </div>
              <div className="form-actions">
                <button type="button" className="btn btn-default" onClick={() => setShowModal(false)}>取消</button>
                <button type="submit" className="btn btn-primary">保存</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  )
}

export default Guides
