export type UserRole = 'BORROWER' | 'LIBRARIAN';

export interface BorrowedBook {
  borrowId?: number;
  bookId?: number;
  bookTitle: string;
  borrowDate: string;
  dueDate: string;
}

export interface User {
  id?: number;
  userId?: number;
  username: string;
  email?: string;
  fullName?: string;
  phoneNumber?: string;
  phone?: string;
  role: UserRole;
  borrowingCount?: number;
  borrowedBooks?: BorrowedBook[];
}

export interface Category {
  id: number;
  categoryId?: number;
  code?: string;
  categoryCode?: string;
  name: string;
  categoryName?: string;
  description?: string;
  bookCount?: number;
}

export interface BookCopy {
  id: number;
  bookCopyId?: number;
  bookId: number;
  copyCode: string;
  assetCode?: string;
  status: 'AVAILABLE' | 'BORROWED' | 'MAINTENANCE' | 'LOST';
}

export interface Book {
  id: number;
  bookId?: number;
  code?: string;
  title: string;
  isbn?: string;
  author: string;
  publisher?: string;
  publishYear?: number;
  publishedYear?: number;
  categoryId?: number;
  categoryName?: string;
  totalCopies?: number;
  totalQuantity?: number;
  availableCopies?: number;
  availableQuantity?: number;
  description?: string;
  coverImage?: string;
  copies?: BookCopy[];
}

export interface BorrowRecord {
  id: number;
  borrowRecordId?: number;
  borrowCode: string;
  borrowerId: number;
  borrowerUsername?: string;
  borrowerName?: string;
  copyId?: number;
  bookCopyId?: number;
  copyCode?: string;
  assetCode?: string;
  bookId?: number;
  bookTitle?: string;
  borrowDate: string;
  dueDate: string;
  returnDate?: string | null;
  status: 'BORROWING' | 'RETURNED' | 'OVERDUE';
  fineAmount?: number;
  note?: string;
}

export interface NotificationItem {
  id: number;
  notificationId?: number;
  userId: number;
  title?: string;
  message: string;
  type?: string;
  read: boolean;
  isRead?: boolean;
  createdAt: string;
}

export interface AuthResponse {
  accessToken?: string;
  token?: string;
  refreshToken?: string;
  user?: User;
}

export interface ApiResponse<T> {
  code?: number;
  status?: number;
  message?: string;
  data?: T;
}
