import React, { useEffect, useMemo, useState } from 'react'
import type { FormEvent } from 'react'
import { createRoot } from 'react-dom/client'
import './style.css'

type Role = 'ADMIN' | 'TECHNICIAN' | 'CLIENT'
type Status = 'OPEN' | 'ASSIGNED' | 'IN_PROGRESS' | 'RESOLVED' | 'CLOSED'
type Priority = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL'

type User = {
  id: number
  name: string
  email: string
  role: Role
}

type Comment = {
  id: number
  message: string
  author: User
  createdAt: string
}

type ServiceOrder = {
  id: number
  title: string
  description: string
  priority: Priority
  status: Status
  client: User
  technician: User | null
  createdAt: string
  comments: Comment[]
}

type Dashboard = {
  total: number
  open: number
  assigned: number
  inProgress: number
  resolved: number
  closed: number
}

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080'

function App() {
  const [token, setToken] = useState(localStorage.getItem('token') ?? '')
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem('user')
    return stored ? JSON.parse(stored) : null
  })
  const [email, setEmail] = useState('admin@demo.com')
  const [password, setPassword] = useState('123456')
  const [orders, setOrders] = useState<ServiceOrder[]>([])
  const [selectedOrder, setSelectedOrder] = useState<ServiceOrder | null>(null)
  const [technicians, setTechnicians] = useState<User[]>([])
  const [dashboard, setDashboard] = useState<Dashboard | null>(null)
  const [message, setMessage] = useState('')
  const [newOrder, setNewOrder] = useState({ title: '', description: '', priority: 'MEDIUM' as Priority })
  const [comment, setComment] = useState('')

  const authHeaders = useMemo(() => ({
    'Content-Type': 'application/json',
    Authorization: `Bearer ${token}`,
  }), [token])

  async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(`${API_URL}${path}`, options)
    if (!response.ok) {
      const body = await response.json().catch(() => ({ error: 'Erro inesperado.' }))
      throw new Error(body.error ?? 'Erro inesperado.')
    }
    return response.json()
  }

  async function login(event?: FormEvent) {
    event?.preventDefault()
    try {
      const data = await request<{ token: string; user: User }>('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      })
      localStorage.setItem('token', data.token)
      localStorage.setItem('user', JSON.stringify(data.user))
      setToken(data.token)
      setUser(data.user)
      setMessage(`Login realizado como ${data.user.name}.`)
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Erro ao fazer login.')
    }
  }

  function logout() {
    localStorage.clear()
    setToken('')
    setUser(null)
    setOrders([])
    setSelectedOrder(null)
    setDashboard(null)
  }

  async function loadData() {
    if (!token) return
    const [ordersData, dashboardData] = await Promise.all([
      request<ServiceOrder[]>('/api/orders', { headers: authHeaders }),
      request<Dashboard>('/api/dashboard', { headers: authHeaders }),
    ])
    setOrders(ordersData)
    setDashboard(dashboardData)
    setSelectedOrder((current) => current ? ordersData.find((order) => order.id === current.id) ?? ordersData[0] ?? null : ordersData[0] ?? null)
    if (user?.role !== 'CLIENT') {
      const techniciansData = await request<User[]>('/api/users/technicians', { headers: authHeaders })
      setTechnicians(techniciansData)
    }
  }

  async function createOrder(event: FormEvent) {
    event.preventDefault()
    try {
      await request<ServiceOrder>('/api/orders', {
        method: 'POST',
        headers: authHeaders,
        body: JSON.stringify(newOrder),
      })
      setNewOrder({ title: '', description: '', priority: 'MEDIUM' })
      setMessage('Ordem de servico criada.')
      await loadData()
    } catch (error) {
      setMessage(error instanceof Error ? error.message : 'Erro ao criar ordem.')
    }
  }

  async function updateStatus(status: Status) {
    if (!selectedOrder) return
    await request<ServiceOrder>(`/api/orders/${selectedOrder.id}/status`, {
      method: 'PATCH',
      headers: authHeaders,
      body: JSON.stringify({ status }),
    })
    await loadData()
  }

  async function assignOrder(technicianId: string) {
    if (!selectedOrder || !technicianId) return
    await request<ServiceOrder>(`/api/orders/${selectedOrder.id}/assign`, {
      method: 'PATCH',
      headers: authHeaders,
      body: JSON.stringify({ technicianId: Number(technicianId) }),
    })
    await loadData()
  }

  async function addComment(event: FormEvent) {
    event.preventDefault()
    if (!selectedOrder || !comment.trim()) return
    await request<ServiceOrder>(`/api/orders/${selectedOrder.id}/comments`, {
      method: 'POST',
      headers: authHeaders,
      body: JSON.stringify({ message: comment }),
    })
    setComment('')
    await loadData()
  }

  useEffect(() => {
    loadData().catch((error) => setMessage(error.message))
  }, [token])

  if (!user) {
    return (
      <main className="login-page">
        <section className="login-panel">
          <div>
            <span className="eyebrow">Fullstack Java</span>
            <h1>Service Order Manager</h1>
            <p>Sistema de gestao de ordens de servico com Spring Boot, JWT, perfis de acesso e React.</p>
          </div>
          <form onSubmit={login} className="login-form">
            <label>Email<input value={email} onChange={(event) => setEmail(event.target.value)} /></label>
            <label>Senha<input type="password" value={password} onChange={(event) => setPassword(event.target.value)} /></label>
            <button type="submit">Entrar</button>
          </form>
          <div className="demo-users">
            <button onClick={() => { setEmail('admin@demo.com'); setPassword('123456') }}>Admin</button>
            <button onClick={() => { setEmail('tecnico@demo.com'); setPassword('123456') }}>Tecnico</button>
            <button onClick={() => { setEmail('cliente@demo.com'); setPassword('123456') }}>Cliente</button>
          </div>
          {message && <p className="feedback">{message}</p>}
        </section>
      </main>
    )
  }

  return (
    <main className="app-shell">
      <aside className="sidebar">
        <div>
          <span className="eyebrow">Ordens de servico</span>
          <h1>Service Manager</h1>
        </div>
        <div className="profile">
          <strong>{user.name}</strong>
          <span>{user.role}</span>
        </div>
        <button className="secondary" onClick={logout}>Sair</button>
      </aside>

      <section className="workspace">
        <header className="topbar">
          <div>
            <h2>Dashboard operacional</h2>
            <p>Chamados, responsaveis, status e historico em um fluxo fullstack.</p>
          </div>
          <button onClick={() => loadData()}>Atualizar</button>
        </header>

        <section className="metrics">
          {dashboard && Object.entries(dashboard).map(([key, value]) => (
            <article className="metric" key={key}>
              <span>{key}</span>
              <strong>{value}</strong>
            </article>
          ))}
        </section>

        {user.role === 'CLIENT' && (
          <form className="create-form" onSubmit={createOrder}>
            <input placeholder="Titulo" value={newOrder.title} onChange={(event) => setNewOrder({ ...newOrder, title: event.target.value })} />
            <select value={newOrder.priority} onChange={(event) => setNewOrder({ ...newOrder, priority: event.target.value as Priority })}>
              <option value="LOW">Baixa</option>
              <option value="MEDIUM">Media</option>
              <option value="HIGH">Alta</option>
              <option value="CRITICAL">Critica</option>
            </select>
            <textarea placeholder="Descreva o problema" value={newOrder.description} onChange={(event) => setNewOrder({ ...newOrder, description: event.target.value })} />
            <button type="submit">Abrir chamado</button>
          </form>
        )}

        {message && <p className="feedback">{message}</p>}

        <section className="content-grid">
          <div className="orders-list">
            {orders.map((order) => (
              <button className={selectedOrder?.id === order.id ? 'order-item active' : 'order-item'} key={order.id} onClick={() => setSelectedOrder(order)}>
                <span>{order.priority}</span>
                <strong>{order.title}</strong>
                <small>{order.status}</small>
              </button>
            ))}
          </div>

          {selectedOrder && (
            <article className="details">
              <div className="details-header">
                <div>
                  <span className="status">{selectedOrder.status}</span>
                  <h2>{selectedOrder.title}</h2>
                  <p>{selectedOrder.description}</p>
                </div>
              </div>

              <div className="info-grid">
                <span>Cliente: <strong>{selectedOrder.client.name}</strong></span>
                <span>Tecnico: <strong>{selectedOrder.technician?.name ?? 'Nao atribuido'}</strong></span>
                <span>Prioridade: <strong>{selectedOrder.priority}</strong></span>
              </div>

              {user.role === 'ADMIN' && (
                <select onChange={(event) => assignOrder(event.target.value)} defaultValue="">
                  <option value="">Atribuir tecnico</option>
                  {technicians.map((technician) => <option key={technician.id} value={technician.id}>{technician.name}</option>)}
                </select>
              )}

              {user.role !== 'CLIENT' && (
                <div className="status-actions">
                  {(['ASSIGNED', 'IN_PROGRESS', 'RESOLVED', 'CLOSED'] as Status[]).map((status) => (
                    <button key={status} onClick={() => updateStatus(status)}>{status}</button>
                  ))}
                </div>
              )}

              <form className="comment-form" onSubmit={addComment}>
                <input placeholder="Adicionar comentario" value={comment} onChange={(event) => setComment(event.target.value)} />
                <button type="submit">Enviar</button>
              </form>

              <div className="timeline">
                {selectedOrder.comments.map((item) => (
                  <div className="timeline-item" key={item.id}>
                    <strong>{item.author.name}</strong>
                    <p>{item.message}</p>
                    <small>{new Date(item.createdAt).toLocaleString('pt-BR')}</small>
                  </div>
                ))}
              </div>
            </article>
          )}
        </section>
      </section>
    </main>
  )
}

createRoot(document.getElementById('app')!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
)
