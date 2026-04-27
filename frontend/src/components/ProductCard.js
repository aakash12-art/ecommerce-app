import React from 'react';
import { Link } from 'react-router-dom';
import './ProductCard.css';

export default function ProductCard({ product }) {
  const { id, name, description, price, imageUrl, category } = product;
  const img = imageUrl || `https://picsum.photos/seed/p${id}/400/280`;

  return (
    <article className="product-card">
      <Link to={`/products/${id}`} className="product-card-link">
        <div className="product-card-image-wrap">
          <img src={img} alt="" className="product-card-image" loading="lazy" />
        </div>
        <div className="product-card-body">
          <p className="product-card-category">{category?.name}</p>
          <h3 className="product-card-title">{name}</h3>
          <p className="product-card-desc">{description}</p>
          <p className="product-card-price">
            {Number(price).toLocaleString(undefined, { style: 'currency', currency: 'USD' })}
          </p>
        </div>
      </Link>
    </article>
  );
}
