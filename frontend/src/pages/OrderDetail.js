import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { client } from '../api/client';
import Loader from '../components/Loader';
import './OrderDetail.css';

export default function OrderDetail() {
  const { id } = useParams();
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const { data } = await client.get(`/api/orders/${id}`);
        setOrder(data);
      } catch (e) {
        setError(e.response?.data?.message || 'Order not found');
      } finally {
        setLoading(false);
      }
    })();
  }, [id]);

  if (loading) return <Loader />;
  if (error || !order) {
    return (
      <div className="page order-detail">
        <p>{error || 'Not found'}</p>
        <Link to="/">Home</Link>
      </div>
    );
  }

  return (
    <div className="page order-detail">
      <Link to="/" className="back-link">
        ← Back to shop
      </Link>
      <h1>Order #{order.id}</h1>
      <p className="order-meta">
        Status: <strong>{order.status}</strong>
        {' · '}
        {new Date(order.createdAt).toLocaleString()}
      </p>
      <ul className="order-items">
        {order.items.map((i) => (
          <li key={`${i.productId}-${i.quantity}`}>
            <span>
              {i.productName} × {i.quantity}
            </span>
            <span>
              {(Number(i.price) * i.quantity).toLocaleString(undefined, {
                style: 'currency',
                currency: 'USD',
              })}
            </span>
          </li>
        ))}
      </ul>
      <p className="order-total">
        Total:{' '}
        <strong>
          {Number(order.totalAmount).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
        </strong>
      </p>
    </div>
  );
}
