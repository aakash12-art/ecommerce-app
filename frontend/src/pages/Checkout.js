import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { client } from '../api/client';
import Loader from '../components/Loader';
import './Checkout.css';

export default function Checkout() {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    (async () => {
      try {
        const { data } = await client.get('/api/cart');
        setCart(data);
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const placeOrder = async () => {
    setError('');
    setSubmitting(true);
    try {
      const { data } = await client.post('/api/orders/checkout');
      navigate(`/orders/${data.id}`, { replace: true });
    } catch (e) {
      setError(e.response?.data?.message || 'Checkout failed');
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <Loader />;

  const empty = !cart?.items?.length;

  return (
    <div className="page checkout-page">
      <h1>Checkout</h1>
      {empty ? (
        <p className="checkout-empty">
          Your cart is empty. <Link to="/">Shop</Link>
        </p>
      ) : (
        <div className="checkout-card">
          <h2>Order summary</h2>
          <ul className="checkout-lines">
            {cart.items.map((i) => (
              <li key={i.productId}>
                <span>
                  {i.productName} × {i.quantity}
                </span>
                <span>
                  {Number(i.lineTotal).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
                </span>
              </li>
            ))}
          </ul>
          <p className="checkout-total">
            Total:{' '}
            <strong>
              {Number(cart.total).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
            </strong>
          </p>
          {error && <div className="checkout-error">{error}</div>}
          <button type="button" className="btn btn-primary" onClick={placeOrder} disabled={submitting}>
            {submitting ? 'Placing order…' : 'Place order'}
          </button>
          <p className="checkout-note">Payment is simulated; your order is confirmed immediately.</p>
        </div>
      )}
    </div>
  );
}
