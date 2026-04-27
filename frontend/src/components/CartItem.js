import React, { useState } from 'react';
import { client } from '../api/client';
import './CartItem.css';

export default function CartItem({ item, onChanged }) {
  const { productId, productName, quantity, unitPrice, lineTotal } = item;
  const [qty, setQty] = useState(quantity);
  const [saving, setSaving] = useState(false);

  const saveQty = async (next) => {
    setSaving(true);
    try {
      await client.put(`/api/cart/items/${productId}`, { quantity: next });
      setQty(next);
      onChanged?.();
    } finally {
      setSaving(false);
    }
  };

  const remove = async () => {
    setSaving(true);
    try {
      await client.delete(`/api/cart/items/${productId}`);
      onChanged?.();
    } finally {
      setSaving(false);
    }
  };

  return (
    <div className="cart-item">
      <div className="cart-item-info">
        <h4 className="cart-item-name">{productName}</h4>
        <p className="cart-item-meta">
          {Number(unitPrice).toLocaleString(undefined, { style: 'currency', currency: 'USD' })} each
        </p>
      </div>
      <div className="cart-item-actions">
        <label className="cart-item-qty">
          Qty
          <input
            type="number"
            min="1"
            value={qty}
            disabled={saving}
            onChange={(e) => setQty(Number(e.target.value))}
            onBlur={() => {
              if (qty < 1) {
                setQty(quantity);
                return;
              }
              if (qty !== quantity) saveQty(qty);
            }}
          />
        </label>
        <p className="cart-item-line">
          {Number(lineTotal).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
        </p>
        <button type="button" className="btn btn-ghost btn-danger" disabled={saving} onClick={remove}>
          Remove
        </button>
      </div>
    </div>
  );
}
