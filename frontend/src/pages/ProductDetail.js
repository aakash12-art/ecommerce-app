import React, { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { client } from '../api/client';
import { useAuth } from '../context/AuthContext';
import Loader from '../components/Loader';
import './ProductDetail.css';

export default function ProductDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [product, setProduct] = useState(null);
  const [loading, setLoading] = useState(true);
  const [qty, setQty] = useState(1);
  const [message, setMessage] = useState('');
  const [adding, setAdding] = useState(false);

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const { data } = await client.get(`/api/products/${id}`);
        if (!cancelled) setProduct(data);
      } catch {
        if (!cancelled) setProduct(null);
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [id]);

  const addToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: { pathname: `/products/${id}` } } });
      return;
    }
    setMessage('');
    setAdding(true);
    try {
      await client.post('/api/cart/items', { productId: Number(id), quantity: qty });
      setMessage('Added to cart.');
    } catch (e) {
      setMessage(e.response?.data?.message || 'Could not add to cart');
    } finally {
      setAdding(false);
    }
  };

  if (loading) return <Loader />;
  if (!product) {
    return (
      <div className="page product-detail">
        <p>Product not found.</p>
        <Link to="/">Back to shop</Link>
      </div>
    );
  }

  const img = product.imageUrl || `https://picsum.photos/seed/p${product.id}/600/450`;

  return (
    <div className="page product-detail">
      <Link to="/" className="back-link">
        ← Back to products
      </Link>
      <div className="product-detail-grid">
        <div className="product-detail-image-wrap">
          <img src={img} alt="" className="product-detail-image" />
        </div>
        <div>
          <p className="product-detail-category">{product.category?.name}</p>
          <h1>{product.name}</h1>
          <p className="product-detail-price">
            {Number(product.price).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
          </p>
          <p className="product-detail-desc">{product.description}</p>
          <div className="product-detail-actions">
            <label>
              Quantity
              <input
                type="number"
                min="1"
                value={qty}
                onChange={(e) => setQty(Math.max(1, Number(e.target.value)))}
              />
            </label>
            <button type="button" className="btn btn-primary" onClick={addToCart} disabled={adding}>
              {adding ? 'Adding…' : isAuthenticated ? 'Add to cart' : 'Sign in to add'}
            </button>
          </div>
          {message && <p className="product-detail-msg">{message}</p>}
        </div>
      </div>
    </div>
  );
}
