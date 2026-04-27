import React, { useCallback, useEffect, useState } from 'react';
import { client } from '../api/client';
import Loader from '../components/Loader';
import './AdminDashboard.css';

const emptyProduct = {
  id: null,
  name: '',
  description: '',
  price: '',
  imageUrl: '',
  categoryId: '',
};

export default function AdminDashboard() {
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [productForm, setProductForm] = useState(emptyProduct);
  const [categoryName, setCategoryName] = useState('');
  const [error, setError] = useState('');
  const [saving, setSaving] = useState(false);

  const load = useCallback(async () => {
    const [pRes, cRes] = await Promise.all([
      client.get('/api/products'),
      client.get('/api/categories'),
    ]);
    setProducts(pRes.data);
    setCategories(cRes.data);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        await load();
      } catch (e) {
        setError(e.response?.data?.message || 'Failed to load admin data');
      } finally {
        setLoading(false);
      }
    })();
  }, [load]);

  const editProduct = (p) => {
    setProductForm({
      id: p.id,
      name: p.name,
      description: p.description || '',
      price: String(p.price),
      imageUrl: p.imageUrl || '',
      categoryId: String(p.category?.id || ''),
    });
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const resetProductForm = () => setProductForm(emptyProduct);

  const saveProduct = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    const payload = {
      name: productForm.name.trim(),
      description: productForm.description,
      price: Number(productForm.price),
      imageUrl: productForm.imageUrl || null,
      categoryId: Number(productForm.categoryId),
    };
    try {
      if (productForm.id) {
        await client.put(`/api/products/${productForm.id}`, payload);
      } else {
        await client.post('/api/products', payload);
      }
      resetProductForm();
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Save failed');
    } finally {
      setSaving(false);
    }
  };

  const deleteProduct = async (id) => {
    if (!window.confirm('Delete this product?')) return;
    setError('');
    try {
      await client.delete(`/api/products/${id}`);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Delete failed');
    }
  };

  const addCategory = async (e) => {
    e.preventDefault();
    if (!categoryName.trim()) return;
    setSaving(true);
    setError('');
    try {
      await client.post('/api/categories', { name: categoryName.trim() });
      setCategoryName('');
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Could not create category');
    } finally {
      setSaving(false);
    }
  };

  const deleteCategory = async (id) => {
    if (!window.confirm('Delete category? Products using it may need to be reassigned first.')) return;
    setError('');
    try {
      await client.delete(`/api/categories/${id}`);
      await load();
    } catch (err) {
      setError(err.response?.data?.message || 'Delete failed');
    }
  };

  if (loading) return <Loader />;

  return (
    <div className="page admin-page">
      <h1>Admin dashboard</h1>
      <p className="admin-lead">Manage catalog and product categories.</p>
      {error && <div className="admin-error">{error}</div>}

      <section className="admin-section">
        <h2>Products</h2>
        <form className="admin-form" onSubmit={saveProduct}>
          <h3>{productForm.id ? 'Edit product' : 'Add product'}</h3>
          <div className="admin-form-grid">
            <label>
              Name
              <input
                value={productForm.name}
                onChange={(ev) => setProductForm({ ...productForm, name: ev.target.value })}
                required
              />
            </label>
            <label>
              Price
              <input
                type="number"
                step="0.01"
                min="0.01"
                value={productForm.price}
                onChange={(ev) => setProductForm({ ...productForm, price: ev.target.value })}
                required
              />
            </label>
            <label className="admin-span-2">
              Category
              <select
                value={productForm.categoryId}
                onChange={(ev) => setProductForm({ ...productForm, categoryId: ev.target.value })}
                required
              >
                <option value="">Select…</option>
                {categories.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.name}
                  </option>
                ))}
              </select>
            </label>
            <label className="admin-span-2">
              Image URL
              <input
                value={productForm.imageUrl}
                onChange={(ev) => setProductForm({ ...productForm, imageUrl: ev.target.value })}
                placeholder="https://…"
              />
            </label>
            <label className="admin-span-2">
              Description
              <textarea
                rows={3}
                value={productForm.description}
                onChange={(ev) => setProductForm({ ...productForm, description: ev.target.value })}
              />
            </label>
          </div>
          <div className="admin-form-actions">
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {productForm.id ? 'Update product' : 'Create product'}
            </button>
            {productForm.id && (
              <button type="button" className="btn btn-ghost" onClick={resetProductForm}>
                Cancel edit
              </button>
            )}
          </div>
        </form>

        <div className="admin-table-wrap">
          <table className="admin-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Category</th>
                <th>Price</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id}>
                  <td>{p.name}</td>
                  <td>{p.category?.name}</td>
                  <td>
                    {Number(p.price).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
                  </td>
                  <td className="admin-table-actions">
                    <button type="button" className="btn btn-ghost" onClick={() => editProduct(p)}>
                      Edit
                    </button>
                    <button
                      type="button"
                      className="btn btn-ghost btn-danger"
                      onClick={() => deleteProduct(p.id)}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section className="admin-section">
        <h2>Categories</h2>
        <form className="admin-inline" onSubmit={addCategory}>
          <input
            value={categoryName}
            onChange={(e) => setCategoryName(e.target.value)}
            placeholder="New category name"
          />
          <button type="submit" className="btn btn-primary" disabled={saving}>
            Add
          </button>
        </form>
        <ul className="admin-category-list">
          {categories.map((c) => (
            <li key={c.id}>
              <span>{c.name}</span>
              <button type="button" className="btn btn-ghost btn-danger" onClick={() => deleteCategory(c.id)}>
                Delete
              </button>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
