import React, { useEffect, useState } from 'react';
import { client } from '../api/client';
import ProductCard from '../components/ProductCard';
import Loader from '../components/Loader';
import './Home.css';

export default function Home() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [categoryId, setCategoryId] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let cancelled = false;
    (async () => {
      try {
        const [pRes, cRes] = await Promise.all([
          client.get('/api/products', { params: categoryId ? { categoryId } : {} }),
          client.get('/api/categories'),
        ]);
        if (!cancelled) {
          setProducts(pRes.data);
          setCategories(cRes.data);
        }
      } catch (e) {
        if (!cancelled) setError(e.message || 'Failed to load products');
      } finally {
        if (!cancelled) setLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [categoryId]);

  if (loading) return <Loader />;

  return (
    <div className="page home-page">
      <section className="home-hero">
        <h1>Browse products</h1>
        <p>Filter by category or explore the full catalog.</p>
        <div className="home-filter">
          <label htmlFor="cat">Category</label>
          <select
            id="cat"
            value={categoryId}
            onChange={(e) => setCategoryId(e.target.value)}
          >
            <option value="">All</option>
            {categories.map((c) => (
              <option key={c.id} value={c.id}>
                {c.name}
              </option>
            ))}
          </select>
        </div>
      </section>
      {error && <p className="page-error">{error}</p>}
      <div className="product-grid">
        {products.map((p) => (
          <ProductCard key={p.id} product={p} />
        ))}
      </div>
      {!error && products.length === 0 && (
        <p className="page-empty">No products match this filter.</p>
      )}
    </div>
  );
}
