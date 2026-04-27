import { render, screen } from '@testing-library/react';
import App from './App';

test('renders shop brand', () => {
  render(<App />);
  expect(screen.getByText(/shop/i)).toBeInTheDocument();
});
