import type { Book, Category, BorrowRecord, NotificationItem, User, UserRole, AuthResponse } from '../types';

const DEFAULT_API_URL = 'http://localhost:8080/api/v1';

export const getApiBaseUrl = (): string => {
  return localStorage.getItem('api_base_url') || DEFAULT_API_URL;
};

export const setApiBaseUrl = (url: string): void => {
  localStorage.setItem('api_base_url', url.trim());
};

export const getStoredToken = (): string | null => {
  return localStorage.getItem('access_token');
};

export const setStoredToken = (token: string): void => {
  localStorage.setItem('access_token', token);
};

export const removeStoredToken = (): void => {
  localStorage.removeItem('access_token');
  localStorage.removeItem('user_info');
};

export const decodeJWT = (token: string): Partial<User> | null => {
  try {
    const payloadBase64 = token.split('.')[1];
    const decodedJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
    const claims = JSON.parse(decodedJson);
    return {
      id: claims.userId || claims.id,
      username: claims.sub || claims.username,
      role: claims.roles || claims.role || 'BORROWER',
    };
  } catch {
    return null;
  }
};

/**
 * Generic HTTP fetch wrapper targeting Spring Boot REST API Backend Gateway
 * Automatically unwraps Spring Boot ApiResult<T> envelope: { code: 200, message: "...", data: T }
 */
export async function apiRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const baseUrl = getApiBaseUrl();
  const token = getStoredToken();

  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  let response: Response;
  try {
    response = await fetch(`${baseUrl}${endpoint}`, {
      ...options,
      headers,
    });
  } catch (netErr: unknown) {
    console.error('Network Error connecting to API Gateway:', netErr);
    throw new Error(
      `Không thể kết nối đến Máy chủ Backend (${baseUrl}). Vui lòng kiểm tra Docker / Spring Boot ApiGateway (cổng 8080) và AuthService!`
    );
  }

  if (!response.ok) {
    if (response.status === 401) {
      removeStoredToken();
      if (endpoint.includes('/login')) {
        throw new Error('Tên đăng nhập hoặc mật khẩu không chính xác!');
      }
      throw new Error('Phiên đăng nhập đã hết hạn hoặc chưa xác thực (401). Vui lòng đăng nhập lại!');
    }

    const errText = await response.text();
    let message = `Lỗi phản hồi từ máy chủ (${response.status})`;
    try {
      const errJson = JSON.parse(errText);
      message = errJson.message || errJson.error || message;
    } catch {
      if (errText) message = errText;
    }
    throw new Error(message);
  }

  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    const json = await response.json();
    // Unwrap Spring Boot ApiResult<T> envelope
    if (json && typeof json === 'object' && 'data' in json && json.data !== undefined) {
      return json.data as T;
    }
    return json as T;
  }
  return {} as T;
}

// --- Data Normalization Helpers ---
const normalizeBook = (b: Record<string, unknown>): Book => ({
  id: Number(b.bookId || b.id || Date.now()),
  bookId: Number(b.bookId || b.id || Date.now()),
  code: String(b.code || ''),
  title: String(b.title || ''),
  author: String(b.author || ''),
  publisher: String(b.publisher || ''),
  publishedYear: Number(b.publishedYear || b.publishYear || 2026),
  publishYear: Number(b.publishedYear || b.publishYear || 2026),
  isbn: String(b.isbn || ''),
  description: String(b.description || ''),
  categoryId: Number(b.categoryId || 0),
  categoryName: String(b.categoryName || ''),
  totalCopies: Number(b.totalQuantity ?? b.totalCopies ?? 0),
  totalQuantity: Number(b.totalQuantity ?? b.totalCopies ?? 0),
  availableCopies: Number(b.availableQuantity ?? b.availableCopies ?? 0),
  availableQuantity: Number(b.availableQuantity ?? b.availableCopies ?? 0),
  copies: Array.isArray(b.copies) ? (b.copies as Book['copies']) : [],
});

const normalizeCategory = (c: Record<string, unknown>): Category => ({
  id: Number(c.categoryId || c.id || Date.now()),
  categoryId: Number(c.categoryId || c.id || Date.now()),
  code: String(c.code || c.categoryCode || ''),
  categoryCode: String(c.code || c.categoryCode || ''),
  name: String(c.name || c.categoryName || ''),
  categoryName: String(c.name || c.categoryName || ''),
  description: String(c.description || ''),
  bookCount: Number(c.bookCount || 0),
});

const normalizeBorrow = (br: Record<string, unknown>): BorrowRecord => ({
  id: Number(br.borrowRecordId || br.id || Date.now()),
  borrowRecordId: Number(br.borrowRecordId || br.id || Date.now()),
  borrowCode: String(br.borrowCode || ''),
  borrowerId: Number(br.borrowerId || 0),
  borrowerUsername: String(br.borrowerUsername || ''),
  borrowerName: String(br.borrowerName || br.borrowerUsername || ''),
  copyId: Number(br.bookCopyId || br.copyId || 0),
  bookCopyId: Number(br.bookCopyId || br.copyId || 0),
  copyCode: String(br.assetCode || br.copyCode || ''),
  assetCode: String(br.assetCode || br.copyCode || ''),
  bookId: Number(br.bookId || 0),
  bookTitle: String(br.bookTitle || ''),
  borrowDate: String(br.borrowDate || ''),
  dueDate: String(br.dueDate || ''),
  returnDate: br.returnDate ? String(br.returnDate) : null,
  status: (br.status as BorrowRecord['status']) || 'BORROWING',
  fineAmount: Number(br.fineAmount || 0),
  note: String(br.note || ''),
});

const normalizeNotification = (n: Record<string, unknown>): NotificationItem => ({
  id: Number(n.notificationId || n.id || Date.now()),
  notificationId: Number(n.notificationId || n.id || Date.now()),
  userId: Number(n.userId || 0),
  title: String(n.title || 'Thông báo hệ thống'),
  message: String(n.message || ''),
  type: String(n.type || 'INFO'),
  read: Boolean(n.isRead ?? n.read ?? false),
  isRead: Boolean(n.isRead ?? n.read ?? false),
  createdAt: String(n.createdAt || n.sentAt || ''),
});

const normalizeUser = (u: Record<string, unknown>): User => ({
  id: Number(u.userId || u.id || Date.now()),
  userId: Number(u.userId || u.id || Date.now()),
  username: String(u.username || u.fullName || u.email || 'user'),
  email: String(u.email || ''),
  fullName: String(u.fullName || u.username || ''),
  phone: String(u.phone || u.phoneNumber || ''),
  phoneNumber: String(u.phone || u.phoneNumber || ''),
  role: (u.role as UserRole) || 'BORROWER',
  borrowingCount: Number(u.borrowingCount || (Array.isArray(u.borrowedBooks) ? u.borrowedBooks.length : 0)),
  borrowedBooks: Array.isArray(u.borrowedBooks) ? (u.borrowedBooks as User['borrowedBooks']) : [],
});

// --- AuthService Endpoints (/api/v1/auth) ---
export const authApi = {
  login: (data: { username: string; password: string }) =>
    apiRequest<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        username: data.username,
        password: data.password,
      }),
    }),
  register: (data: Record<string, unknown>) =>
    apiRequest<AuthResponse>('/auth/sign-up', {
      method: 'POST',
      body: JSON.stringify({
        username: data.username,
        email: data.email,
        password: data.password,
        passwordConfirm: data.passwordConfirm || data.password,
        fullName: data.fullName,
        phone: data.phone || '0912345678',
        role: data.role || 'BORROWER',
      }),
    }),
  logout: (refreshToken?: string) =>
    apiRequest<void>('/auth/logout', {
      method: 'POST',
      body: JSON.stringify({ refreshToken }),
    }),
  refreshToken: (refToken: string) =>
    apiRequest<AuthResponse>(`/auth/refresh-token/${refToken}`, {
      method: 'POST',
    }),
};

// --- UserService Endpoints (/api/v1/user) ---
export const userApi = {
  createUser: (data: { userId: number; email: string; fullName: string; phone: string }) =>
    apiRequest<User>('/user', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  getProfile: async (userId: number): Promise<User> => {
    const raw = await apiRequest<Record<string, unknown>>(`/user/${userId}`);
    return normalizeUser(raw);
  },
  updateUser: (userId: number, data: { fullName: string; phone: string }) =>
    apiRequest<User>(`/user/${userId}`, {
      method: 'PUT',
      body: JSON.stringify({
        fullName: data.fullName,
        phone: data.phone,
      }),
    }),
  searchUsers: async (keyword?: string): Promise<User[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>(
      `/user/search${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`
    );
    return Array.isArray(raw) ? raw.map(normalizeUser) : [];
  },
  getAllUsers: async (): Promise<User[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/user');
    return Array.isArray(raw) ? raw.map(normalizeUser) : [];
  },
  deleteUserInternal: (userId: number) =>
    apiRequest<void>(`/user/${userId}`, {
      method: 'DELETE',
    }),
  deleteUserAdmin: (userId: number) =>
    apiRequest<void>(`/user/admin/${userId}`, {
      method: 'DELETE',
    }),
};

// --- BookBorrowService Endpoints (/api/v1/books, /api/v1/categories, /api/v1/borrows) ---
export const bookApi = {
  getAll: async (): Promise<Book[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/books');
    return Array.isArray(raw) ? raw.map(normalizeBook) : [];
  },
  getById: async (id: number): Promise<Book> => {
    const raw = await apiRequest<Record<string, unknown>>(`/books/${id}`);
    return normalizeBook(raw);
  },
  search: async (title?: string, code?: string): Promise<Book[]> => {
    const params = new URLSearchParams();
    if (title) params.append('title', title);
    if (code) params.append('code', code);
    const raw = await apiRequest<Record<string, unknown>[]>(`/books/search?${params.toString()}`);
    return Array.isArray(raw) ? raw.map(normalizeBook) : [];
  },
  getByCategoryCode: async (categoryCode: string): Promise<Book[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>(`/books/by-category/${categoryCode}`);
    return Array.isArray(raw) ? raw.map(normalizeBook) : [];
  },
  create: (data: Partial<Book> & { code?: string }) =>
    apiRequest<Book>('/books', {
      method: 'POST',
      body: JSON.stringify({
        code: data.code || `BK-${Date.now()}`,
        title: data.title,
        categoryId: data.categoryId || 1,
        author: data.author,
        publisher: data.publisher,
        publishedYear: data.publishedYear || data.publishYear || 2026,
        isbn: data.isbn,
        description: data.description,
        initialQuantity: data.totalCopies || data.totalQuantity || 5,
      }),
    }),
  update: (id: number, data: Partial<Book> & { code?: string }) =>
    apiRequest<Book>(`/books/${id}`, {
      method: 'PUT',
      body: JSON.stringify({
        code: data.code || `BK-${id}`,
        title: data.title,
        categoryId: data.categoryId || 1,
        author: data.author,
        publisher: data.publisher,
        publishedYear: data.publishedYear || data.publishYear || 2026,
        isbn: data.isbn,
        description: data.description,
      }),
    }),
  delete: (id: number) =>
    apiRequest<void>(`/books/${id}`, {
      method: 'DELETE',
    }),
  importBooks: (bookId: number, quantity: number, note?: string) =>
    apiRequest<void>('/books/import', {
      method: 'POST',
      body: JSON.stringify({
        bookId,
        quantity,
        note: note || 'Nhập thêm bản sao sách từ giao diện web',
      }),
    }),
};

export const categoryApi = {
  getAll: async (): Promise<Category[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/categories');
    return Array.isArray(raw) ? raw.map(normalizeCategory) : [];
  },
  getById: async (id: number): Promise<Category> => {
    const raw = await apiRequest<Record<string, unknown>>(`/categories/${id}`);
    return normalizeCategory(raw);
  },
  create: (data: { code?: string; name: string; description?: string }) =>
    apiRequest<Category>('/categories', {
      method: 'POST',
      body: JSON.stringify({
        code: data.code || `CAT-${Date.now()}`,
        name: data.name,
        description: data.description,
      }),
    }),
  update: (id: number, data: { code?: string; name: string; description?: string }) =>
    apiRequest<Category>(`/categories/${id}`, {
      method: 'PUT',
      body: JSON.stringify({
        code: data.code || `CAT-${id}`,
        name: data.name,
        description: data.description,
      }),
    }),
  delete: (id: number) =>
    apiRequest<void>(`/categories/${id}`, {
      method: 'DELETE',
    }),
};

export const borrowApi = {
  getAll: async (): Promise<BorrowRecord[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/borrows');
    return Array.isArray(raw) ? raw.map(normalizeBorrow) : [];
  },
  getMyHistory: async (): Promise<BorrowRecord[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/borrows');
    return Array.isArray(raw) ? raw.map(normalizeBorrow) : [];
  },
  getById: async (id: number): Promise<BorrowRecord> => {
    const raw = await apiRequest<Record<string, unknown>>(`/borrows/${id}`);
    return normalizeBorrow(raw);
  },
  create: (data: { bookId: number; dueDate?: string; days?: number; borrowerId?: number; borrowerUsername?: string; note?: string }) => {
    const days = data.days || 14;
    const computedDueDate = data.dueDate || new Date(Date.now() + days * 86400000).toISOString().split('T')[0];
    return apiRequest<BorrowRecord>('/borrows', {
      method: 'POST',
      body: JSON.stringify({
        bookId: data.bookId,
        dueDate: computedDueDate,
        borrowerId: data.borrowerId,
        note: data.note || 'Phiếu mượn đăng ký qua Web Client',
      }),
    });
  },
  returnBook: (borrowCode: string, note?: string) =>
    apiRequest<BorrowRecord>('/borrows/return', {
      method: 'POST',
      body: JSON.stringify({
        borrowCode,
        note: note || 'Trả sách tại quầy thủ thư',
      }),
    }),
  scanOverdue: () =>
    apiRequest<{ scannedCount: number }>('/borrows/scan-overdue', {
      method: 'POST',
    }),
  getActiveBorrowsByUserIds: (userIds: number[]) =>
    apiRequest<Record<number, unknown[]>>('/borrows/active-by-users', {
      method: 'POST',
      body: JSON.stringify(userIds),
    }),
};

// --- NotificationService Endpoints (/api/v1/notifications) ---
export const notificationApi = {
  getMyNotifications: async (): Promise<NotificationItem[]> => {
    const raw = await apiRequest<Record<string, unknown>[]>('/notifications');
    return Array.isArray(raw) ? raw.map(normalizeNotification) : [];
  },
  markAsRead: (id: number) =>
    apiRequest<void>(`/notifications/${id}/read`, {
      method: 'PUT',
    }),
};
