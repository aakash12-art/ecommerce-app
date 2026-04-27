import React from 'react';
import './Loader.css';

/** Full-page or inline loading indicator. */
export default function Loader({ label = 'Loading…', inline = false }) {
  return (
    <div className={`loader-wrap ${inline ? 'loader-wrap--inline' : ''}`}>
      <div className="loader-spinner" aria-hidden />
      {label && <p className="loader-label">{label}</p>}
    </div>
  );
}
