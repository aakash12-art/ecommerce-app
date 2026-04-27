import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { client } from '../api/client';
import CartItem from '../components/CartItem';
import Loader from '../components/Loader';
import './CartPage.css';

export default function Cart() {
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async () => {
    const { data } = await client.get('/api/cart');
    setCart(data);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        await load();
      } finally {
        setLoading(false);
      }
    })();
  }, [load]);

  if (loading) return <Loader />;

  const empty = !cart?.items?.length;

  return (
    <div className="page cart-page">
      <h1>Your cart</h1>
      {empty ? (
        <p className="cart-empty">
          Your cart is empty. <Link to="/">Continue shopping</Link>
        </p>
      ) : (
        <>
          <div className="cart-panel">
            {cart.items.map((item) => (
              <CartItem key={item.productId} item={item} onChanged={load} />
            ))}
          </div>
          <div className="cart-summary">
            <p className="cart-total">
              Total:{' '}
              <strong>
                {Number(cart.total).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
              </strong>
            </p>
            <Link to="/checkout" className="btn btn-primary">
              Proceed to checkout
            </Link>
          </div>
        </>
      )}
    </div>
  );
}
