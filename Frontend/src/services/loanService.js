import api from './api';

export const requestLoan = async (amount, termMonths) => {
  const response = await api.post('/loans', { amount, termMonths });
  return response.data;
};

export const getMyLoans = async () => {
  const response = await api.get('/loans/my-loans');
  return response.data;
};

export const getAllLoans = async (status) => {
  const params = status ? { status } : {};
  const response = await api.get('/loans', { params });
  return response.data;
};

export const approveLoan = async (id) => {
  const response = await api.put(`/loans/${id}/approve`);
  return response.data;
};

export const rejectLoan = async (id) => {
  const response = await api.put(`/loans/${id}/reject`);
  return response.data;
};
